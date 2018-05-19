package io.choerodon.event.producer.execute;

import io.choerodon.core.event.EventPayload;

import java.io.Serializable;

/**
 * @author flyleft
 * 2018/4/8
 */
public class EventMessage  implements Serializable {
    /**
     * 要发送给消息队列的通道名
     */
    private String topic;
    /**
     * 要发送给消息队列的消息内容
     */
    private transient EventPayload<?> payload;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public EventPayload getPayload() {
        return payload;
    }

    public void setPayload(EventPayload<?> payload) {
        this.payload = payload;
    }

    public EventMessage() {
    }

    public EventMessage(String topic, EventPayload<?> payload) {
        this.topic = topic;
        this.payload = payload;
    }
}
