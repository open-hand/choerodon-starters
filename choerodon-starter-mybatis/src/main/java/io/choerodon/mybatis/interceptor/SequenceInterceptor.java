/*
 * #{copyright}#
 */
package io.choerodon.mybatis.interceptor;

import io.choerodon.mybatis.entity.DbType;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.scripting.defaults.RawSqlSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.mybatis.mapper.MapperException;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.entity.EntityTable;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.mapperhelper.SelectKeyGenerator;

import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * 自动数据多语言支持.
 *
 * @author shengyang.zhou@hand-china.com
 */
@Intercepts({ @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }) })
public class SequenceInterceptor implements Interceptor {
    private Logger logger = LoggerFactory.getLogger(SequenceInterceptor.class);
    private Set<String> processedStatement = new ConcurrentSkipListSet<>();
    private DbType dbType;
    public SequenceInterceptor(DbType dbType) {
        this.dbType = dbType;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object target = invocation.getTarget();
        if (target instanceof Executor) {
            MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
            Object domain = invocation.getArgs()[1];
            if (mappedStatement.getSqlCommandType() == SqlCommandType.INSERT){
                processKey(mappedStatement, domain.getClass());
            }
        }
        return invocation.proceed();
    }

    private void processKey(MappedStatement ms, Class<?> entityClass){
        try{
            if(!processedStatement.contains(ms.getId())){
                processedStatement.add(ms.getId());
                String keyId = ms.getId() + SelectKeyGenerator.SELECT_KEY_SUFFIX;
                EntityTable entityTable = EntityHelper.getEntityTable(entityClass);
                String identity;
                switch (this.dbType) {
                    case HANA:
                        identity = "SELECT " + entityTable.getName() + "_s.nextval FROM DUMMY";
                        break;
                    case POSTGRE_SQL:
                        identity = "SELECT nextval('" + entityTable.getName() + "_s')";
                        break;
                    default:
                        identity = "SELECT " + entityTable.getName() + "_s.nextval FROM DUAL";
                        break;
                }
                EntityColumn column = entityTable.getEntityClassPKColumns().iterator().next();
                SqlSource sqlSource = new RawSqlSource(ms.getConfiguration(), identity, entityClass);
                MappedStatement keyStatement = ms.getConfiguration().getMappedStatement(keyId, false);
                MetaObject keyStatementMetaObject = SystemMetaObject.forObject(keyStatement);
                keyStatementMetaObject.setValue("sqlSource", sqlSource);
                KeyGenerator keyGenerator = new SelectKeyGenerator(keyStatement, true);
                MetaObject msObject = SystemMetaObject.forObject(ms);
                msObject.setValue("keyGenerator", keyGenerator);
            }
        } catch (MapperException e){
            //ignore
        }
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {}
}
