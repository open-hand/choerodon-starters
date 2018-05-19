package io.choerodon.event.producer.check;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * @author flyleft
 * 2018/5/17
 */
@Table(name = "event_producer_record")
@Entity
public class EventProducerRecord {

    @Id
    private String uuid;

    private String type;

    private Timestamp createTime;

    public EventProducerRecord(String uuid, String type, Timestamp createTime) {
        this.uuid = uuid;
        this.type = type;
        this.createTime = createTime;
    }

    public EventProducerRecord() {
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
