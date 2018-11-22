package io.choerodon.oauth.core.password.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.oauth.core.password.domain.BasePasswordHistoryDO;

/**
 * @author wuguokai
 */
public interface BasePasswordHistoryMapper extends BaseMapper<BasePasswordHistoryDO> {

    /**
     * 根据userId查询用户的密码历史记录
     * @param userId
     * @return
     */
    List<String> selectPasswordByUser(@Param("userId") Long userId);
}
