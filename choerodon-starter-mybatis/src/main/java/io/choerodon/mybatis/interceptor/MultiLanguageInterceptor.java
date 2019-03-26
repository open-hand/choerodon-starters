/*
 * #{copyright}#
 */
package io.choerodon.mybatis.interceptor;

import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.entity.BaseConstants;
import io.choerodon.mybatis.entity.BaseDTO;
import io.choerodon.mybatis.entity.Criteria;
import io.choerodon.mybatis.entity.CustomEntityColumn;
import io.choerodon.mybatis.entity.CustomEntityTable;
import io.choerodon.mybatis.util.OGNL;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.entity.EntityField;
import tk.mybatis.mapper.mapperhelper.EntityHelper;

import javax.persistence.Table;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.notNull;

/**
 * 自动数据多语言支持.
 *
 * @author shengyang.zhou@hand-china.com
 */
@Intercepts({ @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }) })
public class MultiLanguageInterceptor implements Interceptor {

    private Logger logger = LoggerFactory.getLogger(MultiLanguageInterceptor.class);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        Object target = invocation.getTarget();
        if (target instanceof Executor) {
            MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
            Object domain = invocation.getArgs()[1];
            Criteria criteria = null;
            if(domain instanceof MapperMethod.ParamMap){
                Map map = ((Map) domain);
                if (map.containsKey(BaseConstants.OPTIONS_CRITERIA)) {
                    criteria = (Criteria) ((Map) domain).get(BaseConstants.OPTIONS_CRITERIA);
                    domain = ((Map) domain).get(BaseConstants.OPTIONS_DTO);
                }
            }
            if (domain instanceof BaseDTO){
                BaseDTO dtoObj = (BaseDTO) domain;
                if (mappedStatement.getSqlCommandType() == SqlCommandType.INSERT
                        || mappedStatement.getSqlCommandType() == SqlCommandType.UPDATE) {
                    Object obj = invocation.proceed();
                    proceedMultiLanguage(dtoObj, invocation, mappedStatement,criteria);
                    return obj;
                } else if (mappedStatement.getSqlCommandType() == SqlCommandType.DELETE) {
                    Object obj = invocation.proceed();
                    proceedDeleteMultiLanguage(dtoObj, invocation);
                    return obj;
                }
            }
            return invocation.proceed();
        }
        return invocation.proceed();
    }

    private void proceedMultiLanguage(BaseDTO parameterObject, Invocation invocation, MappedStatement mappedStatement,Criteria criteria)
            throws Exception {
        Set<String> updateFields = criteria !=null ? criteria.getUpdateFields():null;
        Class<?> clazz = parameterObject.getClass();
        MultiLanguage multiLanguageTable = clazz.getAnnotation(MultiLanguage.class);
        if (multiLanguageTable == null) {
            return;
        }
        Table table = clazz.getAnnotation(Table.class);
        notNull(table, "annotation @Table not found!");
        String tableName = table.name();
        hasText(tableName, "@Table name not found!");
        tableName = getTlTableName(tableName);
        if (mappedStatement.getSqlCommandType() == SqlCommandType.INSERT) {
            proceedInsertMultiLanguage(tableName, parameterObject, (Executor) invocation.getTarget());
        } else if (mappedStatement.getSqlCommandType() == SqlCommandType.UPDATE) {
            if (parameterObject.get__tls().isEmpty()) {
                proceedUpdateMultiLanguage(tableName, parameterObject, (Executor) invocation.getTarget(),updateFields);
            } else {
                proceedUpdateMultiLanguage2(tableName, parameterObject, (Executor) invocation.getTarget(),updateFields);
            }
        }
    }

    private static String getTlTableName(String tableName){
        return tableName.substring(0, tableName.length() - 2) + "_tl";
    }

    private void proceedDeleteMultiLanguage(BaseDTO parameterObject, Invocation invocation)
            throws Exception {
        Class<?> clazz = parameterObject.getClass();
        MultiLanguage multiLanguageTable = clazz.getAnnotation(MultiLanguage.class);
        if (multiLanguageTable == null) {
            return;
        }
        Table table = clazz.getAnnotation(Table.class);
        notNull(table, "annotation @Table not found!");
        String tableName = table.name();
        hasText(tableName, "@Table name not found!");
        tableName = getTlTableName(tableName);

        List<Object> objs = new ArrayList<>();
        List<String> keys = new ArrayList<>();
        for (EntityColumn c : EntityHelper.getEntityTable(clazz).getEntityClassPKColumns()) {
            EntityField f = c.getEntityField();
            Object v = f.getValue(parameterObject);
            keys.add(c.getColumn() + "=?");
            objs.add(v);
        }
        for (Object pkv : objs) {
            if (pkv == null) {
                // 主键中有 null
                return;
            }
        }
        if (keys.size() > 0) {
            Executor executor = (Executor) invocation.getTarget();
            StringBuilder sql = new StringBuilder("DELETE FROM ");
            sql.append(tableName).append(" WHERE ").append(String.join(" AND ",keys));
            executeSql(executor.getTransaction().getConnection(), sql.toString(), objs);
        }
    }

    private void proceedInsertMultiLanguage(String tableName, BaseDTO parameterObject, Executor executor)
            throws Exception {

        Class<?> clazz = parameterObject.getClass();
        List<String> keys = new ArrayList<>();
        List<Object> objs = new ArrayList<>();
        List<String> placeholders = new ArrayList<>();
        StringBuilder sql = new StringBuilder("INSERT INTO " + tableName + "(");
        for (EntityColumn c : EntityHelper.getEntityTable(clazz).getEntityClassPKColumns()) {
            EntityField f = c.getEntityField();
            keys.add(c.getColumn());
            placeholders.add("?");
            objs.add(SystemMetaObject.forObject(parameterObject).getValue(f.getName()));
        }
        keys.add("LANG");
        placeholders.add("?");
        objs.add(null); // 占位符

        for (EntityColumn column : EntityHelper.getEntityTable(clazz).getEntityClassColumns()) {
            CustomEntityColumn customEntityColumn = (CustomEntityColumn) column;
            if(customEntityColumn.isMultiLanguage()){
                keys.add(column.getColumn());
                placeholders.add("?");
                Map<String, String> tls = parameterObject.get__tls().get(column.getProperty());
                if (tls == null) {
                    // if multi language value not exists in __tls, then use
                    // value on current field
                    objs.add(column.getEntityField().getValue(parameterObject));
                    continue;
                }
                objs.add(null); // 占位符
            }
        }
        keys.add("CREATED_BY");
        placeholders.add("" + parameterObject.getCreatedBy());

        keys.add("CREATION_DATE");
        placeholders.add("CURRENT_TIMESTAMP");

        keys.add("LAST_UPDATED_BY");
        placeholders.add("" + parameterObject.getCreatedBy());

        keys.add("LAST_UPDATE_DATE");
        placeholders.add("CURRENT_TIMESTAMP");

        sql.append(String.join( ",", keys));
        sql.append(") VALUES (").append(String.join(",", placeholders)).append(")");

        EntityField[] mlFields = ((CustomEntityTable) EntityHelper.getEntityTable(clazz)).getMultiLanguageColumns().stream().map(EntityColumn::getEntityField).toArray(EntityField[]::new);
        Set<String> languages = OGNL.getSupportedLanguages();
        for (String language : languages) {
            objs.set(objs.size() - mlFields.length - 1, language);
            for (int i = 0; i < mlFields.length; i++) {
                int idx = objs.size() - mlFields.length + i;
                Map<String, String> tls = parameterObject.get__tls().get(mlFields[i].getName());
                if (tls != null) {
                    objs.set(idx, tls.get(language));
                }
                // 当tls为null时,不设置值(使用field的值,旧模式)
            }

            if (logger.isDebugEnabled()) {
                logger.debug("Insert TL(Batch):{}", sql.toString());
                logger.debug("Parameters:{}", objs);
            }
            executeSql(executor.getTransaction().getConnection(), sql.toString(), objs);
        }
    }

    private void proceedUpdateMultiLanguage(String tableName, BaseDTO parameterObject, Executor executor,Set<String> updateFields)
            throws Exception {
        Class<?> clazz = parameterObject.getClass();
        List<String> sets = new ArrayList<>();
        List<Object> objs = new ArrayList<>();
        List<String> keys = new ArrayList<>();
        StringBuilder sql = new StringBuilder("UPDATE " + tableName + " SET ");
        for (EntityColumn column : EntityHelper.getEntityTable(clazz).getEntityClassColumns()) {
            CustomEntityColumn customEntityColumn = (CustomEntityColumn) column;
            if(customEntityColumn.isMultiLanguage()) {
                EntityField field = column.getEntityField();
                if (null != updateFields && !updateFields.isEmpty() && !updateFields.contains(field.getName())) {
                    continue;
                }
                Object value = field.getValue(parameterObject);
                if (value == null) {
                    continue;
                }
                sets.add(column.getColumn() + "=?");
                objs.add(value);
            }
        }
        if (sets.isEmpty()) {
            if (logger.isDebugEnabled()) {
                logger.debug("None multi language field has TL value. skip update.");
            }
            return;
        }

        sets.add("LAST_UPDATED_BY=" + parameterObject.getLastUpdatedBy());
        sets.add("LAST_UPDATE_DATE=CURRENT_TIMESTAMP");

        for (EntityColumn column : EntityHelper.getEntityTable(clazz).getEntityClassPKColumns()) {
            EntityField field = column.getEntityField();
            keys.add(column.getColumn() + "=?");
            objs.add(field.getValue(parameterObject));
        }
        keys.add("LANG=?");
        objs.add(OGNL.language());

        sql.append(String.join(",", sets));
        sql.append(" WHERE ").append(String.join( " AND ", keys));
        if (logger.isDebugEnabled()) {
            logger.debug("Update TL(Classic):{}", sql.toString());
            logger.debug("Parameters:{}", objs);
        }

        Connection connection = executor.getTransaction().getConnection();
        int updateCount = executeSql(connection, sql.toString(), objs);
        if (updateCount < 1) {
            if (logger.isWarnEnabled()) {
                logger.warn("Update TL failed(Classic). update count:" + updateCount);
            }
            doInsertForMissingTlData(tableName, OGNL.language(), parameterObject, connection);
        }
    }

    private void proceedUpdateMultiLanguage2(String tableName, BaseDTO parameterObject, Executor executor,Set<String> updateFields)
            throws Exception {

        Class<?> clazz = parameterObject.getClass();
        List<String> sets = new ArrayList<>();
        List<String> updateFieldNames = new ArrayList<>();

        List<Object> objs = new ArrayList<>();
        List<String> keys = new ArrayList<>();
        StringBuilder sql = new StringBuilder("UPDATE " + tableName + " SET ");
        for (EntityColumn column : EntityHelper.getEntityTable(clazz).getEntityClassColumns()) {
            CustomEntityColumn customEntityColumn = (CustomEntityColumn) column;
            if(customEntityColumn.isMultiLanguage()) {
                if (null != updateFields && !updateFields.isEmpty() && !updateFields.contains(column.getProperty())) {
                    continue;
                }
                Map<String, String> tls = parameterObject.get__tls().get(column.getProperty());
                if (tls == null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("TL value for field '{}' not exists.", column.getProperty());
                    }
                    // if tl value not exists in __tls, skip.
                    continue;
                }
                sets.add(column.getColumn() + "=?");
                updateFieldNames.add(column.getProperty());
                objs.add(null); // just a placeholder
            }
        }
        if (sets.isEmpty()) {
            if (logger.isDebugEnabled()) {
                logger.debug("None multi language field has TL value. skip update.");
                return;
            }
        }

        sets.add("LAST_UPDATED_BY=" + parameterObject.getLastUpdatedBy());
        sets.add("LAST_UPDATE_DATE=CURRENT_TIMESTAMP");

        for (EntityColumn column : EntityHelper.getEntityTable(clazz).getEntityClassPKColumns()) {
            keys.add(column.getColumn() + "=?");
            objs.add(column.getEntityField().getValue(parameterObject));
        }
        keys.add("LANG=?");
        objs.add(null); // just a place holder

        sql.append(String.join(",", sets));
        sql.append(" WHERE ").append(String.join(" AND ", keys));

        Connection connection = executor.getTransaction().getConnection();

        Set<String> languages = OGNL.getSupportedLanguages();
        for (String language : languages) {
            // 前面几个参数都是多语言数据,需要每次更新
            for (int i = 0; i < updateFieldNames.size(); i++) {
                Map<String, String> tls = parameterObject.get__tls().get(updateFieldNames.get(i));
                objs.set(i, tls.get(language));
            }

            // 最后一个参数是语言环境
            objs.set(objs.size() - 1, language);

            if (logger.isDebugEnabled()) {
                logger.debug("Update TL(Batch):{}", sql.toString());
                logger.debug("Parameters:{}", objs, ", ");
            }
            int updateCount = executeSql(connection, sql.toString(), objs);
            if (updateCount < 1) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Update TL failed(Batch). update count:{},lang:{}", updateCount, language);
                }
                doInsertForMissingTlData(tableName, language, parameterObject, connection);
            }
        }
    }

    private void doInsertForMissingTlData(String tableName, String lang, BaseDTO parameterObject, Connection connection)
            throws Exception {

        Class clazz = parameterObject.getClass();
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ").append(tableName).append(" (");
        List<Object> values = new ArrayList<>();
        int pn = 0;
        for (EntityColumn column : EntityHelper.getEntityTable(clazz).getEntityClassPKColumns()) {
            sb.append(column.getColumn()).append(",");
            values.add(column.getEntityField().getValue(parameterObject));
            pn++;
        }
        sb.append("LANG");
        pn++;
        values.add(lang);
        Map<String, Map<String, String>> tls = parameterObject.get__tls();
        for (EntityColumn column : EntityHelper.getEntityTable(clazz).getEntityClassColumns()) {
            CustomEntityColumn customEntityColumn = (CustomEntityColumn) column;
            if(customEntityColumn.isMultiLanguage()) {
                sb.append(",").append(column.getColumn());
                if (tls != null && tls.get(column.getProperty()) != null) {
                    values.add(tls.get(column.getProperty()).get(lang));
                } else {
                    values.add(column.getEntityField().getValue(parameterObject));
                }
                pn++;
            }
        }
        sb.append(",CREATED_BY");
        values.add(parameterObject.getCreatedBy());
        sb.append(",CREATION_DATE");
        sb.append(",LAST_UPDATED_BY");
        values.add(parameterObject.getLastUpdatedBy());
        sb.append(",LAST_UPDATE_DATE");

        sb.append(") VALUES (");
        for (int i = 0; i < pn; i++) {
            sb.append("?,");
        }
        sb.append("?");
        sb.append(",CURRENT_TIMESTAMP");
        sb.append(",?");
        sb.append(",CURRENT_TIMESTAMP");
        sb.append(")");

        if (logger.isDebugEnabled()) {
            logger.debug("Insert Missing TL record:" + sb.toString());
            logger.debug("Parameters: {}", values);
        }

        executeSql(connection, sb.toString(), values);

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

    protected int executeSql(Connection connection, String sql, List<Object> params) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int i = 1;
            for (Object obj : params) {
                ps.setObject(i++, obj);
            }
            ps.execute();
            return ps.getUpdateCount();
        }
    }


}
