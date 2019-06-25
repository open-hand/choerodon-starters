package io.choerodon.websocket;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@ConfigurationProperties("choerodon.ws")
@EnableConfigurationProperties(ChoerodonWebSocketProperties.class)
public class ChoerodonWebSocketProperties {

    private String[] paths = {"/choerodon:msg/**"};

    private boolean oauth = false;

    private String oauthUrl = "http://oauth-server/oauth/api/user";

    private Long heartBeatIntervalMs = 10000L;

    public String[] getPaths() {
        return paths;
    }

    public void setPaths(String[] paths) {
        this.paths = paths;
    }

    public boolean isOauth() {
        return oauth;
    }

    public void setOauth(boolean oauth) {
        this.oauth = oauth;
    }

    public String getOauthUrl() {
        return oauthUrl;
    }

    public void setOauthUrl(String oauthUrl) {
        this.oauthUrl = oauthUrl;
    }

    public Long getHeartBeatIntervalMs() {
        return heartBeatIntervalMs;
    }

    public void setHeartBeatIntervalMs(Long heartBeatIntervalMs) {
        this.heartBeatIntervalMs = heartBeatIntervalMs;
    }
}
