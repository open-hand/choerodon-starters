package io.choerodon.asgard.saga.dto;

public class StartInstanceDTO {

    private String sagaCode;

    private String input;

    private String refType = "";

    private String refId = "";

    private String level;

    private Long sourceId;

    private String uuid;

    public String getSagaCode() {
        return sagaCode;
    }

    public void setSagaCode(String sagaCode) {
        this.sagaCode = sagaCode;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getRefType() {
        return refType;
    }

    public void setRefType(String refType) {
        this.refType = refType;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return "StartInstanceDTO{" +
                "sagaCode='" + sagaCode + '\'' +
                ", input='" + input + '\'' +
                ", refType='" + refType + '\'' +
                ", refId='" + refId + '\'' +
                ", level='" + level + '\'' +
                ", sourceId=" + sourceId +
                ", uuid='" + uuid + '\'' +
                '}';
    }
}
