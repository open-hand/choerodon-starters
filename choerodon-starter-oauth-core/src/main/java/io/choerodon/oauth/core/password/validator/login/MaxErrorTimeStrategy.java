package io.choerodon.oauth.core.password.validator.login;

import io.choerodon.oauth.core.password.PasswordPolicyMap;
import io.choerodon.oauth.core.password.PasswordPolicyType;
import io.choerodon.oauth.core.password.PasswordStrategy;
import io.choerodon.oauth.core.password.domain.BaseLoginAttemptTimesDO;
import io.choerodon.oauth.core.password.domain.BaseUserDO;
import io.choerodon.oauth.core.password.mapper.BaseLoginAttemptTimesMapper;

/**
 * @author wuguokai
 */
public class MaxErrorTimeStrategy implements PasswordStrategy {
    private static final String ERROR_MESSAGE = "error.password.policy.maxErrorTime";
    private static final String TYPE = PasswordPolicyType.MAX_ERROR_TIME.getValue();
    private static final String ENABLE_LOCK_TYPE = PasswordPolicyType.ENABLE_LOCK.getValue();

    private BaseLoginAttemptTimesMapper loginAttemptTimesMapper;

    public MaxErrorTimeStrategy(BaseLoginAttemptTimesMapper loginAttemptTimesMapper) {
        this.loginAttemptTimesMapper = loginAttemptTimesMapper;
    }

    @Override
    public Boolean validate(PasswordPolicyMap policyMap, BaseUserDO userDO, String password) {
        Integer max = (Integer) policyMap.getLoginConfig().get(TYPE);
        Boolean enableLock = (Boolean) policyMap.getLoginConfig().get(ENABLE_LOCK_TYPE);
        if (enableLock) {
            BaseLoginAttemptTimesDO loginAttemptTimesDO = new BaseLoginAttemptTimesDO();
            loginAttemptTimesDO.setUserId(userDO.getId());
            loginAttemptTimesDO  = loginAttemptTimesMapper.selectOne(loginAttemptTimesDO);
            if (loginAttemptTimesDO == null){
                return true;
            }
            Integer attemptTimes = loginAttemptTimesDO.getLoginAttemptTimes();
            if (attemptTimes >= max) {
                return false;
            }
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
