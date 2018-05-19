package io.choerodon.oauth.core.password.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.oauth.core.password.domain.BaseLoginHistoryDO;

/**
 * @author wuguokai
 */
public interface BaseLoginHistoryMapper extends BaseMapper<BaseLoginHistoryDO> {
    @Select("select * from oauth_login_history where user_id = #{userId}")
    BaseLoginHistoryDO findByUser(@Param("userId") Long userId);
}
