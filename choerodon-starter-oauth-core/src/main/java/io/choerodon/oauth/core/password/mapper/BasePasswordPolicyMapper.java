package io.choerodon.oauth.core.password.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.oauth.core.password.domain.BasePasswordPolicyDO;

/**
 * @author wuguokai
 */
public interface BasePasswordPolicyMapper extends BaseMapper<BasePasswordPolicyDO> {
    @Select("select * from oauth_password_policy where organization_id = #{orgId} limit 1")
    BasePasswordPolicyDO findByOrgId(@Param("orgId") Long orgId);
}
