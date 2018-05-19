package io.choerodon.oauth.core.password.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.oauth.core.password.domain.BasePasswordHistoryDO;

/**
 * @author wuguokai
 */
public interface BasePasswordHistoryMapper extends BaseMapper<BasePasswordHistoryDO> {
    @Select("SELECT PASSWORD FROM oauth_password_history WHERE user_id = #{userId} ORDER BY creation_date desc LIMIT #{count}")
    List<String> selectPasswordByUser(@Param("userId") Long userId, @Param("count") Integer count);
}
