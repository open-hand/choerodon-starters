package io.choerodon.oauth.core.password.validator.login;

import io.choerodon.oauth.core.password.PasswordPolicyMap;
import io.choerodon.oauth.core.password.PasswordPolicyType;
import io.choerodon.oauth.core.password.PasswordStrategy;
import io.choerodon.oauth.core.password.domain.BaseLoginAttemptTimesDTO;
import io.choerodon.oauth.core.password.domain.BaseUserDTO;
import io.choerodon.oauth.core.password.mapper.BaseLoginAttemptTimesMapper;

/**
 * @author wuguokai
 */
public class MaxErrorTimeStrategy implements PasswordStrategy {
    private static final String TYPE = PasswordPolicyType.MAX_ERROR_TIME.getValue();
    private static final String ENABLE_LOCK_TYPE = PasswordPolicyType.ENABLE_LOCK.getValue();

    private BaseLoginAttemptTimesMapper loginAttemptTimesMapper;

    public MaxErrorTimeStrategy(BaseLoginAttemptTimesMapper loginAttemptTimesMapper) {
        this.loginAttemptTimesMapper = loginAttemptTimesMapper;
    }

    @Override
    public Boolean validate(PasswordPolicyMap policyMap, BaseUserDTO userDO, String password) {
        Integer max = (Integer) policyMap.getLoginConfig().get(TYPE);
        Boolean enableLock = (Boolean) policyMap.getLoginConfig().get(ENABLE_LOCK_TYPE);
        if (enableLock) {
            BaseLoginAttemptTimesDTO loginAttemptTimesDO = new BaseLoginAttemptTimesDTO();
            loginAttemptTimesDO.setUserId(userDO.getId());
            loginAttemptTimesDO = loginAttemptTimesMapper.selectOne(loginAttemptTimesDO);
            if (loginAttemptTimesDO == null) {
                return true;
            }
            return loginAttemptTimesDO.getLoginAttemptTimes() < max;
        }
        return true;
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
