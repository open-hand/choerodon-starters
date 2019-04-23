package io.choerodon.oauth.core.password.validator.password;

import io.choerodon.core.exception.CommonException;
import io.choerodon.oauth.core.password.PasswordPolicyMap;
import io.choerodon.oauth.core.password.PasswordStrategy;
import io.choerodon.oauth.core.password.domain.BaseUserDTO;
import io.choerodon.oauth.core.password.PasswordPolicyType;

/**
 * @author wuguokai
 */
public class DigitsCountStrategy implements PasswordStrategy {
    private static final String ERROR_MESSAGE = "error.password.policy.digits";
    public static final String TYPE = PasswordPolicyType.DIGITS_COUNT.getValue();

    @Override
    public Object validate(PasswordPolicyMap policyMap, BaseUserDTO userDO, String password) {
        Integer min = (Integer) policyMap.getPasswordConfig().get(TYPE);
        if (min != null && min != 0) {
            int count = 0;
            for (char c : password.toCharArray()) {
                if (Character.isDigit(c)) {
                    count++;
                }
            }
            if (count < min) {
                throw new CommonException(ERROR_MESSAGE, min);
            }
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
