package io.choerodon.event.producer.execute;

/**
 * 创建的事件实体
 *
 * @author flyleft
 */
public class EventRecord {

    private String uuid;

    private String type;

    private String service;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public EventRecord() {
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public EventRecord(String uuid, String type, String service) {
        this.uuid = uuid;
        this.type = type;
        this.service = service;
    }

    @Override
    public String toString() {
        return "EventRecord{" +
                "uuid='" + uuid + '\'' +
                ", type='" + type + '\'' +
                ", service='" + service + '\'' +
                '}';
    }
}
