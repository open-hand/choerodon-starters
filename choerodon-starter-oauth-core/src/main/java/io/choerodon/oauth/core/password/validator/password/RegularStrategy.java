package io.choerodon.oauth.core.password.validator.password;

import io.choerodon.core.exception.CommonException;
import io.choerodon.oauth.core.password.PasswordPolicyMap;
import io.choerodon.oauth.core.password.PasswordPolicyType;
import io.choerodon.oauth.core.password.PasswordStrategy;
import io.choerodon.oauth.core.password.domain.BaseUserDO;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wuguokai
 */
public class RegularStrategy implements PasswordStrategy {
    private static final String ERROR_MESSAGE = "error.password.policy.regular";
    public static final String TYPE = PasswordPolicyType.REGULAR.getValue();

    @Override
    public Object validate(PasswordPolicyMap policyMap, BaseUserDO userDO, String password) {
        Object reg = policyMap.getPasswordConfig().get(TYPE);
        if (reg instanceof String) {
            Pattern pattern = Pattern.compile((String) reg);
            Matcher matcher = pattern.matcher(password);
            if (!matcher.matches()) {
                throw new CommonException(ERROR_MESSAGE);
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
