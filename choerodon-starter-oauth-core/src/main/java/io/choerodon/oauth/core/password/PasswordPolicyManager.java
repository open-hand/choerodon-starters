package io.choerodon.oauth.core.password;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.choerodon.core.exception.CommonException;
import io.choerodon.oauth.core.password.domain.BaseLoginAttemptTimesDO;
import io.choerodon.oauth.core.password.domain.BaseUserDO;
import io.choerodon.oauth.core.password.domain.BasePasswordPolicyDO;
import io.choerodon.oauth.core.password.mapper.BaseLoginAttemptTimesMapper;

/**
 * @author wuguokai
 */
public class PasswordPolicyManager {
    private final String ERROR_EMPTY = "error.password.null";

    @Autowired
    private PasswordStrategyStore passwordStrategyStore;
    @Autowired
    private BaseLoginAttemptTimesMapper baseLoginAttemptTimesMapper;

    @Transactional(rollbackFor = Exception.class)
    public void passwordValidate(String password, BaseUserDO userDO, BasePasswordPolicyDO policy){
        if (password == null) {
            throw new CommonException(ERROR_EMPTY);
        }
        if (policy == null) {
            return;
        }
        PasswordPolicyMap passwordPolicyMap = PasswordPolicyMap.parse(passwordStrategyStore, policy);
        if (passwordPolicyMap.isEnablePassword()) {
            for (PasswordStrategy p : getPasswordProviders(passwordPolicyMap, passwordStrategyStore)) {
                p.validate(passwordPolicyMap, userDO, password);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean isNeedCaptcha(BasePasswordPolicyDO policy, BaseUserDO baseUserDO){
        if (policy == null) {
            return false;
        }
        PasswordPolicyMap passwordPolicyMap = PasswordPolicyMap.parse(passwordStrategyStore, policy);
        if (passwordPolicyMap.isEnableSecurity()) {
            Object enableCaptcha = passwordPolicyMap.getLoginConfig().get(PasswordPolicyType.ENABLE_CAPTCHA.getValue());
            if (enableCaptcha != null && (Boolean) enableCaptcha) {
                Integer maxCaptchaTime = (Integer) passwordPolicyMap.getLoginConfig().get(PasswordPolicyType.MAX_CHECK_CAPTCHA.getValue());
                if (maxCaptchaTime == 0) {
                    return true;
                }
                if (baseUserDO != null) {
                    BaseLoginAttemptTimesDO baseLoginAttemptTimesDO = baseLoginAttemptTimesMapper.findByUser(baseUserDO.getId());
                    if (baseLoginAttemptTimesDO != null && baseLoginAttemptTimesDO.getLoginAttemptTimes() >= maxCaptchaTime) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    public Map loginValidate(String password, BaseUserDO userDO, BasePasswordPolicyDO policy){
        Map<String, Object> returnMap = new HashMap<>();
        if (password == null) {
            throw new CommonException(ERROR_EMPTY);
        }
        if (policy == null) {
            return null;
        }
        PasswordPolicyMap passwordPolicyMap = PasswordPolicyMap.parse(passwordStrategyStore, policy);
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
