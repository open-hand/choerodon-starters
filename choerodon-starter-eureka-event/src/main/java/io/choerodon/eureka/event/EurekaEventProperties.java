package io.choerodon.eureka.event;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;

@ConfigurationProperties(prefix = "choerodon.eureka.event")
public class EurekaEventProperties {

    private Integer retryTime = 5;

    private Integer retryInterval = 3;

    private Integer maxCacheSize = 300;

    private String[] skipServices = new String[]{"register-server", "api-gateway", "gateway-helper", "oauth-server", "config-server"};

    public Integer getRetryTime() {
        return retryTime;
    }

    public void setRetryTime(Integer retryTime) {
        this.retryTime = retryTime;
    }

    public Integer getRetryInterval() {
        return retryInterval;
    }

    public void setRetryInterval(Integer retryInterval) {
        this.retryInterval = retryInterval;
    }

    public Integer getMaxCacheSize() {
        return maxCacheSize;
    }

    public void setMaxCacheSize(Integer maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
    }

    public String[] getSkipServices() {
        return skipServices;
    }

    public void setSkipServices(String[] skipServices) {
        this.skipServices = skipServices;
    }

    @Override
    public String toString() {
        return "EurekaEventProperties{" +
                "retryTime=" + retryTime +
                ", retryInterval=" + retryInterval +
                ", maxCacheSize=" + maxCacheSize +
                ", skipServices=" + Arrays.toString(skipServices) +
                '}';
    }
}
