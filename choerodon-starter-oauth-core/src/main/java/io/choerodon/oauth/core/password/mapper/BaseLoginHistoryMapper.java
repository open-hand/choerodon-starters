package io.choerodon.oauth.core.password.mapper;

import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import io.choerodon.oauth.core.password.domain.BaseLoginHistoryDTO;

/**
 * @author wuguokai
 */
public interface BaseLoginHistoryMapper extends Mapper<BaseLoginHistoryDTO> {
    @Select("select * from oauth_login_history where user_id = #{userId}")
    BaseLoginHistoryDTO findByUser(@Param("userId") Long userId);
}
