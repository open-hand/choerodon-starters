package io.choerodon.event.consumer.domain;

import java.sql.Timestamp;

/**
 * 消费失败的消息实体类
 * @author flyleft
 * 2018/2/2
 */
public class FailedMsg {

    private String uuid;

    private String topic;

    private String message;

    private Timestamp createTime;

    private String exceptionMessage;

    private Integer kafkaPartition;

    private Long kafkaOffset;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
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

    public FailedMsg() {
    }

    public FailedMsg(String uuid) {
        this.uuid = uuid;
    }

    /**
     * 构造器
     * @param uuid 消息uuid
     * @param topic 消息的topic
     * @param message 消息实体
     * @param exceptionMessage 消费异常堆栈信息
     * @param kafkaPartition 消息的kafka分区
     * @param kafkaOffset 消息在kafka分区中位移
     * @param timestamp 消息的时间戳
     */
    public FailedMsg(String uuid, String topic, String message,
                     String exceptionMessage, Integer kafkaPartition,
                     Long kafkaOffset, Long timestamp) {
        this.uuid = uuid;
        this.topic = topic;
        this.message = message;
        this.exceptionMessage = exceptionMessage;
        this.kafkaPartition = kafkaPartition;
        this.kafkaOffset = kafkaOffset;
        if (timestamp == null) {
            this.createTime = new Timestamp(System.currentTimeMillis());
        } else {
            this.createTime = new Timestamp(timestamp);
        }
    }
}
