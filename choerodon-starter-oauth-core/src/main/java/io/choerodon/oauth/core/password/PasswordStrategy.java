package io.choerodon.oauth.core.password;

import io.choerodon.oauth.core.password.domain.BaseUserDTO;

/**
 * @author wuguokai
 */
public interface PasswordStrategy {
    <T> T validate(PasswordPolicyMap policyMap, BaseUserDTO userDO, String password);
    String getType();
    Object parseConfig(Object value);
}
