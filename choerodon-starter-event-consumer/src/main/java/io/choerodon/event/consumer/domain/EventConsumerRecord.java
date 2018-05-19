package io.choerodon.event.consumer.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * 记录消息消费成功的记录，用于消息去重
 * @author flyleft
 * 2017/10/18
 */
@Table(name = "event_consumer_record")
@Entity
public class EventConsumerRecord {

    @Id
    private String uuid;

    private Timestamp createTime;

    public EventConsumerRecord(String uuid, Timestamp createTime) {
        this.uuid = uuid;
        this.createTime = createTime;
    }

    public EventConsumerRecord() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }
}
