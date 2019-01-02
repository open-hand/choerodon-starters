package io.choerodon.asgard.schedule.dto;

import io.choerodon.core.oauth.CustomUserDetails;

import java.util.Objects;

public class ScheduleInstanceConsumerDTO {

    private Long id;

    private String method;

    private String executeParams;

    private String instanceLock;

    private Long objectVersionNumber;

    private CustomUserDetails userDetails;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScheduleInstanceConsumerDTO that = (ScheduleInstanceConsumerDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getExecuteParams() {
        return executeParams;
    }

    public void setExecuteParams(String executeParams) {
        this.executeParams = executeParams;
    }

    public String getInstanceLock() {
        return instanceLock;
    }

    public void setInstanceLock(String instanceLock) {
        this.instanceLock = instanceLock;
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
        return "ScheduleInstanceConsumerDTO{" +
                "id=" + id +
                ", method='" + method + '\'' +
                ", executeParams='" + executeParams + '\'' +
                ", instanceLock='" + instanceLock + '\'' +
                ", objectVersionNumber=" + objectVersionNumber +
                ", userDetails=" + userDetails +
                '}';
    }
}
