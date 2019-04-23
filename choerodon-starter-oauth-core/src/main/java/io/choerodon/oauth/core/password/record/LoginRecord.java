package io.choerodon.oauth.core.password.record;

import io.choerodon.core.exception.CommonException;
import io.choerodon.oauth.core.password.domain.BaseLoginAttemptTimesDTO;
import io.choerodon.oauth.core.password.domain.BaseLoginHistoryDTO;
import io.choerodon.oauth.core.password.mapper.BaseLoginAttemptTimesMapper;
import io.choerodon.oauth.core.password.mapper.BaseLoginHistoryMapper;

import java.util.Date;

/**
 * @author wuguokai
 */
public class LoginRecord {

    private BaseLoginAttemptTimesMapper baseLoginAttemptTimesMapper;

    private BaseLoginHistoryMapper baseLoginHistoryMapper;

    public LoginRecord(BaseLoginAttemptTimesMapper baseLoginAttemptTimesMapper, BaseLoginHistoryMapper baseLoginHistoryMapper) {
        this.baseLoginAttemptTimesMapper = baseLoginAttemptTimesMapper;
        this.baseLoginHistoryMapper = baseLoginHistoryMapper;
    }

    public void loginError(Long userId) {
        BaseLoginAttemptTimesDTO baseLoginAttemptTimesDO = baseLoginAttemptTimesMapper.findByUser(userId);
        if (baseLoginAttemptTimesDO == null) {
            baseLoginAttemptTimesDO = new BaseLoginAttemptTimesDTO();
            baseLoginAttemptTimesDO.setUserId(userId);
            baseLoginAttemptTimesDO.setLoginAttemptTimes(1);
            if (baseLoginAttemptTimesMapper.insertSelective(baseLoginAttemptTimesDO) != 1) {
                throw new CommonException("error.insert.loginAttempt");
            }
        } else {
            baseLoginAttemptTimesDO.setLoginAttemptTimes(baseLoginAttemptTimesDO.getLoginAttemptTimes() + 1);
            if (baseLoginAttemptTimesMapper.updateByPrimaryKeySelective(baseLoginAttemptTimesDO) != 1) {
                throw new CommonException("error.update.loginAttempt");
            }
        }
    }

    public void loginSuccess(Long userId) {
        BaseLoginAttemptTimesDTO baseLoginAttemptTimesDO = baseLoginAttemptTimesMapper.findByUser(userId);
        BaseLoginHistoryDTO baseLoginHistoryDO = baseLoginHistoryMapper.findByUser(userId);
        if (baseLoginHistoryDO == null) {
            baseLoginHistoryDO = new BaseLoginHistoryDTO();
            baseLoginHistoryDO.setUserId(userId);
            baseLoginHistoryDO.setLastLoginAt(new Date());
            if (baseLoginHistoryMapper.insertSelective(baseLoginHistoryDO) != 1) {
                throw new CommonException("error.insert.loginHistory");
            }
        } else {
            baseLoginHistoryDO.setLastLoginAt(new Date());
            if (baseLoginHistoryMapper.updateByPrimaryKeySelective(baseLoginHistoryDO) != 1) {
                throw new CommonException("error.update.loginHistory");
            }
        }
        if (baseLoginAttemptTimesDO != null) {
            baseLoginAttemptTimesDO.setLoginAttemptTimes(0);
            if (baseLoginAttemptTimesMapper.updateByPrimaryKeySelective(baseLoginAttemptTimesDO) != 1) {
                throw new CommonException("error.update.loginAttempt");
            }
        }
    }
}
