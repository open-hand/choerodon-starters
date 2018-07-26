package io.choerodon.asgard.saga;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "choerodon.asgard")
public class ChoerodonSagaProperties {

    private Integer pollInterval = 1;

    private Integer maxExecuteThread = 5;

    private Boolean enabled = false;

    public Integer getPollInterval() {
        return pollInterval;
    }

    public Integer getMaxExecuteThread() {
        return maxExecuteThread;
    }

    public void setMaxExecuteThread(Integer maxExecuteThread) {
        this.maxExecuteThread = maxExecuteThread;
    }

    public void setPollInterval(Integer pollInterval) {
        this.pollInterval = pollInterval;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
