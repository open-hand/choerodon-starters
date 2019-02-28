package io.choerodon.mybatis.mapperhelper;

import io.choerodon.mybatis.entity.MultiLanguageEntityColumn;
import tk.mybatis.mapper.annotation.Version;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.entity.EntityTable;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.mapperhelper.SqlHelper;
import tk.mybatis.mapper.util.StringUtil;
import tk.mybatis.mapper.version.VersionException;

import javax.persistence.Table;
import java.util.Set;

public class MultiLanguageHelper {

    public static String getAllColumns_TL(Class<?> entityClass) {
        Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
        StringBuilder sql = new StringBuilder();
        EntityTable entityTable = EntityHelper.getEntityTable(entityClass);
        for (EntityColumn entityColumn : columnList) {
            if(entityColumn instanceof MultiLanguageEntityColumn && ((MultiLanguageEntityColumn) entityColumn).isMultiLanguage()){
                sql.append("t.");
            } else {
                sql.append("b.");
            }
            sql.append(entityColumn.getColumn()).append(",");
        }
        return sql.substring(0, sql.length() - 1);
    }

    public static String selectAllColumns_TL(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append(getAllColumns_TL(entityClass));
        sql.append(" ");
        return sql.toString();
    }

    public static String fromTable_TL(Class<?> entityClass, String defaultTableName) {
        StringBuilder sql = new StringBuilder();
        sql.append(" FROM ");
        String tableName = entityClass.getAnnotation(Table.class).name();
        EntityTable entityTable = EntityHelper.getEntityTable(entityClass);
        sql.append(tableName).append(" b ");
        if(tableName.toUpperCase().endsWith("_B")){
            sql.append("LEFT OUTER JOIN ").append(tableName.substring(0, tableName.length() - 2) + "_TL t ");
        }else{
            sql.append("LEFT OUTER JOIN ").append(tableName + "_TL t ");
        }
        sql.append("ON (");
        for (EntityColumn column: entityTable.getEntityClassPKColumns()) {
            sql.append("b.").append(column.getColumn()).append("=t.").append(column.getColumn()).append(" AND ");
        }
        sql.append("t.LANG='${@io.choerodon.mybatis.util.OGNL@language()}') ");
        return sql.toString();
    }

    /**
     * where主键条件
     *
     * @param entityClass
     * @param entityName
     * @param useVersion
     * @return
     */
    public static String wherePKColumns(Class<?> entityClass, String entityName, boolean useVersion, boolean useBaseTable) {
        StringBuilder sql = new StringBuilder();
        sql.append("<where>");
        //获取全部列
        Set<EntityColumn> columnSet = EntityHelper.getPKColumns(entityClass);
        //当某个列有主键策略时，不需要考虑他的属性是否为空，因为如果为空，一定会根据主键策略给他生成一个值
        for (EntityColumn column : columnSet) {
            if(useBaseTable){
                sql.append(" AND b.").append(column.getColumnEqualsHolder(entityName));
            } else {
                sql.append(" AND ").append(column.getColumnEqualsHolder(entityName));
            }
        }
        if (useVersion) {
            sql.append(whereVersion(entityClass, entityName, useBaseTable));
        }
        sql.append("</where>");
        return sql.toString();
    }

    /**
     * 乐观锁字段条件
     *
     * @param entityClass
     * @return
     */
    public static String whereVersion(Class<?> entityClass, String entityName, boolean useBaseTable) {
        Set<EntityColumn> columnSet = EntityHelper.getColumns(entityClass);
        boolean hasVersion = false;
        String result = "";
        for (EntityColumn column : columnSet) {
            if (column.getEntityField().isAnnotationPresent(Version.class)) {
                if (hasVersion) {
                    throw new VersionException(entityClass.getCanonicalName() + " 中包含多个带有 @Version 注解的字段，一个类中只能存在一个带有 @Version 注解的字段!");
                }
                hasVersion = true;
                if(useBaseTable){
                    result = " AND b." + column.getColumnEqualsHolder(entityName);
                } else {
                    result = " AND " + column.getColumnEqualsHolder(entityName);
                }
            }
        }
        return result;
    }

    /**
     * where所有列的条件，会判断是否!=null
     *
     * @param entityClass
     * @param empty
     * @param useVersion
     * @return
     */
    public static String whereAllIfColumns_TL(Class<?> entityClass, boolean empty, boolean useVersion) {
        StringBuilder sql = new StringBuilder();
        boolean hasLogicDelete = false;

        sql.append("<where>");
        //获取全部列
        Set<EntityColumn> columnSet = EntityHelper.getColumns(entityClass);
        EntityColumn logicDeleteColumn = tk.mybatis.mapper.mapperhelper.SqlHelper.getLogicDeleteColumn(entityClass);
        //当某个列有主键策略时，不需要考虑他的属性是否为空，因为如果为空，一定会根据主键策略给他生成一个值
        for (EntityColumn column : columnSet) {
            if (!useVersion || !column.getEntityField().isAnnotationPresent(Version.class)) {
                // 逻辑删除，后面拼接逻辑删除字段的未删除条件
                if (logicDeleteColumn != null && logicDeleteColumn == column) {
                    hasLogicDelete = true;
                    continue;
                }
                if(column instanceof MultiLanguageEntityColumn && ((MultiLanguageEntityColumn) column).isMultiLanguage()){
                    sql.append(SqlHelper.getIfNotNull(column, " AND t." + column.getColumnEqualsHolder(), empty));
                } else {
                    sql.append(SqlHelper.getIfNotNull(column, " AND b." + column.getColumnEqualsHolder(), empty));
                }
            }
        }
        if (useVersion) {
            sql.append(SqlHelper.whereVersion(entityClass));
        }
        if (hasLogicDelete) {
            sql.append(SqlHelper.whereLogicDelete(entityClass, false));
        }

        sql.append("</where>");
        return sql.toString();
    }

    /**
     * 获取默认的orderBy，通过注解设置的
     *
     * @param entityClass
     * @return
     */
    public static String orderByDefault_TL(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        String orderByClause = EntityHelper.getOrderByClause(entityClass);
        if (orderByClause.length() > 0) {
            sql.append(" ORDER BY b.");
            sql.append(orderByClause);
        }
        return sql.toString();
    }

    /**
     * example支持查询指定列时
     *
     * @return
     */
    public static String exampleSelectColumns_TL(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        sql.append("<choose>");
        sql.append("<when test=\"@tk.mybatis.mapper.util.OGNL@hasSelectColumns(_parameter)\">");
        sql.append("<foreach collection=\"_parameter.selectColumns\" item=\"selectColumn\" separator=\",\">");
        sql.append("<choose>");
        sql.append("<when test=\"@io.choerodon.mybatis.util.OGNL@isMultiLanguageColumn(_parameter, selectColumn)\">");
        sql.append("t.${selectColumn}");
        sql.append("</when>");
        sql.append("<otherwise>");
        sql.append("b.${selectColumn}");
        sql.append("</otherwise>");
        sql.append("</choose>");
        sql.append("</foreach>");
        sql.append("</when>");
        //不支持指定列的时候查询全部列
        sql.append("<otherwise>");
        sql.append(getAllColumns_TL(entityClass));
        sql.append("</otherwise>");
        sql.append("</choose>");
        return sql.toString();
    }

    /**
     * example查询中的orderBy条件，会判断默认orderBy
     *
     * @return
     */
    public static String exampleOrderBy_TL(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        sql.append("<if test=\"orderByClause != null\">");
        sql.append("order by b.${orderByClause}");
        sql.append("</if>");
        String orderByClause = EntityHelper.getOrderByClause(entityClass);
        if (orderByClause.length() > 0) {
            sql.append("<if test=\"orderByClause == null\">");
            sql.append("ORDER BY b.");
            sql.append(orderByClause);
            sql.append("</if>");
        }
        return sql.toString();
    }


    /**
     * update set列，不考虑乐观锁注解 @Version
     * @return
     */
    public static String updateSetColumnsExample(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        sql.append("<choose>");
        sql.append("<when test=\"example.updateColumns != null\">");
        sql.append("<set>");
        sql.append("<foreach collection=\"example.updateColumns\" item=\"updateColumn\" separator=\",\">");
        sql.append("${updateColumn.column}=#{record.${updateColumn.property}}");
        sql.append("</foreach>");
        sql.append(updateSetVersion(entityClass, "record", false));
        sql.append("</set>");
        sql.append("</when>");
        sql.append("<otherwise>");
        sql.append(SqlHelper.updateSetColumns(entityClass, "record", false, false));
        sql.append("</otherwise>");
        sql.append("</choose>");
        return sql.toString();
    }

    /**
     * 乐观锁字段条件
     *
     * @param entityClass
     * @return
     */
    public static String updateSetVersion(Class<?> entityClass, String entityName, boolean useBaseTable) {
        Set<EntityColumn> columnSet = EntityHelper.getColumns(entityClass);
        boolean hasVersion = false;
        StringBuilder result = new StringBuilder();
        for (EntityColumn column : columnSet) {
            if (column.getEntityField().isAnnotationPresent(Version.class)) {
                if (hasVersion) {
                    throw new VersionException(entityClass.getCanonicalName() + " 中包含多个带有 @Version 注解的字段，一个类中只能存在一个带有 @Version 注解的字段!");
                }
                hasVersion = true;
                Version version = column.getEntityField().getAnnotation(Version.class);
                String versionClass = version.nextVersion().getCanonicalName();
                result.append(", ");
                result.append("<bind name=\"").append(column.getProperty()).append("Version\" value=\"");
                //version = ${@tk.mybatis.mapper.version@nextVersionClass("versionClass", version)}
                result.append("@tk.mybatis.mapper.version.VersionUtil@nextVersion(")
                        .append("@").append(versionClass).append("@class, ");
                if (StringUtil.isNotEmpty(entityName)) {
                    result.append(entityName).append(".");
                }
                result.append(column.getProperty()).append(")\"/>");
                result.append(column.getColumn()).append(" = #{").append(column.getProperty()).append("Version},");
            }
        }
        return result.toString();
    }


}
