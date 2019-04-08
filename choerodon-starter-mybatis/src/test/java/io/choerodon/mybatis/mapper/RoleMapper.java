package io.choerodon.mybatis.mapper;


import io.choerodon.mybatis.dto.Role;
import io.choerodon.mybatis.common.Mapper;

import java.util.Map;

/**
 * @author superlee
 */
public interface RoleMapper extends Mapper<Role> {
    Map selectTest();
}
