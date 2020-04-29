package io.choerodon.asgard.schedule;


import com.fasterxml.jackson.core.type.TypeReference;

import io.choerodon.asgard.common.AbstractAsgardConsumer;
import io.choerodon.asgard.common.ApplicationContextHelper;
import io.choerodon.asgard.common.UpdateStatusDTO;
import io.choerodon.asgard.schedule.annotation.JobTask;
import io.choerodon.asgard.schedule.dto.PollScheduleInstanceDTO;
import io.choerodon.asgard.schedule.dto.ScheduleInstanceConsumerDTO;
import io.choerodon.asgard.schedule.feign.ScheduleConsumerClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

import static io.choerodon.asgard.common.InstanceResultUtils.getErrorInfoFromException;
import static io.choerodon.asgard.common.InstanceResultUtils.getLoggerException;
import static io.choerodon.asgard.common.InstanceResultUtils.resultToJson;

public class ScheduleConsumer extends AbstractAsgardConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleConsumer.class);

    private static final Map<String, JobTaskInvokeBean> invokeBeanMap = new HashMap<>();

    private ScheduleConsumerClient scheduleConsumerClient;

    private PollScheduleInstanceDTO pollScheduleInstanceDTO;

    public ScheduleConsumer(String service, String instance, PlatformTransactionManager transactionManager,
                            Executor executor, ScheduledExecutorService schedule,
                            ApplicationContextHelper contextHelper, long pollIntervalMs) {
        super(service, instance, transactionManager, executor, schedule, contextHelper, pollIntervalMs);
    }

    public void setScheduleConsumerClient(ScheduleConsumerClient scheduleConsumerClient) {
        this.scheduleConsumerClient = scheduleConsumerClient;
    }

    private PollScheduleInstanceDTO getPollScheduleInstanceDTO() {
        if (pollScheduleInstanceDTO == null) {
            Set<String> pollMethods = invokeBeanMap.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toSet());
            pollScheduleInstanceDTO = new PollScheduleInstanceDTO(pollMethods, instance, service, runningTasks);
        }
        return pollScheduleInstanceDTO;
    }


    @Override
    protected void scheduleRunning(String instance) {
        scheduleConsumerClient.pollBatch(getPollScheduleInstanceDTO()).forEach(t -> {
            LOGGER.trace("ScheduleConsumer polled scheduleTaskInstances: {}", t);
            runningTasks.add(t.getId());
            CompletableFuture.supplyAsync(() -> invoke(t), executor)
                    .exceptionally(ex -> {
                        LOGGER.warn("@JobTask method: {}, id: {} supplyAsync failed", t.getMethod(), t.getId(), ex);
                        return null;
                    });
        });
    }

    private ScheduleInstanceConsumerDTO invoke(final ScheduleInstanceConsumerDTO data) {
        final JobTaskInvokeBean invokeBean = invokeBeanMap.get(data.getMethod());
        final JobTask jobTask = invokeBean.jobTask;
        PlatformTransactionManager platformTransactionManager = getSagaTaskTransactionManager(jobTask.transactionManager());
        TransactionStatus status = createTransactionStatus(transactionManager, jobTask.transactionIsolation().value());
        beforeInvoke(data.getUserDetails());
        try {
            invokeBean.method.setAccessible(true);
            Object result = invokeBean.method.invoke(invokeBean.object, getInputMap(data.getExecuteParams()));
            if (result != null) {
                result = objectMapper.writeValueAsString(result);
            }
            scheduleConsumerClient.updateStatus(data.getId(), new UpdateStatusDTO(data.getId(), QuartzDefinition.InstanceStatus.COMPLETED.name(),
                    resultToJson(result, objectMapper), null, data.getObjectVersionNumber()));
            platformTransactionManager.commit(status);
            runningTasks.remove(data.getId());
        } catch (Exception e) {
            String errorMsg = getErrorInfoFromException(e);
            LOGGER.info("@JobTask method: {}, id: {} invoke error", data.getMethod(), data.getId(), getLoggerException(e));
            invokeError(platformTransactionManager, status, data, errorMsg);
        } finally {
            afterInvoke();
        }
        return data;
    }

    private void invokeError(final PlatformTransactionManager platformTransactionManager,
                             final TransactionStatus status,
                             final ScheduleInstanceConsumerDTO data,
                             final String errorMsg) {
        try {
            platformTransactionManager.rollback(status);
        } catch (Exception e) {
            LOGGER.warn("@JobTask method: {}, id: {} transaction rollback error", data.getMethod(), data.getId(), e);
        } finally {
            try {
                scheduleConsumerClient.updateStatus(data.getId(), new UpdateStatusDTO(data.getId(),
                        QuartzDefinition.InstanceStatus.FAILED.name(), null, errorMsg, data.getObjectVersionNumber()));
                runningTasks.remove(data.getId());
            } catch (Exception ex) {
                LOGGER.warn("@JobTask method: {}, id: {} updateStatusFailed error, error message: {}", data.getMethod(), data.getId(), ex.getMessage());
                runningTasks.remove(data.getId());
            }
        }
    }

    private Map<String, Object> getInputMap(final String jsonMap) throws IOException {
        if (StringUtils.isEmpty(jsonMap)) {
            return new HashMap<>();
        }
        return objectMapper.readValue(jsonMap, new TypeReference<Map<String, Object>>() {
        });
    }

    static void addInvokeBean(String key, JobTaskInvokeBean invokeBean) {
        invokeBeanMap.put(key, invokeBean);
    }

}
