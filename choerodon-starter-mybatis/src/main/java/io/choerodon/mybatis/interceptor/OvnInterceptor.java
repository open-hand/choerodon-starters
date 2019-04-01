/*
 * #{copyright}#
 */
package io.choerodon.mybatis.interceptor;

import io.choerodon.mybatis.common.SelectOptionsMapper;
import io.choerodon.mybatis.entity.BaseDTO;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Properties;

/**
 * 插入、更新以后,自动设置 objectversionnumber。<br>
 * 更新、删除 失败以后，抛出异常的动作，要在具体执行操作的地方自己检测。<br>
 * BaseServiceImpl 已经支持
 *
 * @author shengyang.zhou@hand-china.com
 */
@Order(1)
@Component
@Intercepts({ @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }) })
public class OvnInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) args[0];
        Object domain = args[1];
        if(domain instanceof Map){
            Map map = ((Map) domain);
            if (map.containsKey(SelectOptionsMapper.OPTIONS_DTO)) {
                domain = ((Map) domain).get(SelectOptionsMapper.OPTIONS_DTO);
            }
        }
        if (!(domain instanceof BaseDTO)) {
            return invocation.proceed();
        }
        BaseDTO baseDTO = (BaseDTO) domain;
        if (mappedStatement.getSqlCommandType() == SqlCommandType.INSERT) {
            baseDTO.setObjectVersionNumber(1L);
        }
        Object result = invocation.proceed();
        Long ovn = baseDTO.getObjectVersionNumber();
        if (ovn != null && mappedStatement.getSqlCommandType() == SqlCommandType.UPDATE){
            baseDTO.setObjectVersionNumber(ovn + 1L);
        }
        return result;
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {

    }

}
