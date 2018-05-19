package io.choerodon.oauth.core.password.validator.password;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

import io.choerodon.core.exception.CommonException;
import io.choerodon.oauth.core.password.PasswordPolicyMap;
import io.choerodon.oauth.core.password.PasswordStrategy;
import io.choerodon.oauth.core.password.domain.BaseUserDO;
import io.choerodon.oauth.core.password.PasswordPolicyType;
import io.choerodon.oauth.core.password.mapper.BasePasswordHistoryMapper;

/**
 * @author wuguokai
 */
public class NotRecentStrategy implements PasswordStrategy {
    private static final String ERROR_MESSAGE = "error.password.policy.notRecent";
    public static final String TYPE = PasswordPolicyType.NOT_RECENT.getValue();
    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    private BasePasswordHistoryMapper passwordHistoryMapper;

    public NotRecentStrategy(BasePasswordHistoryMapper passwordHistoryMapper) {
        this.passwordHistoryMapper = passwordHistoryMapper;
    }

    @Override
    public Object validate(PasswordPolicyMap policyMap, BaseUserDO userDO, String password) {
        Integer count = (Integer) policyMap.getPasswordConfig().get(TYPE);
        List<String> passwordHistoryList = passwordHistoryMapper.selectPasswordByUser(userDO.getId(), count);
        passwordHistoryList.forEach(p -> {
            if (ENCODER.matches(password, p)) {
                throw new CommonException(ERROR_MESSAGE, count);
            }
        });
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
