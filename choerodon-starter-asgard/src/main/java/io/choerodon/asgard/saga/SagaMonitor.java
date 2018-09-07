package io.choerodon.asgard.saga;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.asgard.AsgardApplicationContextHelper;
import io.choerodon.asgard.UpdateTaskInstanceStatusDTO;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.asgard.saga.dto.PollBatchDTO;
import io.choerodon.asgard.saga.dto.PollCodeDTO;
import io.choerodon.asgard.saga.dto.SagaTaskInstanceDTO;
import io.choerodon.asgard.saga.feign.SagaMonitorClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static io.choerodon.asgard.InstanceCommonUtils.getErrorInfoFromException;
import static io.choerodon.asgard.InstanceCommonUtils.resultToJson;

public class SagaMonitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SagaMonitor.class);

    private final ChoerodonSagaProperties choerodonSagaProperties;

    private final SagaMonitorClient sagaMonitorClient;

    private final Executor executor;

    static final Map<String, SagaTaskInvokeBean> invokeBeanMap = new HashMap<>();

    private static Boolean enabledDbRecord = false;

    private final DataSourceTransactionManager transactionManager;

    private final Environment environment;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ScheduledExecutorService scheduledExecutorService;

    private Set<SagaTaskInstanceDTO> msgQueue;

    private Set<Long> records = Collections.synchronizedSet(new LinkedHashSet<>());

    private final AsgardApplicationContextHelper asgardApplicationContextHelper;

    private final SagaTaskInstanceStore taskInstanceStore;

    public SagaMonitor(ChoerodonSagaProperties choerodonSagaProperties,
                       SagaMonitorClient sagaMonitorClient,
                       Executor executor,
                       DataSourceTransactionManager transactionManager,
                       Environment environment,
                       SagaTaskInstanceStore taskInstanceStore,
                       AsgardApplicationContextHelper asgardApplicationContextHelper) {
        this.choerodonSagaProperties = choerodonSagaProperties;
        this.sagaMonitorClient = sagaMonitorClient;
        this.executor = executor;
        this.transactionManager = transactionManager;
        this.environment = environment;
        this.asgardApplicationContextHelper = asgardApplicationContextHelper;
        this.taskInstanceStore = taskInstanceStore;
        msgQueue = Collections.synchronizedSet(new LinkedHashSet<>(choerodonSagaProperties.getMaxPollSize()));
    }

    public void setScheduledExecutorService(ScheduledExecutorService scheduledExecutorService) {
        this.scheduledExecutorService = scheduledExecutorService;
    }

    static void setEnabledDbRecordTrue() {
        SagaMonitor.enabledDbRecord = true;
    }

    @PostConstruct
    private void start() {
        List<PollCodeDTO> codeDTOS = invokeBeanMap.entrySet().stream().map(t -> new PollCodeDTO(t.getValue().sagaTask.sagaCode(),
                t.getValue().sagaTask.code())).collect(Collectors.toList());
        final int maxPollSize = choerodonSagaProperties.getMaxPollSize();
        try {
            String instance = InetAddress.getLocalHost().getHostAddress() + ":" + environment.getProperty("server.port");
            LOGGER.info("sagaMonitor prepare to start saga consumer, pollTasks {}, instance {}, maxPollSize {}, ", codeDTOS, instance, maxPollSize);
            scheduledExecutorService.scheduleWithFixedDelay(() -> {
                boolean noNeedUpdateSagaStatus = noNeedUpdateSagaStatus();
                if (noNeedUpdateSagaStatus && msgQueue.isEmpty()) {
                    try {
                        List<SagaTaskInstanceDTO> pollMessages = sagaMonitorClient.pollBatch(new PollBatchDTO(instance, codeDTOS, maxPollSize));
                        LOGGER.debug("sagaMonitor polled messages, size {} data {}", pollMessages.size(), pollMessages);
                        msgQueue.addAll(pollMessages);
                        msgQueue.forEach(t -> executor.execute(new InvokeTask(t)));
                    } catch (Exception e) {
                        LOGGER.warn("sagaMonitor poll error {}", e.getMessage());
                    }
                } else {
                    LOGGER.debug("sagaMonitor skip poll, dbRecordNotEmpty {}, msgQueue {}", noNeedUpdateSagaStatus, msgQueue);
                }
            }, 20, choerodonSagaProperties.getPollIntervalMs(), TimeUnit.MILLISECONDS);
        } catch (UnknownHostException e) {
            LOGGER.error("sagaMonitor can't get localhost, failed to start saga consumer. {}", e.getCause());
        }
    }

    private boolean noNeedUpdateSagaStatus() {
        if (enabledDbRecord) {
            if (!records.isEmpty()) {
                return false;
            }
            if (msgQueue.isEmpty()) {
                records.addAll(taskInstanceStore.selectOvertimeTaskInstance());
                if (!records.isEmpty()) {
                    records.forEach(i -> executor.execute(new UpdateStatusFailedTask(i)));
                    return false;
                }
            }
        }
        return true;
    }

    private class UpdateStatusFailedTask implements Runnable {

        private final long taskInstanceId;

        UpdateStatusFailedTask(long taskInstanceId) {
            this.taskInstanceId = taskInstanceId;
        }

        @Override
        public void run() {
            try {
                sagaMonitorClient.updateStatus(taskInstanceId, new UpdateTaskInstanceStatusDTO(taskInstanceId,
                        SagaDefinition.TaskInstanceStatus.FAILED.name(), null, "error.SagaMonitor.updateStatusFailed"));
                taskInstanceStore.removeTaskInstance(taskInstanceId);
            } catch (Exception e) {
                LOGGER.warn("error.SagaMonitor.updateStatusFailed.reRun, {}", e);
            } finally {
                records.remove(taskInstanceId);
            }
        }
    }

    private class InvokeTask implements Runnable {

        private final SagaTaskInstanceDTO dto;

        private final SagaTaskInvokeBean invokeBean;

        private final SagaTask sagaTask;

        InvokeTask(SagaTaskInstanceDTO dto) {
            this.dto = dto;
            this.invokeBean = invokeBeanMap.get(dto.getSagaCode() + dto.getTaskCode());
            sagaTask = invokeBean.sagaTask;
        }

        @Override
        public void run() {
            try {
                invoke(dto);
            } catch (Exception e) {
                LOGGER.error("sagaMonitor consume message error, cause {}", e);
            } finally {
                msgQueue.remove(dto);
            }
        }

        private void invoke(SagaTaskInstanceDTO data) {
            if (sagaTask.enabledDbRecord()) {
                taskInstanceStore.storeTaskInstance(data.getId());
            }
            PlatformTransactionManager platformTransactionManager;
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
            def.setReadOnly(sagaTask.transactionReadOnly());
            def.setIsolationLevel(sagaTask.transactionIsolation().value());
            def.setTimeout(sagaTask.transactionTimeout());
            String transactionManagerName = sagaTask.transactionManager();
            if (StringUtils.isEmpty(transactionManagerName)) {
                platformTransactionManager = transactionManager;
            } else {
                platformTransactionManager = asgardApplicationContextHelper.getSpringFactory()
                        .getBean(transactionManagerName, PlatformTransactionManager.class);
            }
            TransactionStatus status = platformTransactionManager.getTransaction(def);
            try {
                invokeBean.method.setAccessible(true);
                final Object result = invokeBean.method.invoke(invokeBean.object, data.getInput());
                sagaMonitorClient.updateStatus(data.getId(), new UpdateTaskInstanceStatusDTO(data.getId(),
                        SagaDefinition.TaskInstanceStatus.COMPLETED.name(), resultToJson(result, objectMapper), null));
                if (sagaTask.enabledDbRecord()) {
                    taskInstanceStore.removeTaskInstance(data.getId());
                }
                platformTransactionManager.commit(status);
            } catch (Exception e) {
                platformTransactionManager.rollback(status);
                String errorMsg = getErrorInfoFromException(e);
                LOGGER.warn("sagaMonitor invoke method error, transaction rollback, msg {}, cause {}", data, errorMsg);
                sagaMonitorClient.updateStatus(data.getId(), new UpdateTaskInstanceStatusDTO(data.getId(),
                        SagaDefinition.TaskInstanceStatus.FAILED.name(), null, errorMsg));
                if (sagaTask.enabledDbRecord()) {
                    taskInstanceStore.removeTaskInstance(data.getId());
                }
            }
        }
    }

}
