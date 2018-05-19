package io.choerodon.event.consumer.handler;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.client.RestTemplate;

import io.choerodon.event.consumer.CommonUtils;
import io.choerodon.event.consumer.DuplicateRemoveListener;
import io.choerodon.event.consumer.EventConsumerProperties;
import io.choerodon.event.consumer.domain.FailedMsg;
import io.choerodon.event.consumer.domain.MsgExecuteBean;
import io.choerodon.event.consumer.exception.SendEventStoreException;
import io.choerodon.event.consumer.retry.RetryFactory;

/**
 * 默认消息处理器
 *
 * @author flyleft
 * 2018/1/25
 */
public class DefaultMsgHandlerImpl implements MsgHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MsgHandler.class);

    private Set<String> beingHandlingUuid = new HashSet<>();

    private DataSourceTransactionManager transactionManager;

    private DuplicateRemoveListener listener;

    private Optional<RetryFactory> retryFactory;


    private RestTemplate restTemplate;

    @Value("${event.store.service.name:event-store-service}")
    private String eventStoreService;

    /**
     * 构造器
     *
     * @param transactionManager 事务处理器
     * @param listener           消息去重器
     * @param retryFactory       用于重试的工厂
     * @param restTemplate       发送HTTP请求的restTemplate
     */
    public DefaultMsgHandlerImpl(DataSourceTransactionManager transactionManager,
                                 DuplicateRemoveListener listener,
                                 Optional<RetryFactory> retryFactory,
                                 RestTemplate restTemplate) {
        this.transactionManager = transactionManager;
        this.listener = listener;
        this.retryFactory = retryFactory;
        this.restTemplate = restTemplate;
    }

    @Override
    public void executeAsync(final MsgExecuteBean msgExecuteBean) {
        execute(msgExecuteBean);
    }

    @Override
    public void execute(final MsgExecuteBean bean) {
        if (filter(bean)) {
            return;
        }
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(bean.eventListener.transactionDefinition());
        TransactionStatus status = transactionManager.getTransaction(def);
        try {
            bean.method.setAccessible(true);
            bean.method.invoke(bean.object, bean.getPayload());
            msgSucceededHandle(bean);
            transactionManager.commit(status);
        } catch (Exception e) {
            LOGGER.warn("message consume exception, msg : {}, cause {}", bean.getPayload(), e.toString());
            if (LOGGER.isDebugEnabled()) {
                e.printStackTrace();
            }
            transactionManager.rollback(status);
            bean.setExceptionMessage(CommonUtils.getErrorInfoFromException(e));
            msgFailedHandle(bean);
        }
    }

    private void msgSucceededHandle(final MsgExecuteBean bean) {
        if (bean.consumerProperties.isEnableDuplicateRemove()) {
            beingHandlingUuid.remove(bean.getPayload().getUuid());
            listener.after(bean.getPayload().getUuid());
        }
        bean.setSuccess(true);
    }

    private void msgFailedHandle(final MsgExecuteBean bean) {
        if (retryFactory.isPresent() && !bean.isRetry() && bean.eventListener.retryTimes() > 0) {
            bean.setRetry(true);
            retryFactory.get().addRetry(bean);
            return;
        }
        if (bean.isRetry() && bean.getHasRetryTimes().get() < bean.eventListener.retryTimes()) {
            return;
        }
        if (bean.consumerProperties.isEnableDuplicateRemove()) {
            beingHandlingUuid.remove(bean.getPayload().getUuid());
        }
        if (EventConsumerProperties.FAILED_STRATEGY_EVENT_STORE.equals(bean.consumerProperties.getFailedStrategy())) {
            msgFailedSendEventStoreCallback(bean);
        }
    }

    private void msgFailedSendEventStoreCallback(final MsgExecuteBean bean) {
        FailedMsg failedMsg = new FailedMsg(bean.getPayload().getUuid(), bean.eventListener.topic(),
                bean.getPayloadJson(), bean.getExceptionMessage(), bean.getKafkaPartition(),
                bean.getKafkaOffset(), bean.getMessageTimestamp());
        String uri = "http://" + eventStoreService + "/v1/messages/failed";
        ResponseEntity<?> responseEntity;
        responseEntity = restTemplate.postForEntity(uri, failedMsg, Void.class);
        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new SendEventStoreException("error has happened when send msg to event store, http status:"
                    + responseEntity.getStatusCodeValue());
        }
    }

    @Override
    public boolean filter(final MsgExecuteBean bean) {
        if (bean.isRetry()) {
            return false;
        }
        String uuid = bean.getPayload().getUuid();
        synchronized (this) {

            boolean result = bean.consumerProperties.isEnableDuplicateRemove()
                    && (beingHandlingUuid.contains(uuid) || listener.hasBeanConsumed(uuid));
            if (result) {
                LOGGER.debug("skip message by filter, duplicate uuid found, uuid {}", uuid);
                beingHandlingUuid.add(uuid);
                return true;
            }
        }
        return false;
    }
}
