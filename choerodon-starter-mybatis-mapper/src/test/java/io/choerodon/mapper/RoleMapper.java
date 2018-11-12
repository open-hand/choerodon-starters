package io.choerodon.mapper;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * @author superlee
 */
public interface RoleMapper extends BaseMapper<RoleDO> {
    int customInsert(RoleDO roleDO);
}
