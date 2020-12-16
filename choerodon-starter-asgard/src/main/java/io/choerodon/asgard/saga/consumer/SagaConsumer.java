package io.choerodon.asgard.saga.consumer;

import io.choerodon.asgard.common.AbstractAsgardConsumer;
import io.choerodon.asgard.common.ApplicationContextHelper;
import io.choerodon.asgard.common.UpdateStatusDTO;
import io.choerodon.asgard.saga.SagaDefinition;
import io.choerodon.asgard.saga.SagaProperties;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.asgard.saga.context.ApplicationContextUtil;
import io.choerodon.asgard.saga.dto.PollSagaTaskInstanceDTO;
import io.choerodon.asgard.saga.dto.SagaTaskInstanceDTO;
import io.choerodon.asgard.saga.feign.SagaConsumerClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;

import static io.choerodon.asgard.common.InstanceResultUtils.*;

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
        try {
            List<SagaTaskInstanceDTO> list = consumerClient.pollBatch(getPollDTO());
            if (!CollectionUtils.isEmpty(list)) {
                list.forEach(t -> {
                    LOGGER.trace("SagaConsumer polled sagaTaskInstances: {}", t);
                    runningTasks.add(t.getId());
                    CompletableFuture.supplyAsync(() -> invoke(t), executor)
                            .exceptionally(ex -> {
                                LOGGER.warn("@SagaTask method code: {}, id: {} supplyAsync failed", t.getTaskCode(), t.getId(), ex);
                                return null;
                            })
                            .thenAccept(i -> LOGGER.trace("@SagaTask method code: {}, id: {} supplyAsync completed", t.getTaskCode(), t.getId()));
                });
            }
        } catch (Exception e) {
            LOGGER.error("SagaTask failed to execute", e);
        }
    }

    private PollSagaTaskInstanceDTO getPollDTO() {
        if (pollDTO == null) {
            pollDTO = new PollSagaTaskInstanceDTO(instance, service, this.properties.getConsumer().getMaxPollSize(), runningTasks);
        }
        return pollDTO;
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
            runningTasks.remove(data.getId());
            platformTransactionManager.commit(status);
        } catch (Exception e) {
            LOGGER.info("@SagaTask method code: {}, id: {} invoke error", data.getTaskCode(), data.getId(), getLoggerException(e));
            String errorMsg = getErrorInfoFromException(e);
            invokeError(platformTransactionManager, status, data, errorMsg, invokeBean);
        } finally {
            afterInvoke();
        }
        return data;
    }


    private void invokeError(final PlatformTransactionManager platformTransactionManager,
                             final TransactionStatus status,
                             final SagaTaskInstanceDTO data,
                             final String errorMsg,
                             final SagaTaskInvokeBean invokeBean) {
        try {
            platformTransactionManager.rollback(status);
        } catch (Exception e) {
            LOGGER.warn("@SagaTask method code: {}, id: {} transaction rollback error", data.getTaskCode(), data.getId(), e);
        } finally {
            try {
                ResponseEntity<String> responseEntity = consumerClient.updateStatus(data.getId(),
                        UpdateStatusDTO.UpdateStatusDTOBuilder.newInstance()
                                .withStatus(SagaDefinition.TaskInstanceStatus.FAILED.name())
                                .withExceptionMessage(errorMsg)
                                .withId(data.getId())
                                .withObjectVersionNumber(data.getObjectVersionNumber()).build());
                // 执行失败 执行失败回调
                executeFailureCallbackMethod(responseEntity.getBody(), invokeBean, data);
                runningTasks.remove(data.getId());
            } catch (Exception ex) {
                CompletableFuture.supplyAsync(() -> this.retryUpdateStatusFailed(data.getId(), errorMsg), executor);
            }
        }
    }

    private void executeFailureCallbackMethod(String sagaTaskInstanceStatus, SagaTaskInvokeBean invokeBean, SagaTaskInstanceDTO data) {
        if (!StringUtils.isEmpty(sagaTaskInstanceStatus) && sagaTaskInstanceStatus.equals(SagaDefinition.TaskInstanceStatus.FAILED.name())) {
            String failureCallbackStatus = SagaDefinition.TaskInstanceStatus.COMPLETED.name();
            try {
                Object object = ApplicationContextHelper.getBean(invokeBean.clazz);
                invokeBean.failureCallbackMethod.invoke(object, data.getInput());
            } catch (Exception e) {
                failureCallbackStatus = SagaDefinition.TaskInstanceStatus.FAILED.name();
                e.printStackTrace();
            } finally {
                consumerClient.updateStatusFailureCallback(data.getId(), failureCallbackStatus);
            }
        }
    }

    private Long retryUpdateStatusFailed(final Long id, final String errorMsg) {
        while (true) {
            try {
                Thread.sleep(1000);
                SagaTaskInstanceDTO dto = consumerClient.queryStatus(id);
                if (dto == null) {
                    runningTasks.remove(id);
                    LOGGER.error("@SagaTask method id: {} queryStatus failed", id);
                    return id;
                }
                consumerClient.updateStatus(id, UpdateStatusDTO.UpdateStatusDTOBuilder.newInstance()
                        .withStatus(SagaDefinition.TaskInstanceStatus.FAILED.name())
                        .withExceptionMessage(errorMsg)
                        .withId(id)
                        .withObjectVersionNumber(dto.getObjectVersionNumber()).build());
                runningTasks.remove(id);
                break;
            } catch (InterruptedException e) {
                runningTasks.remove(id);
                Thread.currentThread().interrupt();
                LOGGER.error("@SagaTask method id: {} retry to updateStatus failed, thread is Interrupted", id, e);
            } catch (Exception e) {
                LOGGER.debug("@SagaTask method id: {} auto retry to updateStatus failed", id, e);
            }
        }
        return id;
    }
}
