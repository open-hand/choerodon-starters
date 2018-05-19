package io.choerodon.feign;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by WUGUOKAI on 2018/3/12.
 */
@ConfigurationProperties(prefix = "choerodon")
public class CommonProperties {

    private  Long serviceAccountId = 2L;

    private  String oauthJwtKey = "choerodon";

    public Long getServiceAccountId() {
        return serviceAccountId;
    }

    public void setServiceAccountId(Long serviceAccountId) {
        this.serviceAccountId = serviceAccountId;
    }

    public String getOauthJwtKey() {
        return oauthJwtKey;
    }

    public void setOauthJwtKey(String oauthJwtKey) {
        this.oauthJwtKey = oauthJwtKey;
    }
}
