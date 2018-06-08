package io.choerodon.websocket.helper;

import java.io.Serializable;

/**
 * @author crockitwood
 */
public class EnvSession implements Serializable{
    private String registerKey;
    private Long envId;
    private String version;

    public String getRegisterKey() {
        return registerKey;
    }

    public void setRegisterKey(String registerKey) {
        this.registerKey = registerKey;
    }

    public Long getEnvId() {
        return envId;
    }

    public void setEnvId(Long envId) {
        this.envId = envId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
