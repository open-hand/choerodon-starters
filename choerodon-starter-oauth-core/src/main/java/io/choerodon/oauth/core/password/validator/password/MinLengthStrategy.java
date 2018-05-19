package io.choerodon.oauth.core.password.validator.password;

import io.choerodon.core.exception.CommonException;
import io.choerodon.oauth.core.password.PasswordPolicyMap;
import io.choerodon.oauth.core.password.PasswordPolicyType;
import io.choerodon.oauth.core.password.PasswordStrategy;
import io.choerodon.oauth.core.password.domain.BaseUserDO;

/**
 * @author wuguokai
 */
public class MinLengthStrategy implements PasswordStrategy {
    private static final String ERROR_MESSAGE = "error.password.policy.minLength";
    public static final String TYPE = PasswordPolicyType.MIN_LENGTH.getValue();

    @Override
    public Object validate(PasswordPolicyMap policyMap, BaseUserDO userDO, String password) {
        Integer min = (Integer) policyMap.getPasswordConfig().get(TYPE);
        if (min != null && min != 0 && password.length()<min) {
            throw new CommonException(ERROR_MESSAGE, min);
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
