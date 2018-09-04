package io.choerodon.oauth.core.password.mapper;

import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.oauth.core.password.domain.BasePasswordPolicyDO;

/**
 * @author wuguokai
 */
public interface BasePasswordPolicyMapper extends BaseMapper<BasePasswordPolicyDO> {

    BasePasswordPolicyDO findByOrgId(@Param("orgId") Long orgId);
}
