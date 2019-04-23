package io.choerodon.oauth.core.password.mapper;

import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import io.choerodon.oauth.core.password.domain.BaseLoginAttemptTimesDTO;

/**
 * @author wuguokai
 */
public interface BaseLoginAttemptTimesMapper extends Mapper<BaseLoginAttemptTimesDTO> {
    @Select("select * from oauth_login_attempt_times where user_id = #{userId}")
    BaseLoginAttemptTimesDTO findByUser(@Param("userId") Long userId);
}
