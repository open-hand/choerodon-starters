package io.choerodon.oauth.core.password.validator.password;

import io.choerodon.core.exception.CommonException;
import io.choerodon.oauth.core.password.PasswordPolicyMap;
import io.choerodon.oauth.core.password.PasswordStrategy;
import io.choerodon.oauth.core.password.PasswordPolicyType;
import io.choerodon.oauth.core.password.domain.BaseUserDTO;

/**
 * @author wuguokai
 */
public class NotUsernameStrategy implements PasswordStrategy {
    private static final String ERROR_MESSAGE = "error.password.policy.notUsername";
    public static final String TYPE = PasswordPolicyType.NOT_USERNAME.getValue();

    @Override
    public Object validate(PasswordPolicyMap policyMap, BaseUserDTO userDO, String password) {
        Boolean notUsername = (Boolean) policyMap.getPasswordConfig().get(TYPE);
        if (notUsername) {
            String userName = userDO.getLoginName();
            if (password.equals(userName)) {
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
