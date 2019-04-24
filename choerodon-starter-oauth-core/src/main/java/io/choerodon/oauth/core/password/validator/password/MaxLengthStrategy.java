package io.choerodon.oauth.core.password.validator.password;

import io.choerodon.core.exception.CommonException;
import io.choerodon.oauth.core.password.PasswordPolicyMap;
import io.choerodon.oauth.core.password.PasswordPolicyType;
import io.choerodon.oauth.core.password.PasswordStrategy;
import io.choerodon.oauth.core.password.domain.BaseUserDTO;

/**
 * @author wuguokai
 */
public class MaxLengthStrategy implements PasswordStrategy {
    private static final String ERROR_MESSAGE = "error.password.policy.maxLength";
    private static final String TYPE = PasswordPolicyType.MAX_LENGTH.getValue();

    @Override
    public Object validate(PasswordPolicyMap policyMap, BaseUserDTO userDO, String password) {
        Integer max = (Integer) policyMap.getPasswordConfig().get(TYPE);
        if (max != null && max != 0 && password.length() > max) {
            throw new CommonException(ERROR_MESSAGE, max);
        }
        return null;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public Object parseConfig(Object value) {
        return null;
    }
}
