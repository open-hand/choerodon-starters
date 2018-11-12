package io.choerodon.websocket.helper;

import java.io.Serializable;

/**
 * @author crockitwood
 */
public class EnvSession implements Serializable{
    private String registerKey;
    private Long clusterId;
    private String version;

    public String getRegisterKey() {
        return registerKey;
    }

    public void setRegisterKey(String registerKey) {
        this.registerKey = registerKey;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
