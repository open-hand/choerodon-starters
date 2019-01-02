package io.choerodon.asgard.schedule.dto;

import java.util.Set;

public class PollScheduleInstanceDTO {

    private Set<String> methods;

    private String instance;

    private String service;

    public Set<String> getMethods() {
        return methods;
    }

    public void setMethods(Set<String> methods) {
        this.methods = methods;
    }

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

    public PollScheduleInstanceDTO(Set<String> methods, String instance, String service) {
        this.methods = methods;
        this.instance = instance;
        this.service = service;
    }

    public PollScheduleInstanceDTO() {
    }
}
