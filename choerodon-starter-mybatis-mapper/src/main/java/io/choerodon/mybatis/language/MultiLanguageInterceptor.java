/*
 * #{copyright}#
 */

package io.choerodon.mybatis.language;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import io.choerodon.mybatis.domain.EntityColumn;
import io.choerodon.mybatis.domain.EntityTable;
import io.choerodon.mybatis.helper.EntityHelper;
import io.choerodon.mybatis.helper.LanguageHelper;
import io.choerodon.mybatis.helper.MapperTemplate;
import io.choerodon.mybatis.util.StringUtil;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * 自动数据多语言支持.
 *
 * @author superleader8@gmail.com
 */
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class MultiLanguageInterceptor implements Interceptor {
    private Logger logger = LoggerFactory.getLogger(MultiLanguageInterceptor.class);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object target = invocation.getTarget();
        if (target instanceof Executor) {
            Executor executor = (Executor) target;
            MappedStatement statement = (MappedStatement) invocation.getArgs()[0];
            Object parameter = invocation.getArgs()[1];
            EntityTable table = EntityHelper.getTableByMapper(MapperTemplate.getMapperClassName(statement.getId()));
            if (table != null && table.isMultiLanguage()) {
                Object obj = invocation.proceed();
                Connection connection = executor.getTransaction().getConnection();
                switch (statement.getSqlCommandType()) {
                    case INSERT:
                        insertMultiLanguage(table, parameter, connection);
                        break;
                    case UPDATE:
                        updateMultiLanguage(table, parameter, connection);
                        break;
                    case DELETE:
                        proceedDeleteMultiLanguage(table, parameter, connection);
                        break;
                    default:
                        break;
                }
                return obj;
            }
        }
        return invocation.proceed();
    }

    private void proceedDeleteMultiLanguage(EntityTable table, Object parameter, Connection connection)
            throws SQLException {
        List<Object> objs = new ArrayList<>();
        List<String> keys = new ArrayList<>();
        for (EntityColumn column : table.getEntityClassPkColumns()) {
            keys.add(column.getColumn() + "=?");
            if (column.getJavaType().equals(parameter.getClass())) {
                // deleteByPrimaryKey 的情况
                objs.add(parameter);
            } else {
                objs.add(column.getField().get(parameter));
            }
        }
        if (!keys.isEmpty()) {
            executeSql(connection, "DELETE FROM "
                    + table.getMultiLanguageTableName()
                    + " WHERE " + StringUtil.join(keys, " AND "), objs);
        }
    }

    private void insertMultiLanguage(EntityTable table, Object parameterObject, Connection connection)
            throws SQLException {
        List<String> keys = new ArrayList<>();
        List<Object> objs = new ArrayList<>();
        List<String> placeholders = new ArrayList<>();
        StringBuilder sql = new StringBuilder("INSERT INTO " + table.getMultiLanguageTableName() + "(");
        for (EntityColumn column : table.getEntityClassPkColumns()) {
            placeholders.add("?");
            keys.add(column.getColumn());
            objs.add(column.getField().get(parameterObject));
        }
        keys.add("lang");
        placeholders.add("?");
        objs.add(LanguageHelper.language());

        for (EntityColumn column : table.getMultiLanguageColumns()) {
            placeholders.add("?");
            keys.add(column.getColumn());
            objs.add(column.getField().get(parameterObject));
        }
        sql.append(StringUtil.join(keys, ","));
        sql.append(") VALUES (").append(StringUtil.join(placeholders, ",")).append(")");
        executeSql(connection, sql.toString(), objs);
    }

    private void insertMultiLanguage(EntityTable table, String lang, Object parameter, Connection connection)
            throws SQLException {
        StringBuilder builder = new StringBuilder();
        List<Object> values = new ArrayList<>();
        List<Object> keys = new ArrayList<>();
        List<String> placeholders = new ArrayList<>();
        for (EntityColumn column : table.getEntityClassPkColumns()) {
            values.add(column.getField().get(parameter));
            keys.add(column.getColumn());
            placeholders.add("?");
        }
        for (EntityColumn column : table.getMultiLanguageColumns()) {
            values.add(column.getField().get(parameter));
            keys.add(column.getColumn());
            placeholders.add("?");
        }
        keys.add("lang");
        values.add(lang);
        placeholders.add("?");

        builder.append("INSERT INTO ").append(table.getMultiLanguageTableName()).append(" (");
        builder.append(StringUtil.join(keys, ","));
        builder.append(") VALUES (");
        builder.append(StringUtil.join(placeholders, ","));
        builder.append(")");
        logger.debug("Insert missing multi language record: {} ,parameters: {}", builder, values);
        executeSql(connection, builder.toString(), values);
    }

    private void updateMultiLanguage(EntityTable table, Object parameter, Connection connection) throws SQLException {
        List<String> sets = new ArrayList<>();
        List<Object> objs = new ArrayList<>();
        List<String> keys = new ArrayList<>();
        StringBuilder sql = new StringBuilder("UPDATE " + table.getMultiLanguageTableName() + " SET ");
        for (EntityColumn column : table.getMultiLanguageColumns()) {
            Object value = column.getField().get(parameter);
            if (value == null) {
                continue;
            }
            objs.add(value);
            sets.add(column.getColumn() + "=?");
        }
        if (sets.isEmpty()) {
            logger.debug("None multi language field has multi language value. skip update.");
            return;
        }
        for (EntityColumn column : table.getEntityClassPkColumns()) {
            keys.add(column.getColumn() + "=?");
            objs.add(column.getField().get(parameter));
        }
        keys.add("lang=?");
        objs.add(LanguageHelper.language());
        sql.append(StringUtil.join(sets, ","));
        sql.append(" WHERE ").append(StringUtil.join(keys, " AND "));
        if (logger.isDebugEnabled()) {
            logger.debug("Update TL(Classic):{}", sql.toString());
            logger.debug("Parameters:{}", StringUtil.join(objs, ","));
        }
        int updateCount = executeSql(connection, sql.toString(), objs);
        if (updateCount < 1) {
            logger.warn("Update multi language failed. update count: {}", updateCount);
            insertMultiLanguage(table, LanguageHelper.language(), parameter, connection);
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
    public void setProperties(Properties properties) {
        // no need properties
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
