package io.choerodon.asgard.schedule;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "choerodon.schedule.consumer")
public class ScheduleProperties {

    private Long pollIntervalMs = 1000L;

    private Integer coreThreadNum = 1;

    private Integer maxThreadNum = 2;

    private Boolean enabled = false;

    public Long getPollIntervalMs() {
        return pollIntervalMs;
    }

    public void setPollIntervalMs(Long pollIntervalMs) {
        this.pollIntervalMs = pollIntervalMs;
    }

    public Integer getCoreThreadNum() {
        return coreThreadNum;
    }

    public void setCoreThreadNum(Integer coreThreadNum) {
        this.coreThreadNum = coreThreadNum;
    }

    public Integer getMaxThreadNum() {
        return maxThreadNum;
    }

    public void setMaxThreadNum(Integer maxThreadNum) {
        this.maxThreadNum = maxThreadNum;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
