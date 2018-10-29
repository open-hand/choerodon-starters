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
public class SpecialCharCountStrategy implements PasswordStrategy {
    private static final String ERROR_MESSAGE = "error.password.policy.specialChar";
    public static final String TYPE = PasswordPolicyType.SPECIALCHAR_COUNT.getValue();
    /**
     * The regex expression to validate special characters. It actually includes 24 characters:
     *  ~`@#$%^&*-_=+|/()<>,.;:!
     */
    private static final String SPECIAL_REGEX = "[~`@#$%^&*\\-_=+|/()<>,.;:!]";
    private static final Pattern PATTERN = Pattern.compile(SPECIAL_REGEX);

    @Override
    public Object validate(PasswordPolicyMap policyMap, BaseUserDO userDO, String password) {
        Integer min = (Integer) policyMap.getPasswordConfig().get(TYPE);
        if (min != null && min != 0) {
            int count = 0;

            Matcher m = PATTERN.matcher(password);

            while (m.find()){
                count++;
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
