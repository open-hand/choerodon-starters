package io.choerodon.asgard.schedule;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.asgard.AsgardApplicationContextHelper;
import io.choerodon.asgard.UpdateTaskInstanceStatusDTO;
import io.choerodon.asgard.schedule.annotation.JobTask;
import io.choerodon.asgard.schedule.dto.ScheduleInstanceConsumerDTO;
import io.choerodon.asgard.schedule.feign.ScheduleMonitorClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static io.choerodon.asgard.InstanceCommonUtils.getErrorInfoFromException;
import static io.choerodon.asgard.InstanceCommonUtils.resultToJson;

@Slf4j
public class ScheduleMonitor {

    private static final Map<String, JobTaskInvokeBean> invokeBeanMap = new HashMap<>();

    private final DataSourceTransactionManager transactionManager;

    private final Environment environment;

    private final Executor executor;

    private final ScheduleMonitorClient scheduleMonitorClient;

    private final AsgardApplicationContextHelper applicationContextHelper;

    private final ScheduledExecutorService scheduledExecutorService;

    private Set<ScheduleInstanceConsumerDTO> msgQueue;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final long pollIntervalMs;

    static void addInvokeBean(String key, JobTaskInvokeBean invokeBean) {
        invokeBeanMap.put(key, invokeBean);
    }

    public ScheduleMonitor(DataSourceTransactionManager transactionManager, Environment environment,
                           Executor executor, ScheduleMonitorClient scheduleMonitorClient,
                           AsgardApplicationContextHelper applicationContextHelper,
                           ScheduledExecutorService scheduledExecutorService,
                           long pollIntervalMs) {
        this.transactionManager = transactionManager;
        this.environment = environment;
        this.executor = executor;
        this.scheduleMonitorClient = scheduleMonitorClient;
        this.applicationContextHelper = applicationContextHelper;
        this.scheduledExecutorService = scheduledExecutorService;
        this.pollIntervalMs = pollIntervalMs;
        msgQueue = Collections.synchronizedSet(new LinkedHashSet<>(1 << 5));
    }

    @PostConstruct
    public void start() {
        final Set<String> pollMethods = invokeBeanMap.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toSet());
        try {
            final String instance = InetAddress.getLocalHost().getHostAddress() + ":" + environment.getProperty("server.port");
            log.info("scheduleMonitor prepare to start schedule consumer, methods {}, instance {}", pollMethods, instance);
            scheduledExecutorService.scheduleWithFixedDelay(() -> {
                if (msgQueue.isEmpty()) {
                    try {
                        List<ScheduleInstanceConsumerDTO> pollMessages = scheduleMonitorClient.pollBatch(pollMethods, instance);
                        log.debug("scheduleMonitor polled messages, size {} data {}", pollMessages.size(), pollMessages);
                        msgQueue.addAll(pollMessages);
                        msgQueue.forEach(t -> executor.execute(new ScheduleMonitor.InvokeTask(t)));
                    } catch (Exception e) {
                        log.warn("scheduleMonitor poll error {}", e.getMessage());
                    }
                } else {
                    log.debug("scheduleMonitor skip poll, dbRecordNotEmpty {}, msgQueue {}", msgQueue);
                }
            }, 20, pollIntervalMs, TimeUnit.MILLISECONDS);
        } catch (UnknownHostException e) {
            log.error("scheduleMonitor can't get localhost, failed to start schedule consumer. {}", e.getCause());
        }
    }

    private class InvokeTask implements Runnable {

        private final ScheduleInstanceConsumerDTO dto;

        private final JobTaskInvokeBean invokeBean;

        private final JobTask jobTask;

        InvokeTask(ScheduleInstanceConsumerDTO dto) {
            this.dto = dto;
            this.invokeBean = invokeBeanMap.get(dto.getMethod());
            jobTask = invokeBean.jobTask;
        }

        @Override
        public void run() {
            try {
                invoke(dto);
            } catch (Exception e) {
                log.error("scheduleMonitor consume message error, cause {}", e);
            } finally {
                msgQueue.remove(dto);
            }
        }

        private void invoke(ScheduleInstanceConsumerDTO data) {
            PlatformTransactionManager platformTransactionManager;
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
            def.setReadOnly(jobTask.transactionReadOnly());
            def.setIsolationLevel(jobTask.transactionIsolation().value());
            def.setTimeout(jobTask.transactionTimeout());
            String transactionManagerName = jobTask.transactionManager();
            if (StringUtils.isEmpty(transactionManagerName)) {
                platformTransactionManager = transactionManager;
            } else {
                platformTransactionManager = applicationContextHelper.getSpringFactory()
                        .getBean(transactionManagerName, PlatformTransactionManager.class);
            }
            TransactionStatus status = platformTransactionManager.getTransaction(def);
            try {
                invokeBean.method.setAccessible(true);
                Object result = invokeBean.method.invoke(invokeBean.object, getInputMap(data.getExecuteParams()));
                if (result != null) {
                    result = objectMapper.writeValueAsString(result);
                }
                scheduleMonitorClient.updateStatus(data.getId(), new UpdateTaskInstanceStatusDTO(data.getId(),
                        QuartzDefinition.InstanceStatus.COMPLETED.name(), resultToJson(result, objectMapper), null, data.getObjectVersionNumber()));
                platformTransactionManager.commit(status);
            } catch (Exception e) {
                platformTransactionManager.rollback(status);
                String errorMsg = getErrorInfoFromException(e);
                log.warn("scheduleMonitor invoke method error, transaction rollback, msg {}, cause {}", data, errorMsg);
                scheduleMonitorClient.updateStatus(data.getId(), new UpdateTaskInstanceStatusDTO(data.getId(),
                        QuartzDefinition.InstanceStatus.FAILED.name(), null, errorMsg, data.getObjectVersionNumber()));
            }
        }

        private Map<String, Object> getInputMap(final String jsonMap) throws IOException {
            if (StringUtils.isEmpty(jsonMap)) {
                return new HashMap<>();
            }
            return objectMapper.readValue(jsonMap, new TypeReference<Map<String, Object>>() {});
        }
    }

}
