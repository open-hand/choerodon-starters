package io.choerodon.asgard.saga.consumer;

import io.choerodon.asgard.common.*;
import io.choerodon.asgard.saga.SagaDefinition;
import io.choerodon.asgard.saga.SagaProperties;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.asgard.saga.dto.PollSagaTaskInstanceDTO;
import io.choerodon.asgard.saga.dto.SagaTaskInstanceDTO;
import io.choerodon.asgard.saga.feign.SagaConsumerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;

import static io.choerodon.asgard.common.InstanceResultUtils.getErrorInfoFromException;
import static io.choerodon.asgard.common.InstanceResultUtils.resultToJson;

public class SagaConsumer extends AbstractAsgardConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(SagaConsumer.class);

    static final Map<String, SagaTaskInvokeBean> invokeBeanMap = new HashMap<>();

    private SagaConsumerClient consumerClient;

    private PollSagaTaskInstanceDTO pollDTO;

    private SagaProperties properties;

    public SagaConsumer(String service, String instance, PlatformTransactionManager transactionManager,
                        Executor executor, ScheduledExecutorService scheduledExecutorService,
                        ApplicationContextHelper contextHelper, long pollIntervalMs) {
        super(service, instance, transactionManager, executor, scheduledExecutorService, contextHelper, pollIntervalMs);
    }

    public void setConsumerClient(SagaConsumerClient consumerClient) {
        this.consumerClient = consumerClient;
    }

    public void setProperties(SagaProperties properties) {
        this.properties = properties;
    }

    @Override
    public void scheduleRunning(String instance) {
        consumerClient.pollBatch(getPollDTO()).forEach(t -> {
            LOGGER.debug("sagaConsumer polled message: {}", t);
            runningTasks.add(t.getId());
            CompletableFuture.supplyAsync(() -> invoke(t), executor)
                    .exceptionally(ex -> {
                        if (ex instanceof UpdateStatusException) {
                            LOGGER.debug("sagaConsumer update status failed, prepare to retry, instanceId: {}", ((UpdateStatusException) ex).id);
                            CompletableFuture.supplyAsync(() -> retryUpdateStatusFailed(((UpdateStatusException) ex).id), executor)
                                    .thenAccept(j -> LOGGER.debug("sagaConsumer auto update status success, id: {}", ((UpdateStatusException) ex).id));
                        }
                        return null;
                    })
                    .thenAccept(i -> runningTasks.remove(i.getId()));
        });
    }

    private PollSagaTaskInstanceDTO getPollDTO() {
        if (pollDTO == null) {
            pollDTO = new PollSagaTaskInstanceDTO(instance, service, this.properties.getConsumer().getMaxPollSize());
        }
        return pollDTO;
    }

    private Long retryUpdateStatusFailed(Long id) {
        while (true) {
            try {
                Thread.sleep(200);
                SagaTaskInstanceDTO dto = consumerClient.queryStatus(id);
                if (dto == null) {
                    LOGGER.error("error.sagaConsumer.retryUpdateStatusFailed, id: {}", id);
                    return id;
                }
                consumerClient.retryUpdateStatus(id, UpdateStatusDTO.UpdateStatusDTOBuilder.newInstance()
                        .withStatus(SagaDefinition.TaskInstanceStatus.FAILED.name())
                        .withExceptionMessage("sagaConsumer update status failed")
                        .withId(id)
                        .withObjectVersionNumber(dto.getObjectVersionNumber()).build());
                break;
            } catch (QueryStatusException | UpdateStatusException e) {
                LOGGER.info("error.sagaConsumer.retryUpdate id: {}", id);
            } catch (InterruptedException e) {
                runningTasks.remove(id);
                Thread.currentThread().interrupt();
                LOGGER.error("error.sagaConsumer.retryUpdateThreadInterrupted", e);
            }
        }
        return id;
    }


    /**
     * 执行@SagaTask注解的方法
     */
    private SagaTaskInstanceDTO invoke(final SagaTaskInstanceDTO data) {
        final SagaTaskInvokeBean invokeBean = invokeBeanMap.get(data.getSagaCode() + data.getTaskCode());
        final SagaTask sagaTask = invokeBean.sagaTask;
        PlatformTransactionManager platformTransactionManager = getSagaTaskTransactionManager(sagaTask.transactionManager());
        TransactionStatus status = createTransactionStatus(transactionManager, sagaTask.transactionIsolation().value());
        beforeInvoke(data.getUserDetails());
        try {
            invokeBean.method.setAccessible(true);
            final Object result = invokeBean.method.invoke(invokeBean.object, data.getInput());
            consumerClient.updateStatus(data.getId(),
                    UpdateStatusDTO.UpdateStatusDTOBuilder.newInstance()
                            .withStatus(SagaDefinition.TaskInstanceStatus.COMPLETED.name())
                            .withOutput(resultToJson(result, objectMapper))
                            .withId(data.getId())
                            .withObjectVersionNumber(data.getObjectVersionNumber()).build());
            platformTransactionManager.commit(status);
        } catch (Exception e) {
            try {
                platformTransactionManager.rollback(status);
            } finally {
                String errorMsg = getErrorInfoFromException(e);
                LOGGER.info("error.sagaConsumer.invoke {}", errorMsg);
                consumerClient.updateStatus(data.getId(),
                        UpdateStatusDTO.UpdateStatusDTOBuilder.newInstance()
                                .withStatus(SagaDefinition.TaskInstanceStatus.FAILED.name())
                                .withExceptionMessage(errorMsg)
                                .withId(data.getId())
                                .withObjectVersionNumber(data.getObjectVersionNumber()).build());
            }
        } finally {
            afterInvoke();
        }
        return data;
    }

}
