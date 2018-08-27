package io.choerodon.config.mapper;

import io.choerodon.config.domain.ServiceConfig;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * config的数据库操作map
 *
 * @author wuguokai
 */
public interface ServiceConfigMapper extends BaseMapper<ServiceConfig> {
    /**
     * 通过serviceName查询Config对象信息
     *
     * @param serviceName 服务名
     * @return Config对象
     */
//    ServiceConfig selectOneByServiceDefault(@Param("serviceName") String serviceName);
}
