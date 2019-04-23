package io.choerodon.oauth.core.password.mapper;

import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

import io.choerodon.oauth.core.password.domain.BasePasswordHistoryDTO;

/**
 * @author wuguokai
 */
public interface BasePasswordHistoryMapper extends Mapper<BasePasswordHistoryDTO> {

    /**
     * 根据userId查询用户的密码历史记录
     * @param userId
     * @return
     */
    List<String> selectPasswordByUser(@Param("userId") Long userId);
}
