/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2016 abel533@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package io.choerodon.mybatis.helper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import io.choerodon.mybatis.MapperException;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.EntityColumn;
import io.choerodon.mybatis.domain.EntityTable;
import io.choerodon.mybatis.util.StringUtil;



/**
 * 拼常用SQL的工具类
 *
 * @author liuzh
 * @since 2015-11-03 22:40
 */
public class SqlHelper {

    public static final List<String> MODIFY_AUDIT_FIELDS = Arrays.asList("creationDate", "createdBy", "lastUpdateDate", "lastUpdatedBy");

    public static final List<String> VERSION_AUDIT_FIELDS = Collections.singletonList("objectVersionNumber");
    private static final String LANG_BIND = "<bind name=\"lang\" value=\"@io.choerodon.mybatis.helper.LanguageHelper@language()\" />";
    private static final String AUDIT_BIND = "<bind name=\"audit\" value=\"@io.choerodon.mybatis.helper.AuditHelper@audit()\" />";
    private static final String OPTIONAL_BIND = "<bind name=\"optional\" value=\"@io.choerodon.mybatis.helper.OptionalHelper@optional()\" />";
    private static final String IF_TEST = "<if test=\"";
    private static final String IF = "</if>";
    private static final String AND = " AND ";
    private static final String LEFT_WHERE = "<where>";
    private static final String RIGIT_WHERE = "</where>";

    /**
     * getBindCache
     *
     * @param column column
     * @return String
     */
    public static String getBindCache(EntityColumn column) {
        return getBindCache(null, column);
    }

    /**
     * getBindCache
     *
     * @param column column
     * @param entityName entityName
     * @return String
     */
    public static String getBindCache(String entityName, EntityColumn column) {
        StringBuilder sql = new StringBuilder();
        sql.append("<bind name=\"");
        sql.append(column.getProperty()).append("_cache\" ");
        sql.append("value=\"");
        if (entityName != null) {
            sql.append(entityName + ".");
        }
        sql.append(column.getProperty()).append("\"/>");
        return sql.toString();
    }

    /**
     * getBindValue
     * @param value  value
     * @param column column
     * @return String
     */
    public static String getBindValue(EntityColumn column, String value) {
        StringBuilder sql = new StringBuilder();
        sql.append("<bind name=\"");
        sql.append(column.getProperty()).append("_bind\" ");
        sql.append("value='").append(value).append("'/>");
        return sql.toString();
    }

    public static String getLangBind() {
        return LANG_BIND;
    }

    public static String getAuditBind() {
        return AUDIT_BIND;
    }

    public static String getOptionalBind() {
        return OPTIONAL_BIND;
    }

    /**
     * getIfCacheNotNull
     * @param contents  contents
     * @param column column
     * @return String
     */
    public static String getIfCacheNotNull(EntityColumn column, String contents) {
        StringBuilder sql = new StringBuilder();
        sql.append(IF_TEST).append(column.getProperty()).append("_cache != null\">");
        sql.append(contents);
        sql.append(IF);
        return sql.toString();
    }

    /**
     * 如果_cache == null
     * @param contents contents
     * @param column column
     * @return String
     */
    public static String getIfCacheIsNull(EntityColumn column, String contents) {
        StringBuilder sql = new StringBuilder();
        sql.append(IF_TEST).append(column.getProperty()).append("_cache == null\">");
        sql.append(contents);
        sql.append(IF);
        return sql.toString();
    }

    /**
     * 判断自动!=null的条件结构
     *
     * @param column   column
     * @param contents contents
     * @param empty    empty
     * @return String
     */
    public static String getIfNotNull(EntityColumn column, String contents, boolean empty) {
        return getIfNotNull(null, column, contents, empty);
    }

    /**
     * 判断自动!=null的条件结构
     *
     * @param entityName entityName
     * @param column     column
     * @param contents   contents
     * @param empty      empty
     * @return String
     */
    public static String getIfNotNull(String entityName, EntityColumn column, String contents, boolean empty) {
        StringBuilder sql = new StringBuilder();
        sql.append(IF_TEST);
        if (StringUtil.isNotEmpty(entityName)) {
            sql.append(entityName).append(".");
        }
        sql.append(column.getProperty()).append(" != null");
        if (empty && column.getJavaType().equals(String.class)) {
            sql.append(AND);
            if (StringUtil.isNotEmpty(entityName)) {
                sql.append(entityName).append(".");
            }
            sql.append(column.getProperty()).append(" != '' ");
        }
        sql.append("\">");
        sql.append(contents);
        sql.append(IF);
        return sql.toString();
    }

    /**
     * 判断自动==null的条件结构
     *
     * @param column   column
     * @param contents contents
     * @param empty    empty
     * @return String
     */
    public static String getIfIsNull(EntityColumn column, String contents, boolean empty) {
        return getIfIsNull(null, column, contents, empty);
    }

    /**
     * 判断自动==null的条件结构
     *
     * @param entityName entityName
     * @param column     column
     * @param contents   contents
     * @param empty      empty
     * @return String
     */
    public static String getIfIsNull(String entityName, EntityColumn column, String contents, boolean empty) {
        StringBuilder sql = new StringBuilder();
        sql.append(IF_TEST);
        if (StringUtil.isNotEmpty(entityName)) {
            sql.append(entityName).append(".");
        }
        sql.append(column.getProperty()).append(" == null");
        if (empty && column.getJavaType().equals(String.class)) {
            sql.append(" or ");
            if (StringUtil.isNotEmpty(entityName)) {
                sql.append(entityName).append(".");
            }
            sql.append(column.getProperty()).append(" == '' ");
        }
        sql.append("\">");
        sql.append(contents);
        sql.append(IF);
        return sql.toString();
    }

    /**
     * 判断自动optionals.contains()的条件结构
     *
     * @param optionalsName optionalsName
     * @param column        column
     * @param contents      contents
     * @return String
     */
    public static String getIfContains(String optionalsName, EntityColumn column, String contents) {
        StringBuilder sql = new StringBuilder();
        sql.append(IF_TEST);
        sql.append(optionalsName + ".contains('" + column.getProperty() + "')");
        sql.append("\">");
        sql.append(contents);
        sql.append(IF);
        return sql.toString();
    }

    /**
     * 获取所有查询列，如id,name,code...
     *
     * @param entityClass entityClass
     * @return String
     */
    public static String getAllColumns(Class<?> entityClass) {
        Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
        StringBuilder sql = new StringBuilder();
        for (EntityColumn entityColumn : columnList) {
            if (EntityHelper.getTableByEntity(entityClass).isMultiLanguage()
                    && (entityColumn.isMultiLanguage() || entityColumn.isId())) {
                sql.append(entityColumn.isMultiLanguage() ? "t." : "b.");
            }
            sql.append(entityColumn.getColumn()).append(",");
        }
        return sql.substring(0, sql.length() - 1);
    }

    /**
     * select xxx,xxx...
     *
     * @param entityClass entityClass
     * @return String
     */
    public static String selectAllColumns(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append(getAllColumns(entityClass));
        sql.append(" ");
        return sql.toString();
    }

    /**
     * select count(x)
     *
     * @param entityClass entityClass
     * @return String
     */
    public static String selectCount(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        Set<EntityColumn> pkColumns = EntityHelper.getPkColumns(entityClass);
        processSelectCount(entityClass, sql, pkColumns);
        return sql.toString();
    }

    private static void processSelectCount(Class<?> entityClass, StringBuilder sql, Set<EntityColumn> pkColumns) {
        if (pkColumns.size() == 1) {
            EntityTable table = EntityHelper.getTableByEntity(entityClass);
            if (table.isMultiLanguage()) {
                sql.append("COUNT(t.").append(pkColumns.iterator().next().getColumn()).append(") ");
            } else {
                sql.append("COUNT(").append(pkColumns.iterator().next().getColumn()).append(") ");
            }
        } else {
            sql.append("COUNT(*) ");
        }
    }

    /**
     * select case when count(x) more than 0 then 1 else 0 end
     *
     * @param entityClass entityClass
     * @return String
     */
    public static String selectCountExists(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT CASE WHEN ");
        Set<EntityColumn> pkColumns = EntityHelper.getPkColumns(entityClass);
        processSelectCount(entityClass, sql, pkColumns);
        sql.append(" > 0 THEN 1 ELSE 0 END AS result ");
        return sql.toString();
    }

    /**
     * from tableName - 动态表名
     *
     * @param entityClass      entityClass
     * @param defaultTableName defaultTableName
     * @return String
     */
    public static String selectFromTableTl(Class<?> entityClass, String defaultTableName) {
        StringBuilder builder = new StringBuilder();
        builder.append(" FROM ");
        builder.append(defaultTableName);
        builder.append(" ");
        EntityTable table = EntityHelper.getTableByEntity(entityClass);
        if (table.isMultiLanguage()) {
            builder.append("b ");
            if (StringUtil.tableNameAllUpperCase(defaultTableName)) {
                builder.append("LEFT JOIN ").append(defaultTableName + "_TL t ");
            } else {
                builder.append("LEFT JOIN ").append(defaultTableName + "_tl t ");

            }
            builder.append("ON (");
            for (EntityColumn column : table.getEntityClassPkColumns()) {
                builder.append("b.").append(column.getColumn())
                        .append("=t.").append(column.getColumn())
                        .append(AND);
            }
            builder.append("t.lang=#{lang}");
            builder.append(")");
        }
        return builder.toString();
    }

    /**
     * update tableName - 动态表名
     *
     * @param entityClass      entityClass
     * @param defaultTableName defaultTableName
     * @return String
     */
    public static String updateTable(Class<?> entityClass, String defaultTableName) {
        return updateTable(entityClass, defaultTableName, null);
    }

    /**
     * update tableName - 动态表名
     *
     * @param entityClass      entityClass
     * @param defaultTableName 默认表名
     * @param entityName       别名
     * @return String
     */
    public static String updateTable(Class<?> entityClass, String defaultTableName, String entityName) {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ");
        sql.append(defaultTableName);
        sql.append(" ");
        return sql.toString();
    }

    /**
     * delete tableName - 动态表名
     *
     * @param entityClass      entityClass
     * @param defaultTableName defaultTableName
     * @return String
     */
    public static String deleteFromTable(Class<?> entityClass, String defaultTableName) {
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM ");
        sql.append(defaultTableName);
        sql.append(" ");
        return sql.toString();
    }

    /**
     * insert into tableName - 动态表名
     *
     * @param entityClass      entityClass
     * @param defaultTableName defaultTableName
     * @return String
     */
    public static String insertIntoTable(Class<?> entityClass, String defaultTableName) {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ");
        sql.append(defaultTableName);
        sql.append(" ");
        return sql.toString();
    }

    /**
     * insert table()列
     *
     * @param entityClass entityClass
     * @param skipId      是否从列中忽略id类型
     * @param notNull     是否判断!=null
     * @param notEmpty    是否判断String类型!=''
     * @return String
     */
    public static String insertColumns(Class<?> entityClass, boolean skipId, boolean notNull, boolean notEmpty) {
        return insertColumns(null, entityClass, skipId, notNull, notEmpty);
    }

    /**
     * insert table()列
     * @param entityName entityName
     * @param entityClass entityClass
     * @param skipId      是否从列中忽略id类型
     * @param notNull     是否判断!=null
     * @param notEmpty    是否判断String类型!=''
     * @return String
     */
    public static String insertColumns(String entityName, Class<?> entityClass, boolean skipId, boolean notNull, boolean notEmpty) {
        StringBuilder sql = new StringBuilder();
        sql.append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
        //获取全部列
        Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
        //当某个列有主键策略时，不需要考虑他的属性是否为空，因为如果为空，一定会根据主键策略给他生成一个值
        for (EntityColumn column : columnList) {
            if (doContinue(skipId, column)) {
                continue;
            }
            if (notNull) {
                sql.append(SqlHelper.getIfNotNull(entityName, column, column.getColumn() + ",", notEmpty));
            } else {
                sql.append(column.getColumn() + ",");
            }
        }
        sql.append("</trim>");
        return sql.toString();
    }

    private static boolean doContinue(boolean skipId, EntityColumn column) {
        if (!column.isInsertable()) {
            return true;
        }
        return skipId && column.isId();
    }

    /**
     * insert-values()列
     *
     * @param entityClass entityClass
     * @param skipId      是否从列中忽略id类型
     * @param notNull     是否判断!=null
     * @param notEmpty    是否判断String类型!=''
     * @return String
     */
    public static String insertValuesColumns(Class<?> entityClass, boolean skipId, boolean notNull, boolean notEmpty) {
        StringBuilder sql = new StringBuilder();
        sql.append("<trim prefix=\"VALUES (\" suffix=\")\" suffixOverrides=\",\">");
        //获取全部列
        Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
        //当某个列有主键策略时，不需要考虑他的属性是否为空，因为如果为空，一定会根据主键策略给他生成一个值
        for (EntityColumn column : columnList) {
            if (doContinue(skipId, column)) {
                continue;
            }
            if (notNull) {
                sql.append(SqlHelper.getIfNotNull(column, column.getColumnHolder() + ",", notEmpty));
            } else {
                sql.append(column.getColumnHolder() + ",");
            }
        }
        sql.append("</trim>");
        return sql.toString();
    }

    /**
     * update set列
     *
     * @param entityClass entityClass
     * @param entityName  实体映射名
     * @param notNull     是否判断!=null
     * @param notEmpty    是否判断String类型!=''
     * @return String
     */
    public static String updateSetColumns(Class<?> entityClass, String entityName, boolean notNull, boolean notEmpty) {
        StringBuilder sql = new StringBuilder();
        sql.append("<set>");
        //获取全部列
        Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
        //当某个列有主键策略时，不需要考虑他的属性是否为空，因为如果为空，一定会根据主键策略给他生成一个值
        for (EntityColumn column : columnList) {
            if (!column.isId() && column.isUpdatable()) {
                if (notNull) {
                    sql.append(SqlHelper.getIfNotNull(entityName, column,
                            column.getColumnEqualsHolder(entityName) + ",", notEmpty));
                } else {
                    sql.append(column.getColumnEqualsHolder(entityName) + ",");
                }
            }
        }
        sql.append("</set>");
        return sql.toString();
    }

    /**
     * update set列
     *
     * @param entityClass entityClass
     * @param entityName  实体映射名
     * @param notNull     是否判断!=null
     * @param notEmpty    是否判断String类型!=''
     * @throws Exception Exception
     * @return  String String
     */
    public static String updateSetColumnsAndVersion(Class<?> entityClass,
                                                    String entityName,
                                                    boolean notNull,
                                                    boolean notEmpty) throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("<set>");
        //获取全部列
        Set<EntityColumn> columnSet = EntityHelper.getColumns(entityClass);
        //当某个列有主键策略时，不需要考虑他的属性是否为空，因为如果为空，一定会根据主键策略给他生成一个值
        boolean modifyAudit = entityClass.isAnnotationPresent(ModifyAudit.class);
        boolean versionAudit = entityClass.isAnnotationPresent(VersionAudit.class);
        for (EntityColumn column : columnSet) {
            String columnName = column.getProperty();
            //如果启用监控，插入时更新值
            if (fillSelfMaintainFileds(sql, modifyAudit, versionAudit, column, columnName)) {
                continue;
            }
            if (!column.isId() && column.isUpdatable()) {
                if (notNull) {
                    sql.append(SqlHelper.getIfNotNull(entityName, column,
                            column.getColumnEqualsHolder(entityName) + ",", notEmpty));
                } else {
                    sql.append(column.getColumnEqualsHolder(entityName) + ",");
                }
            }
        }
        sql.append("</set>");
        return sql.toString();
    }

    private static boolean fillSelfMaintainFileds(StringBuilder sql, boolean modifyAudit,
                                                  boolean versionAudit, EntityColumn column,
                                                  String columnName) {
        if (modifyAudit && SqlHelper.MODIFY_AUDIT_FIELDS.contains(column.getProperty())) {
            if (columnName.equals("lastUpdateDate")) {
                sql.append("LAST_UPDATE_DATE = #{audit.now},");
            } else if (columnName.equals("lastUpdatedBy")) {
                sql.append("LAST_UPDATED_BY = #{audit.user},");
            }
            return true;
        }
        if (versionAudit && SqlHelper.VERSION_AUDIT_FIELDS.contains(column.getProperty())) {
            if (columnName.equals("objectVersionNumber")) {
                sql.append("OBJECT_VERSION_NUMBER = OBJECT_VERSION_NUMBER+1,");
            } else {
                throw new MapperException("未知的Version列" + columnName);
            }
            return true;
        }
        return false;
    }

    /**
     * where主键条件
     *
     * @param entityClass entityClass
     * @return String
     */
    public static String wherePkColumnsTl(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        sql.append(LEFT_WHERE);
        //获取全部列
        Set<EntityColumn> columnList = EntityHelper.getPkColumns(entityClass);
        //当某个列有主键策略时，不需要考虑他的属性是否为空，因为如果为空，一定会根据主键策略给他生成一个值
        for (EntityColumn column : columnList) {
            sql.append(AND + column.getColumnEqualsHolderTl(null));
        }
        sql.append(RIGIT_WHERE);
        return sql.toString();
    }

    /**
     * 拼接where列子语句
     *
     * @param entityClass entityClass
     * @return String
     */
    public static String wherePkColumns(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        sql.append(LEFT_WHERE);
        //获取全部列
        Set<EntityColumn> columnList = EntityHelper.getPkColumns(entityClass);
        //当某个列有主键策略时，不需要考虑他的属性是否为空，因为如果为空，一定会根据主键策略给他生成一个值
        for (EntityColumn column : columnList) {
            sql.append(AND + column.getColumnEqualsHolder(null));
        }
        sql.append(RIGIT_WHERE);
        return sql.toString();
    }

    /**
     * 拼接where和OBJECT_VERSION_NUMBER
     *
     * @param entityClass entityClass
     * @return String
     */
    public static String wherePrimaryAndVersion(Class<?> entityClass) {
        boolean versionAudit = entityClass.isAnnotationPresent(VersionAudit.class);
        StringBuilder sql = new StringBuilder();
        sql.append(LEFT_WHERE);
        //获取全部列
        Set<EntityColumn> columnList = EntityHelper.getPkColumns(entityClass);
        //当某个列有主键策略时，不需要考虑他的属性是否为空，因为如果为空，一定会根据主键策略给他生成一个值
        for (EntityColumn column : columnList) {
            sql.append(AND + column.getColumnEqualsHolder(null));
        }
        if (versionAudit) {
            sql.append(" AND OBJECT_VERSION_NUMBER = #{objectVersionNumber}");
        }
        sql.append(RIGIT_WHERE);
        return sql.toString();
    }

    /**
     * where所有列的条件，会判断是否!=null
     *
     * @param entityClass entityClass
     * @param empty empty
     * @return String
     */
    public static String whereAllIfColumns(Class<?> entityClass, boolean empty) {
        StringBuilder sql = new StringBuilder();
        sql.append(LEFT_WHERE);
        //获取全部列
        Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
        //当某个列有主键策略时，不需要考虑他的属性是否为空，因为如果为空，一定会根据主键策略给他生成一个值
        for (EntityColumn column : columnList) {
            sql.append(getIfNotNull(column, AND + column.getColumnEqualsHolder(null), empty));
        }
        sql.append(RIGIT_WHERE);
        return sql.toString();
    }

    /**
     * 拼接多语言表sql
     *
     * @param entityClass entityClass
     * @param empty       empty
     * @return String
     */
    public static String whereAllIfColumnsTl(Class<?> entityClass, boolean empty) {
        StringBuilder sql = new StringBuilder();
        sql.append(LEFT_WHERE);
        //获取全部列
        Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
        //当某个列有主键策略时，不需要考虑他的属性是否为空，因为如果为空，一定会根据主键策略给他生成一个值
        for (EntityColumn column : columnList) {
            sql.append(getIfNotNull(column, AND + column.getColumnEqualsHolderTl(null), empty));
        }
        sql.append(RIGIT_WHERE);
        return sql.toString();
    }

    /**
     * 获取默认的orderBy，通过注解设置的
     *
     * @param entityClass entityClass
     * @return String
     */
    public static String orderByDefault(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        String orderByClause = EntityHelper.getOrderByClause(entityClass);
        if (orderByClause.length() > 0) {
            sql.append(" ORDER BY ");
            sql.append(orderByClause);
        }
        return sql.toString();
    }


    /**
     * example查询中的orderBy条件，会判断默认orderBy
     * @param entityClass entityClass
     * @return  String
     */
    public static String exampleOrderBy(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        sql.append("<if test=\"orderByClause != null\">");
        sql.append("order by ${orderByClause}");
        sql.append(IF);
        String orderByClause = EntityHelper.getOrderByClause(entityClass);
        if (orderByClause.length() > 0) {
            sql.append("<if test=\"orderByClause == null\">");
            sql.append("ORDER BY " + orderByClause);
            sql.append(IF);
        }
        return sql.toString();
    }

    /**
     * Example查询中的where结构，用于只有一个Example参数时
     * @return String
     */
    public static String exampleWhereClause() {
        return "<if test=\"_parameter != null\">"
                + "<where>\n"
                + "  <foreach collection=\"oredCriteria\" item=\"criteria\" separator=\"or\">\n"
                + "    <if test=\"criteria.valid\">\n"
                + "      <trim prefix=\"(\" prefixOverrides=\"and\" suffix=\")\">\n"
                + "        <foreach collection=\"criteria.criteria\" item=\"criterion\">\n"
                + "          <choose>\n"
                + "            <when test=\"criterion.noValue\">\n"
                + "              and ${criterion.condition}\n"
                + "            </when>\n"
                + "            <when test=\"criterion.singleValue\">\n"
                + "              and ${criterion.condition} #{criterion.value}\n"
                + "            </when>\n"
                + "            <when test=\"criterion.betweenValue\">\n"
                + "              and ${criterion.condition} #{criterion.value} and #{criterion.secondValue}\n"
                + "            </when>\n"
                + "            <when test=\"criterion.listValue\">\n"
                + "              and ${criterion.condition}\n"
                + "              <foreach close=\")\" collection=\"criterion.value\" "
                + "                       item=\"listItem\" open=\"(\" separator=\",\">\n"
                + "                #{listItem}\n"
                + "              </foreach>\n"
                + "            </when>\n"
                + "          </choose>\n"
                + "        </foreach>\n"
                + "      </trim>\n"
                + "    </if>\n"
                + "  </foreach>\n"
                + "</where>"
                + "</if>";
    }

    /**
     * Condition-Update中的where结构，用于多个参数时，Example带@Param("example")注解时
     * @return String
     */
    public static String updateByExampleWhereClause() {
        return "<where>\n"
                + "  <foreach collection=\"example.oredCriteria\" item=\"criteria\" separator=\"or\">\n"
                + "    <if test=\"criteria.valid\">\n"
                + "      <trim prefix=\"(\" prefixOverrides=\"and\" suffix=\")\">\n"
                + "        <foreach collection=\"criteria.criteria\" item=\"criterion\">\n"
                + "          <choose>\n"
                + "            <when test=\"criterion.noValue\">\n"
                + "              and ${criterion.condition}\n"
                + "            </when>\n"
                + "            <when test=\"criterion.singleValue\">\n"
                + "              and ${criterion.condition} #{criterion.value}\n"
                + "            </when>\n"
                + "            <when test=\"criterion.betweenValue\">\n"
                + "              and ${criterion.condition} #{criterion.value} and #{criterion.secondValue}\n"
                + "            </when>\n"
                + "            <when test=\"criterion.listValue\">\n"
                + "              and ${criterion.condition}\n"
                + "              <foreach close=\")\" collection=\"criterion.value\" "
                + "                       item=\"listItem\" open=\"(\" separator=\",\">\n"
                + "                #{listItem}\n"
                + "              </foreach>\n"
                + "            </when>\n"
                + "          </choose>\n"
                + "        </foreach>\n"
                + "      </trim>\n"
                + "    </if>\n"
                + "  </foreach>\n"
                + "</where>";
    }

}
