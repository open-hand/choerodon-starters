package io.choerodon.oauth.core.password.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.oauth.core.password.domain.BaseLoginAttemptTimesDO;

/**
 * @author wuguokai
 */
public interface BaseLoginAttemptTimesMapper extends BaseMapper<BaseLoginAttemptTimesDO> {
    @Select("select * from oauth_login_attempt_times where user_id = #{userId}")
    BaseLoginAttemptTimesDO findByUser(@Param("userId") Long userId);
}
