package io.choerodon.oauth.core.password.record;

import org.springframework.beans.factory.annotation.Autowired;

import io.choerodon.core.exception.CommonException;
import io.choerodon.oauth.core.password.domain.BaseLoginAttemptTimesDO;
import io.choerodon.oauth.core.password.domain.BasePasswordHistoryDO;
import io.choerodon.oauth.core.password.mapper.BaseLoginAttemptTimesMapper;
import io.choerodon.oauth.core.password.mapper.BasePasswordHistoryMapper;

/**
 * @author wuguokai
 */
public class PasswordRecord {
    @Autowired
    private BasePasswordHistoryMapper basePasswordHistoryMapper;
    @Autowired
    private BaseLoginAttemptTimesMapper baseLoginAttemptTimesMapper;

    public void updatePassword(Long userId, String oldPassword) {
        BaseLoginAttemptTimesDO baseLoginAttemptTimesDO = baseLoginAttemptTimesMapper.findByUser(userId);
        if (baseLoginAttemptTimesDO != null) {
            baseLoginAttemptTimesDO.setLoginAttemptTimes(0);
            if (baseLoginAttemptTimesMapper.updateByPrimaryKeySelective(baseLoginAttemptTimesDO) != 1) {
                throw new CommonException("error.update.password_record");
            }
        }
        BasePasswordHistoryDO basePasswordHistoryDO = new BasePasswordHistoryDO();
        basePasswordHistoryDO.setUserId(userId);
        basePasswordHistoryDO.setPassword(oldPassword);
        if (basePasswordHistoryMapper.insertSelective(basePasswordHistoryDO) != 1) {
            throw new CommonException("error.insert.passwordHistory");
        }
    }

    public void unLockUser(Long userId) {
        BaseLoginAttemptTimesDO baseLoginAttemptTimesDO = baseLoginAttemptTimesMapper.findByUser(userId);
        if (baseLoginAttemptTimesDO != null) {
            baseLoginAttemptTimesDO.setLoginAttemptTimes(0);
            if (baseLoginAttemptTimesMapper.updateByPrimaryKeySelective(baseLoginAttemptTimesDO) != 1) {
                throw new CommonException("error.update.loginAttempt");
            }
        }
    }
}
