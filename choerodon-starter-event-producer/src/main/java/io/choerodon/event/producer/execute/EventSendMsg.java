package io.choerodon.event.producer.execute;

/**
 * @author flyleft
 * 2018/4/4
 */
public class EventSendMsg {

    /**
     * 要发送给消息队列的通道名
     */
    private String topic;
    /**
     * 要发送给消息队列的消息内容
     */
    private String payload;

    public EventSendMsg() {
    }

    public EventSendMsg(String topic, String payload) {
        this.topic = topic;
        this.payload = payload;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

}
