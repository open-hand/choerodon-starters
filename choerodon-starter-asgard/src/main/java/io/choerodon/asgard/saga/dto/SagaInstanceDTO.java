package io.choerodon.asgard.saga.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class SagaInstanceDTO {

    private Long id;

    private String sagaCode;

    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    private Long inputDataId;

    private Long outputDataId;

    private String refType;

    private String refId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSagaCode() {
        return sagaCode;
    }

    public void setSagaCode(String sagaCode) {
        this.sagaCode = sagaCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Long getInputDataId() {
        return inputDataId;
    }

    public void setInputDataId(Long inputDataId) {
        this.inputDataId = inputDataId;
    }

    public Long getOutputDataId() {
        return outputDataId;
    }

    public void setOutputDataId(Long outputDataId) {
        this.outputDataId = outputDataId;
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
}
