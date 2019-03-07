package io.choerodon.mybatis.interceptor;

import io.choerodon.mybatis.common.AuditDomainSetter;
import io.choerodon.mybatis.entity.BaseDTO;
import io.choerodon.mybatis.util.OGNL;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import tk.mybatis.mapper.MapperException;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.entity.EntityTable;
import tk.mybatis.mapper.mapperhelper.EntityHelper;

import java.util.Date;
import java.util.Map;
import java.util.Properties;

/**
 * 在更新之前自动添加四个审计字段的值， creationDate， createdBy， lastUpdateDate， lastUpdatedBy
 */
@Intercepts({ @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }) })
public class AuditInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object parameter = invocation.getArgs()[1];
        MappedStatement statement = (MappedStatement) invocation.getArgs()[0];
        if (parameter instanceof BaseDTO){
            switch (statement.getSqlCommandType()){
                case INSERT:
                    ((BaseDTO) parameter).setCreatedBy(OGNL.principal());
                    ((BaseDTO) parameter).setCreationDate(new Date());
                case UPDATE:
                    ((BaseDTO) parameter).setLastUpdatedBy(OGNL.principal());
                    ((BaseDTO) parameter).setLastUpdateDate(new Date());
            }
        }
        return invocation.proceed();
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
