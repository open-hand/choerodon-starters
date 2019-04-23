package io.choerodon.oauth.core.password.record;

import io.choerodon.core.exception.CommonException;
import io.choerodon.oauth.core.password.domain.BaseLoginAttemptTimesDTO;
import io.choerodon.oauth.core.password.domain.BasePasswordHistoryDTO;
import io.choerodon.oauth.core.password.mapper.BaseLoginAttemptTimesMapper;
import io.choerodon.oauth.core.password.mapper.BasePasswordHistoryMapper;

/**
 * @author wuguokai
 */
public class PasswordRecord {

    private BasePasswordHistoryMapper basePasswordHistoryMapper;

    private BaseLoginAttemptTimesMapper baseLoginAttemptTimesMapper;

    public PasswordRecord(BaseLoginAttemptTimesMapper baseLoginAttemptTimesMapper,
                          BasePasswordHistoryMapper basePasswordHistoryMapper) {
        this.baseLoginAttemptTimesMapper = baseLoginAttemptTimesMapper;
        this.basePasswordHistoryMapper = basePasswordHistoryMapper;
    }

    public void updatePassword(Long userId, String oldPassword) {
        BaseLoginAttemptTimesDTO baseLoginAttemptTimesDO = baseLoginAttemptTimesMapper.findByUser(userId);
        if (baseLoginAttemptTimesDO != null) {
            baseLoginAttemptTimesDO.setLoginAttemptTimes(0);
            if (baseLoginAttemptTimesMapper.updateByPrimaryKeySelective(baseLoginAttemptTimesDO) != 1) {
                throw new CommonException("error.update.password_record");
            }
        }
        BasePasswordHistoryDTO basePasswordHistoryDO = new BasePasswordHistoryDTO();
        basePasswordHistoryDO.setUserId(userId);
        basePasswordHistoryDO.setPassword(oldPassword);
        if (basePasswordHistoryMapper.insertSelective(basePasswordHistoryDO) != 1) {
            throw new CommonException("error.insert.passwordHistory");
        }
    }

    public void unLockUser(Long userId) {
        BaseLoginAttemptTimesDTO baseLoginAttemptTimesDO = baseLoginAttemptTimesMapper.findByUser(userId);
        if (baseLoginAttemptTimesDO != null) {
            baseLoginAttemptTimesDO.setLoginAttemptTimes(0);
            if (baseLoginAttemptTimesMapper.updateByPrimaryKeySelective(baseLoginAttemptTimesDO) != 1) {
                throw new CommonException("error.update.loginAttempt");
            }
        }
    }
}
