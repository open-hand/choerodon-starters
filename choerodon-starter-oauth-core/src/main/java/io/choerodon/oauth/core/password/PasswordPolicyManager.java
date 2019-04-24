package io.choerodon.oauth.core.password;

import io.choerodon.core.exception.CommonException;
import io.choerodon.oauth.core.password.domain.BaseLoginAttemptTimesDTO;
import io.choerodon.oauth.core.password.domain.BasePasswordPolicyDTO;
import io.choerodon.oauth.core.password.domain.BaseUserDTO;
import io.choerodon.oauth.core.password.mapper.BaseLoginAttemptTimesMapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author wuguokai
 */
public class PasswordPolicyManager {

    private static final String ERROR_EMPTY = "error.password.null";

    private PasswordStrategyStore passwordStrategyStore;

    private BaseLoginAttemptTimesMapper baseLoginAttemptTimesMapper;

    public PasswordPolicyManager(PasswordStrategyStore passwordStrategyStore,
                                 BaseLoginAttemptTimesMapper baseLoginAttemptTimesMapper) {
        this.passwordStrategyStore = passwordStrategyStore;
        this.baseLoginAttemptTimesMapper = baseLoginAttemptTimesMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    public void passwordValidate(String password, BaseUserDTO userDO, BasePasswordPolicyDTO policy) {
        if (password == null) {
            throw new CommonException(ERROR_EMPTY);
        }
        if (policy == null) {
            return;
        }
        PasswordPolicyMap passwordPolicyMap = PasswordPolicyMap.parse(policy);
        if (passwordPolicyMap.isEnablePassword()) {
            for (PasswordStrategy p : getPasswordProviders(passwordPolicyMap, passwordStrategyStore)) {
                p.validate(passwordPolicyMap, userDO, password);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean isNeedCaptcha(BasePasswordPolicyDTO policy, BaseUserDTO baseUserDO) {
        if (policy == null) {
            return false;
        }
        PasswordPolicyMap passwordPolicyMap = PasswordPolicyMap.parse(policy);
        if (passwordPolicyMap.isEnableSecurity()) {
            Object enableCaptcha = passwordPolicyMap.getLoginConfig().get(PasswordPolicyType.ENABLE_CAPTCHA.getValue());
            if (enableCaptcha != null && (Boolean) enableCaptcha) {
                Integer maxCaptchaTime = (Integer) passwordPolicyMap.getLoginConfig().get(PasswordPolicyType.MAX_CHECK_CAPTCHA.getValue());
                if (maxCaptchaTime == 0) {
                    return true;
                }
                if (baseUserDO != null) {
                    BaseLoginAttemptTimesDTO baseLoginAttemptTimesDO = baseLoginAttemptTimesMapper.findByUser(baseUserDO.getId());
                    return baseLoginAttemptTimesDO != null && baseLoginAttemptTimesDO.getLoginAttemptTimes() >= maxCaptchaTime;
                }
            }
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    public Map loginValidate(String password, BaseUserDTO userDO, BasePasswordPolicyDTO policy) {
        Map<String, Object> returnMap = new HashMap<>();
        if (password == null) {
            throw new CommonException(ERROR_EMPTY);
        }
        if (policy == null) {
            return null;
        }
        PasswordPolicyMap passwordPolicyMap = PasswordPolicyMap.parse(policy);
        if (passwordPolicyMap.isEnableSecurity()) {
            for (PasswordStrategy p : getLoginProviders(passwordPolicyMap, passwordStrategyStore)) {
                Object returnValue = p.validate(passwordPolicyMap, userDO, password);
                if (returnValue != null) {
                    returnMap.put(p.getType(), returnValue);
                }
            }
        }
        return returnMap;
    }

    private List<PasswordStrategy> getLoginProviders(PasswordPolicyMap policy, PasswordStrategyStore store) {
        LinkedList<PasswordStrategy> list = new LinkedList<>();
        for (String id : policy.getLoginPolicies()) {
            PasswordStrategy provider = store.getProvider(id);
            if (provider != null)
                list.add(provider);
        }
        return list;
    }

    private List<PasswordStrategy> getPasswordProviders(PasswordPolicyMap policy, PasswordStrategyStore store) {
        LinkedList<PasswordStrategy> list = new LinkedList<>();
        for (String id : policy.getPasswordPolicies()) {
            PasswordStrategy provider = store.getProvider(id);
            if (provider != null)
                list.add(provider);
        }
        return list;
    }
}
