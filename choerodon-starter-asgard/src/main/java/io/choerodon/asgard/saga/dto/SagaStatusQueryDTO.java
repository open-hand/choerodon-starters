package io.choerodon.asgard.saga.dto;

public class SagaStatusQueryDTO {

    public static final String STATUS_CANCEL = "cancel";

    public static final String STATUS_CONFIRM = "confirm";

    private String status;

    private String payload;

    private String refType;

    private String refId;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public SagaStatusQueryDTO() {
    }

    public SagaStatusQueryDTO(String status, String payload, String refType, String refId) {
        this.status = status;
        this.payload = payload;
        this.refType = refType;
        this.refId = refId;
    }

    public SagaStatusQueryDTO(String status) {
        this.status = status;
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

    @Override
    public String toString() {
        return "SagaStatusQueryDTO{" +
                "status='" + status + '\'' +
                ", payload='" + payload + '\'' +
                ", refType='" + refType + '\'' +
                ", refId='" + refId + '\'' +
                '}';
    }
}
