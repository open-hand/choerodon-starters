package io.choerodon.asgard.schedule.dto;

import java.util.Set;

public class PollScheduleInstanceDTO {

    private Set<String> methods;

    private String instance;

    private String service;

    private Set<Long> runningIds;

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public PollScheduleInstanceDTO(Set<String> methods, String instance, String service, Set<Long> runningIds) {
        this.methods = methods;
        this.instance = instance;
        this.service = service;
        this.runningIds = runningIds;
    }

    public PollScheduleInstanceDTO() {
    }

    public Set<Long> getRunningIds() {
        return runningIds;
    }

    public void setRunningIds(Set<Long> runningIds) {
        this.runningIds = runningIds;
    }

    public Set<String> getMethods() {
        return methods;
    }

    public void setMethods(Set<String> methods) {
        this.methods = methods;
    }
}
