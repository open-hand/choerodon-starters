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
}
