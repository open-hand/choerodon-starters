package io.choerodon.saga;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "choerodon.saga")
public class ChoerodonSagaProperties {

    private Integer pollInterval = 60;

    private Integer maxExecuteThread = 5;

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
}
