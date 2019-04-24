package io.choerodon.oauth.core.password.service;

import io.choerodon.oauth.core.password.domain.BaseUserDTO;

/**
 * @author wuguokai
 */
public interface BaseUserService {
    BaseUserDTO lockUser(Long userId, long lockExpireTime);
}
