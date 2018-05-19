package io.choerodon.oauth.core.password.service;

import io.choerodon.oauth.core.password.domain.BaseUserDO;

/**
 * @author wuguokai
 */
public interface BaseUserService {
    BaseUserDO lockUser(Long userId, long lockExpireTime);
}
