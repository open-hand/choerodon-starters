package io.choerodon.asgard.saga.dto;


import java.util.Set;

public class PollSagaTaskInstanceDTO {

    private String instance;

    private String service;

    private Integer maxPollSize;

    private Set<Long> runningIds;

    public PollSagaTaskInstanceDTO(String instance, String service, Integer maxPollSize) {
        this.instance = instance;
        this.service = service;
        this.maxPollSize = maxPollSize;
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


    public Integer getMaxPollSize() {
        return maxPollSize;
    }

    public void setMaxPollSize(Integer maxPollSize) {
        this.maxPollSize = maxPollSize;
    }

    public Set<Long> getRunningIds() {
        return runningIds;
    }

    public void setRunningIds(Set<Long> runningIds) {
        this.runningIds = runningIds;
    }

    @Override
    public String toString() {
        return "PollSagaTaskInstanceDTO{" +
                "instance='" + instance + '\'' +
                ", service='" + service + '\'' +
                ", maxPollSize=" + maxPollSize +
                ", runningIds=" + runningIds +
                '}';
    }
}
