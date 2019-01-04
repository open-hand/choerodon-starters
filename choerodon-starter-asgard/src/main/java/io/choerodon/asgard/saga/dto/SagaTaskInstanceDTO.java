package io.choerodon.asgard.saga.dto;

import io.choerodon.core.oauth.CustomUserDetails;

public class SagaTaskInstanceDTO {

    private Long id;

    private String status;

    private String taskCode;

    private String sagaCode;

    private String input;

    private Long objectVersionNumber;

    private CustomUserDetails userDetails;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(String taskCode) {
        this.taskCode = taskCode;
    }

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

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public CustomUserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(CustomUserDetails userDetails) {
        this.userDetails = userDetails;
    }

    @Override
    public String toString() {
        return "SagaTaskInstanceDTO{" +
                "id=" + id +
                ", status='" + status + '\'' +
                ", taskCode='" + taskCode + '\'' +
                ", sagaCode='" + sagaCode + '\'' +
                ", input='" + input + '\'' +
                ", objectVersionNumber=" + objectVersionNumber +
                '}';
    }
}
