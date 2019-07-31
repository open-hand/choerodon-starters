package io.choerodon.websocket.websocket;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "choerodon.websocket")
public class SocketProperties {
    private boolean security = true;
    private String oauthUrl;
    private String agent = "/agent/**";
    private String front = "/ws/**";
    private boolean commandTimeoutEnabled = true;
    //millisecond
    private long registerInterval = 2000;
    private int maxRedisMsgListenerConcurrency;
    private int commandTimeoutSeconds = 10;
    private int durationCount = 10;
    /**
     * Maximum message buffer, unit bytes.
     */
    private int maxMessageBufferSize = 500 * 1024;
    /**
     * Maximum free time allowed. In milliseconds. Health checks should be performed after this time.
     */
    private int healthCheckDuration = 30 * 1000;
    /**
     * The timeout time for a io.choerodon.websocket.websocket.health check. Exceeding this value indicates that the io.choerodon.websocket.websocket.health check is not passed. Milliseconds.
     */
    private int healthCheckTimeout = 6 * 1000;
    /**
     * The maximum number of io.choerodon.websocket.websocket.health checks is considered to be a failure only if three fail.
     */
    private int healthCheckTryNumber = 3;
    /**
     * Number of io.choerodon.websocket.websocket.health worker.
     */
    private int healthCheckWorkerNumber = Runtime.getRuntime().availableProcessors() + 1;
    /**
     *  don`t dispatch to channel when destination is self
     */
    private boolean dispatchChannel = false;

    public String getOauthUrl() {
        return oauthUrl;
    }

    public void setOauthUrl(String oauthUrl) {
        this.oauthUrl = oauthUrl;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getFront() {
        return front;
    }

    public void setFront(String front) {
        this.front = front;
    }

    public boolean isSecurity() {
        return security;
    }

    public void setSecurity(boolean security) {
        this.security = security;
    }


    public int getMaxRedisMsgListenerConcurrency() {
        return maxRedisMsgListenerConcurrency;
    }

    public void setMaxRedisMsgListenerConcurrency(int maxRedisMsgListenerConcurrency) {
        this.maxRedisMsgListenerConcurrency = maxRedisMsgListenerConcurrency;
    }


    public long getRegisterInterval() {
        return registerInterval;
    }

    public void setRegisterInterval(long registerInterval) {
        this.registerInterval = registerInterval;
    }

    public int getCommandTimeoutSeconds() {
        return commandTimeoutSeconds;
    }

    public void setCommandTimeoutSeconds(int commandTimeoutSeconds) {
        this.commandTimeoutSeconds = commandTimeoutSeconds;
    }


    public boolean isCommandTimeoutEnabled() {
        return commandTimeoutEnabled;
    }

    public void setCommandTimeoutEnabled(boolean commandTimeoutEnabled) {
        this.commandTimeoutEnabled = commandTimeoutEnabled;
    }

    public boolean isDispatchChannel() {
        return dispatchChannel;
    }

    public void setDispatchChannel(boolean dispatchChannel) {
        this.dispatchChannel = dispatchChannel;
    }

    public int getDurationCount() {
        return durationCount;
    }

    public void setDurationCount(int durationCount) {
        this.durationCount = durationCount;
    }

    public int getHealthCheckDuration() {
        return healthCheckDuration;
    }

    public void setHealthCheckDuration(int healthCheckDuration) {
        this.healthCheckDuration = healthCheckDuration;
    }

    public int getHealthCheckTimeout() {
        return healthCheckTimeout;
    }

    public void setHealthCheckTimeout(int healthCheckTimeout) {
        this.healthCheckTimeout = healthCheckTimeout;
    }

    public int getHealthCheckTryNumber() {
        return healthCheckTryNumber;
    }

    public void setHealthCheckTryNumber(int healthCheckTryNumber) {
        this.healthCheckTryNumber = healthCheckTryNumber;
    }

    public int getHealthCheckWorkerNumber() {
        return healthCheckWorkerNumber;
    }

    public void setHealthCheckWorkerNumber(int healthCheckWorkerNumber) {
        this.healthCheckWorkerNumber = healthCheckWorkerNumber;
    }

    public int getMaxMessageBufferSize() {
        return maxMessageBufferSize;
    }

    public void setMaxMessageBufferSize(int maxMessageBufferSize) {
        this.maxMessageBufferSize = maxMessageBufferSize;
    }
}
