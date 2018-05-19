package io.choerodon.oauth.core.password;

import io.choerodon.oauth.core.password.domain.BaseUserDO;

/**
 * @author wuguokai
 */
public interface PasswordStrategy {
    <T> T validate(PasswordPolicyMap policyMap, BaseUserDO userDO, String password);
    String getType();
    Object parseConfig(Object value);
}
