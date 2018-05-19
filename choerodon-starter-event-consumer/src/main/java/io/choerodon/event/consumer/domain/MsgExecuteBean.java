package io.choerodon.event.consumer.domain;

import io.choerodon.core.event.EventPayload;
import io.choerodon.event.consumer.EventConsumerProperties;
import io.choerodon.event.consumer.annotation.EventListener;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 消息执行实体
 *
 * @author flyleft
 * 2018/1/25
 */
public class MsgExecuteBean {

    public final EventListener eventListener;

    public final Method method;

    public final Object object;

    public final EventConsumerProperties consumerProperties;

    private String payloadJson;

    private EventPayload<?> payload;

    private Long messageTimestamp;

    private String exceptionMessage;

    private boolean retry;

    private Integer kafkaPartition;

    private Long kafkaOffset;


    private AtomicInteger hasRetryTimes = new AtomicInteger(0);


    private AtomicBoolean success = new AtomicBoolean(false);


    public MsgExecuteBean(final EventListener eventListener,
                          final Method method,
                          final Object object,
                          final EventConsumerProperties consumerProperties) {
        this.eventListener = eventListener;
        this.method = method;
        this.object = object;
        this.consumerProperties = consumerProperties;
    }

    public boolean isRetry() {
        return retry;
    }

    public void setRetry(boolean retry) {
        this.retry = retry;
    }

    public EventPayload getPayload() {
        return payload;
    }

    public void setPayload(EventPayload<?> payload) {
        this.payload = payload;
    }

    public boolean getSuccess() {
        return success.get();
    }

    public void setSuccess(boolean success) {
        this.success.set(success);
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public AtomicInteger getHasRetryTimes() {
        return hasRetryTimes;
    }

    public Long getMessageTimestamp() {
        return messageTimestamp;
    }

    public void setMessageTimestamp(Long messageTimestamp) {
        this.messageTimestamp = messageTimestamp;
    }

    public Integer getKafkaPartition() {
        return kafkaPartition;
    }

    public void setKafkaPartition(Integer kafkaPartition) {
        this.kafkaPartition = kafkaPartition;
    }

    public Long getKafkaOffset() {
        return kafkaOffset;
    }

    public void setKafkaOffset(Long kafkaOffset) {
        this.kafkaOffset = kafkaOffset;
    }

    public String getPayloadJson() {
        return payloadJson;
    }

    public void setPayloadJson(String payloadJson) {
        this.payloadJson = payloadJson;
    }
}
