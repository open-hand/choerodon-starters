package io.choerodon.mybatis.mapperhelper;

import io.choerodon.mybatis.common.query.Comparison;
import io.choerodon.mybatis.common.query.JoinColumn;
import io.choerodon.mybatis.common.query.JoinOn;
import io.choerodon.mybatis.common.query.JoinTable;
import io.choerodon.mybatis.common.query.Selection;
import io.choerodon.mybatis.common.query.SortField;
import io.choerodon.mybatis.common.query.Where;
import io.choerodon.mybatis.common.query.WhereField;
import io.choerodon.mybatis.entity.BaseDTO;
import io.choerodon.mybatis.entity.Criteria;
import io.choerodon.mybatis.entity.CustomEntityColumn;
import io.choerodon.mybatis.entity.CustomEntityTable;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.annotation.Version;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.entity.EntityTable;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.mapperhelper.SqlHelper;
import tk.mybatis.mapper.util.StringUtil;
import tk.mybatis.mapper.version.VersionException;

import javax.persistence.Table;
import javax.persistence.criteria.JoinType;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.choerodon.mybatis.mapperhelper.CustomEntityResolve.buildJoinKey;

public class CustomHelper {

    public static String getAllColumns_TL(Class<?> entityClass) {
        Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
        StringBuilder sql = new StringBuilder();
        EntityTable entityTable = EntityHelper.getEntityTable(entityClass);
        for (EntityColumn entityColumn : columnList) {
            if(entityColumn instanceof CustomEntityColumn && ((CustomEntityColumn) entityColumn).isMultiLanguage()){
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
                if(column instanceof CustomEntityColumn && ((CustomEntityColumn) column).isMultiLanguage()){
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

    /**
     * 按照主键查询生成SQL.
     *
     * @param dto
     * @return sql
     */
    public static String buildSelectByPrimaryKeySQL(BaseDTO dto) {
        EntityTable table = EntityHelper.getEntityTable(dto.getClass());
        Criteria criteria = new Criteria();
        for(EntityColumn pkColumn : table.getEntityClassPKColumns()){
            criteria.where(pkColumn.getProperty());
        }
        return buildSelectSelectiveSql(dto, criteria);
    }


    /**
     * 按照查询条件生成SQL.
     *
     * @param dto
     * @return sql
     */
    public static String buildSelectSelectiveSql(BaseDTO dto, Criteria criteria) {
        CustomEntityTable table = (CustomEntityTable) EntityHelper.getEntityTable(dto.getClass());
        List<Selection> selectFields = new ArrayList<>(50);
        List<Selection> selections = criteria.getSelectFields();
        if (selections == null || selections.isEmpty()) {
            for (EntityColumn column : table.getAllColumns()) {
                if (criteria.getExcludeSelectFields() == null || !criteria.getExcludeSelectFields().contains(column.getProperty())) {
                    selectFields.add(new Selection(column.getProperty()));
                }
            }
        } else {
            for (Selection selection : selections) {
                if (criteria.getExcludeSelectFields() == null || !criteria.getExcludeSelectFields().contains(selection.getField())) {
                    selectFields.add(selection);
                }
            }
        }


        String sql = new SQL() {
            {
                // SELECT
                for (Selection selection : selectFields) {
                    String selectionSql = generateSelectionSQL(dto, selection);
                    if (!StringUtils.isEmpty(selectionSql))
                        SELECT(selectionSql);
                }
                // FROM
                FROM(table.getName() + " " + table.getAlias());

                // JOIN
                for (Map.Entry<String, CustomEntityColumn> entry : table.getJoinMapping().entrySet()) {
                    CustomEntityColumn column = (CustomEntityColumn) entry.getValue();
                    JoinTable jt = column.findJoinTableByName(entry.getKey());
                    String joinSql = generateJoinSQL(dto, column, jt, selectFields);
                    if (!StringUtils.isEmpty(joinSql)){
                        JoinType joinType = jt.type();
                        switch (joinType){
                            case LEFT:
                                LEFT_OUTER_JOIN(joinSql);
                                break;
                            case INNER:
                                INNER_JOIN(joinSql);
                                break;
                            case RIGHT:
                                RIGHT_OUTER_JOIN(joinSql);
                                break;
                        }
                    }

                }
                //WHERE
                String whereSql = generateWhereClauseSQL(dto, criteria);
                if (!StringUtils.isEmpty(whereSql))
                    WHERE(whereSql);

                // ORDER BY
                List<SortField> sortFields = criteria.getSortFields();
                if(sortFields != null && !sortFields.isEmpty()) {
                    for (SortField sortField : sortFields) {
                        for (EntityColumn sortColumn : table.getEntityClassColumns()) {
                            if(sortColumn.getProperty().equals(sortField.getField())){
                                ORDER_BY(findColumnNameByField(dto,sortField.getField(),false) + sortField.getSortType().sql());
                                break;
                            }
                        }
                    }
                } else {
                    //默认order by
                    for (EntityColumn sortColumn : table.getEntityClassColumns()) {
                        if (sortColumn.getOrderBy() != null) {
                            ORDER_BY(findColumnNameByField(dto, (CustomEntityColumn)sortColumn, false) + " " + sortColumn.getOrderBy());
                        }
                    }
                }


            }
        }.usingAppender(new StringBuilder()).toString();
        return sql;
    }

    /**
     * 生成查询字段SQL.
     *
     * @param dto
     * @param selection
     * @return SQL
     */
    private static String generateSelectionSQL(BaseDTO dto, Selection selection) {
        return findColumnNameByField(dto,selection.getField(),true);
    }

    /**
     * 按照属性名转换字段SQL.
     *
     * @param dto
     * @param field
     * @return
     */
    private static String findColumnNameByField(BaseDTO dto, String field, boolean withAlias){
        CustomEntityTable table = (CustomEntityTable) EntityHelper.getEntityTable(dto.getClass());
        CustomEntityColumn entityColumn = table.findColumnByProperty(field);
        return findColumnNameByField(dto, entityColumn, withAlias);
    }

    /**
     * 按照属性名转换字段SQL.
     *
     * @param dto
     * @param entityColumn
     * @return
     */
    private static String findColumnNameByField(BaseDTO dto, CustomEntityColumn entityColumn, boolean withAlias){
        CustomEntityTable table = (CustomEntityTable) EntityHelper.getEntityTable(dto.getClass());
        StringBuilder sb = new StringBuilder();
        if (entityColumn != null) {
            JoinColumn jc = entityColumn.getJoinColumn();
            if (jc != null) {
                CustomEntityColumn joinField = table.getJoinMapping().get(jc.joinName());
                JoinTable joinTable = joinField.findJoinTableByName(jc.joinName());
                if (joinField != null && joinTable != null) {
                    CustomEntityTable joinEntityTable = (CustomEntityTable)EntityHelper.getEntityTable(joinTable.target());
                    EntityColumn refColumn = joinEntityTable.findColumnByProperty(jc.field());
                    sb.append(table.getAlias(buildJoinKey(joinTable))).append(".").append(refColumn.getColumn());
                    if(withAlias) {
                        sb.append(" AS ").append(entityColumn.getColumn());
                    }
                }
            } else {
                sb.append(table.getAlias()).append(".").append(entityColumn.getColumn());
            }
        }
        return sb.toString();
    }

    /**
     * 生成JOIN表的SQL.
     *
     * @param dto
     * @return SQL
     */
    private static String generateJoinSQL(BaseDTO dto, EntityColumn localColumn, JoinTable joinTable, List<Selection> selections) {
        StringBuilder sb = new StringBuilder();
        CustomEntityTable localTable = (CustomEntityTable) EntityHelper.getEntityTable(dto.getClass());
        String joinKey = buildJoinKey(joinTable);
        CustomEntityTable foreignTable = (CustomEntityTable) EntityHelper.getEntityTable(joinTable.target());
        boolean foundJoinColumn = false;
        for (Selection selection : selections) {
            CustomEntityColumn entityColumn = (CustomEntityColumn) localTable.findColumnByProperty(selection.getField());
            if (entityColumn != null && entityColumn.getJoinColumn() != null && joinTable.name().equals(entityColumn.getJoinColumn().joinName())) {
                foundJoinColumn = true;
                break;
            }
        }
        if (foundJoinColumn) {
            String jointTableName = foreignTable.getName();
            if(joinTable.joinMultiLanguageTable()){
                if(jointTableName.toUpperCase().endsWith("_B")){
                    jointTableName = jointTableName.substring(0, jointTableName.length() - 2) + "_TL";
                }else{
                    jointTableName = jointTableName + "_TL";
                }
            }
            sb.append(jointTableName).append(" ").append(localTable.getAlias(joinKey)).append(" ON ");
            JoinOn[] joinOns = joinTable.on();
            for (int i = 0, j = joinOns.length; i < j; i++) {
                JoinOn joinOn = joinOns[i];
                String joinField = joinOn.joinField();
                if(StringUtils.isEmpty(joinField)) continue;
                if (i != 0) {
                    sb.append(" AND ");
                }
                EntityColumn foreignColumn = foreignTable.findColumnByProperty(joinField);
                String columnName = foreignColumn != null ? foreignColumn.getColumn() : StringUtil.camelhumpToUnderline(joinField);
                if (StringUtils.isEmpty(joinOn.joinExpression())) {
                    sb.append(localTable.getAlias()).append(".").append(localColumn.getColumn()).append(" = ");
                    sb.append(localTable.getAlias(joinKey)).append(".").append(columnName);
                } else {
                    sb.append(localTable.getAlias(joinKey)).append(".").append(columnName);
                    sb.append(" = ").append(joinOn.joinExpression());
                }
            }
        }
        return sb.toString();
    }


    /**
     * 生成Where的SQL.
     *
     * @param dto
     * @return SQL
     */
    private static String generateWhereClauseSQL(BaseDTO dto, Criteria criteria) {
        StringBuilder sb = new StringBuilder();
        List<WhereField> whereFields = criteria.getWhereFields();
        CustomEntityTable table = (CustomEntityTable) EntityHelper.getEntityTable(dto.getClass());

        for (EntityColumn column : table.getEntityClassColumns()) {
            try {
                CustomEntityColumn hapEntityColumn = (CustomEntityColumn) column;
                if (column.getEntityField().getValue(dto) != null) {
                    Where where = hapEntityColumn.getWhere();
                    if (where != null){
                        Comparison comparison = where.comparison();
                        boolean isWhereField = false;
                        if(whereFields != null && !whereFields.isEmpty()){
                            for (WhereField whereField : whereFields) {
                                String f = whereField.getField();
                                if (f != null && f.equals(column.getProperty())) {
                                    isWhereField = true;
                                    if(whereField.getComparison() != null) {
                                        comparison = whereField.getComparison();
                                    }
                                    break;
                                }
                            }
                            if(!isWhereField) continue;
                        }
                        if (sb.length() > 0) {
                            sb.append(" AND ");
                        }

                        String columnName = column.getColumn();
                        JoinColumn jc = hapEntityColumn.getJoinColumn();
                        if(jc != null) {
                            CustomEntityColumn joinField = table.getJoinMapping().get(jc.joinName());
                            JoinTable jt = joinField.findJoinTableByName(jc.joinName());
                            CustomEntityTable foreignTable = (CustomEntityTable) EntityHelper.getEntityTable(jt.target());
                            EntityColumn foreignColumn = foreignTable.findColumnByProperty(jc.field());
                            columnName = foreignColumn.getColumn();
                            sb.append(table.getAlias(buildJoinKey(jt))).append(".");
                        } else {
                            sb.append(table.getAlias()).append(".");
                        }
                        sb.append(columnName).append(formatComparisonSQL(comparison.sql(), column.getColumnHolder("dto")));
                    }
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * 格式化SQL
     * @param format
     * @param placeHolder
     * @return
     */
    private static String formatComparisonSQL(String format,String placeHolder){
        if (format.contains("{0}")) {
            MessageFormat mf = new MessageFormat(format);
            return mf.format(new String[]{placeHolder});
        } else {
            return format + placeHolder;
        }
    }

}
