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

import javax.persistence.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import io.choerodon.mybatis.MapperException;
import io.choerodon.mybatis.annotation.ColumnType;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.code.DbType;
import io.choerodon.mybatis.code.IdentityDialect;
import io.choerodon.mybatis.code.Style;
import io.choerodon.mybatis.domain.Config;
import io.choerodon.mybatis.domain.EntityColumn;
import io.choerodon.mybatis.domain.EntityField;
import io.choerodon.mybatis.domain.EntityTable;
import io.choerodon.mybatis.util.SimpleTypeUtil;
import io.choerodon.mybatis.util.StringUtil;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.UnknownTypeHandler;


/**
 * 实体类工具类 - 处理实体和数据库表以及字段关键的一个类
 * <p>项目地址 : <a href="https://github.com/abel533/Mapper" target="_blank">https://github.com/abel533/Mapper</a></p>
 *
 * @author liuzh
 */
public class EntityHelper {
    private static final String MULTI_LANGUAGE_TABLE_SUFFIX_LOW_CASE = "_tl";
    private static final String MULTI_LANGUAGE_TABLE_SUFFIX_UPPER_CASE = "_TL";

    /**
     * 实体类 => 表对象
     */
    private static final Map<Class<?>, EntityTable> entityClassTableMap = new ConcurrentHashMap<>();
    private static final Map<String, EntityTable> mapperClassTableMap = new ConcurrentHashMap<>();

    private EntityHelper() {
    }


    /**
     * 获取表对象
     *
     * @param entityClass entityClass
     * @return EntityTable
     */
    public static EntityTable getTableByEntity(Class<?> entityClass) {
        EntityTable entityTable = entityClassTableMap.get(entityClass);
        if (entityTable == null) {
            throw new MapperException("not found table for entity class: " + entityClass);
        }
        return entityTable;
    }

    /**
     * 获取表对象
     *
     * @param mapperClass mapperClass
     * @return EntityTable
     */
    public static EntityTable getTableByMapper(String mapperClass) {
        EntityTable entityTable = mapperClassTableMap.get(mapperClass);
        if (entityTable == null) {
            throw new MapperException("not found table for mapper class: " + mapperClass);
        }
        return entityTable;
    }

    /**
     * 获取默认的orderby语句
     *
     * @param entityClass entityClass
     * @return String
     */
    public static String getOrderByClause(Class<?> entityClass) {
        EntityTable table = getTableByEntity(entityClass);
        if (table.getOrderByClause() != null) {
            return table.getOrderByClause();
        }
        StringBuilder orderBy = new StringBuilder();
        for (EntityColumn column : table.getEntityClassColumns()) {
            if (column.getOrderBy() != null) {
                if (orderBy.length() != 0) {
                    orderBy.append(",");
                }
                orderBy.append(column.getColumn()).append(" ").append(column.getOrderBy());
            }
        }
        table.setOrderByClause(orderBy.toString());
        return table.getOrderByClause();
    }

    /**
     * 获取全部列
     *
     * @param entityClass entityClass
     * @return Set
     */
    public static Set<EntityColumn> getColumns(Class<?> entityClass) {
        return getTableByEntity(entityClass).getEntityClassColumns();
    }

    /**
     * 获取主键信息
     *
     * @param entityClass entityClass
     * @return Set
     */
    public static Set<EntityColumn> getPkColumns(Class<?> entityClass) {
        return getTableByEntity(entityClass).getEntityClassPkColumns();
    }

    /**
     * 获取查询的Select
     *
     * @param entityClass entityClass
     * @return String
     */
    public static String getSelectColumns(Class<?> entityClass) {
        EntityTable entityTable = getTableByEntity(entityClass);
        if (entityTable.getBaseSelect() != null) {
            return entityTable.getBaseSelect();
        }
        Set<EntityColumn> columnList = getColumns(entityClass);
        StringBuilder selectBuilder = new StringBuilder();
        boolean skipAlias = Map.class.isAssignableFrom(entityClass);
        for (EntityColumn entityColumn : columnList) {
            selectBuilder.append(entityColumn.getColumn());
            if (!skipAlias && !entityColumn.getColumn().equalsIgnoreCase(entityColumn.getProperty())) {
                //不等的时候分几种情况，例如`DESC`
                if (entityColumn.getColumn().substring(1, entityColumn.getColumn().length() - 1)
                        .equalsIgnoreCase(entityColumn.getProperty())) {
                    selectBuilder.append(",");
                } else {
                    selectBuilder.append(" AS ").append(entityColumn.getProperty()).append(",");
                }
            } else {
                selectBuilder.append(",");
            }
        }
        entityTable.setBaseSelect(selectBuilder.substring(0, selectBuilder.length() - 1));
        return entityTable.getBaseSelect();
    }

    /**
     * 初始化实体属性
     *
     * @param entityClass entityClass
     * @param mapperClass mapperClass
     * @param config      config
     */
    public static synchronized void initEntityNameMap(Class<?> entityClass, String mapperClass, Config config) {
        if (entityClassTableMap.containsKey(entityClass)
                && mapperClassTableMap.containsKey(mapperClass)) {
            return;
        }
        Style style = config.getStyle();

        //创建并缓存EntityTable
        EntityTable entityTable = null;
        if (entityClass.isAnnotationPresent(Table.class)) {
            Table table = entityClass.getAnnotation(Table.class);
            if (!table.name().equals("")) {
                entityTable = new EntityTable(entityClass);
                entityTable.setTable(table);
            }
        }
        if (entityTable == null) {
            entityTable = new EntityTable(entityClass);
            //可以通过style控制
            entityTable.setName(StringUtil.convertByStyle(entityClass.getSimpleName(), style));
        }
        if (entityClass.isAnnotationPresent(MultiLanguage.class)) {
            entityTable.setMultiLanguage(true);
            String tableName = entityTable.getName();
            if (StringUtil.tableNameAllUpperCase(tableName)) {
                entityTable.setMultiLanguageTableName(tableName + MULTI_LANGUAGE_TABLE_SUFFIX_UPPER_CASE);
            } else {
                entityTable.setMultiLanguageTableName(tableName + MULTI_LANGUAGE_TABLE_SUFFIX_LOW_CASE);
            }
        }
        entityTable.setEntityClassColumns(new LinkedHashSet<>());
        entityTable.setEntityClassPkColumns(new LinkedHashSet<>());
        entityTable.setMultiLanguageColumns(new LinkedHashSet<>());
        //处理所有列
        List<EntityField> fields = null;
        if (config.isEnableMethodAnnotation()) {
            fields = FieldHelper.getAll(entityClass);
        } else {
            fields = FieldHelper.getFields(entityClass);
        }
        for (EntityField field : fields) {
            //如果启用了简单类型，就做简单类型校验，如果不是简单类型，直接跳过
            //3.5.0 如果启用了枚举作为简单类型，就不会自动忽略枚举类型
            //4.0 如果标记了 Column 或 ColumnType 注解，也不忽略
            if (config.isUseSimpleType()
                    && !field.isAnnotationPresent(Column.class)
                    && !field.isAnnotationPresent(ColumnType.class)
                    && !SimpleTypeUtil.isSimpleType(field.getJavaType())) {
                continue;
            }
            processField(entityTable, style, field, config);
        }
        //当pk.size=0的时候使用所有列作为主键
        if (entityTable.getEntityClassPkColumns().isEmpty()) {
            entityTable.setEntityClassPkColumns(entityTable.getEntityClassColumns());
        }
        entityTable.initPropertyMap();
        entityClassTableMap.put(entityClass, entityTable);
        mapperClassTableMap.put(mapperClass, entityTable);
    }

    /**
     * 处理一列
     *
     * @param entityTable entityTable
     * @param style       style
     * @param field       field
     * @param config      config
     */
    private static void processField(EntityTable entityTable, Style style, EntityField field, Config config) {
        //排除字段
        if (field.isAnnotationPresent(Transient.class)) {
            return;
        }
        //Id
        EntityColumn entityColumn = new EntityColumn(entityTable);
        entityColumn.setField(field);
        if (field.isAnnotationPresent(Id.class)) {
            entityColumn.setId(true);
        }
        if (field.isAnnotationPresent(MultiLanguageField.class)) {
            entityColumn.setMultiLanguage(true);
        }
        //Column
        String columnName = null;
        if (field.isAnnotationPresent(Column.class)) {
            Column column = field.getAnnotation(Column.class);
            columnName = column.name();
            entityColumn.setUpdatable(column.updatable());
            entityColumn.setInsertable(column.insertable());
        }
        //ColumnType
        if (field.isAnnotationPresent(ColumnType.class)) {
            ColumnType columnType = field.getAnnotation(ColumnType.class);
            //是否为 blob 字段
            entityColumn.setBlob(columnType.isBlob());
            //column可以起到别名的作用
            if (StringUtil.isEmpty(columnName) && StringUtil.isNotEmpty(columnType.column())) {
                columnName = columnType.column();
            }
            if (columnType.jdbcType() != JdbcType.UNDEFINED) {
                entityColumn.setJdbcType(columnType.jdbcType());
            }
            if (columnType.typeHandler() != UnknownTypeHandler.class) {
                entityColumn.setTypeHandler(columnType.typeHandler());
            }
        }

        //表名
        if (StringUtil.isEmpty(columnName)) {
            columnName = StringUtil.convertByStyle(field.getName(), style);
        }
        entityColumn.setProperty(field.getName());
        entityColumn.setColumn(columnName);
        entityColumn.setJavaType(field.getJavaType());
        //OrderBy
        if (field.isAnnotationPresent(OrderBy.class)) {
            OrderBy orderBy = field.getAnnotation(OrderBy.class);
            if (orderBy.value().equals("")) {
                entityColumn.setOrderBy("ASC");
            } else {
                entityColumn.setOrderBy(orderBy.value());
            }
        }
        //主键策略 - Oracle序列，MySql自动增长，UUID
        primaryKeyStrategy(entityTable, field, entityColumn, config);
        if (entityColumn.isMultiLanguage()) {
            entityTable.getMultiLanguageColumns().add(entityColumn);
        } else if (entityColumn.isId()) {
            entityTable.getEntityClassPkColumns().add(entityColumn);
        }
        entityTable.getEntityClassColumns().add(entityColumn);
    }

    private static void primaryKeyStrategy(EntityTable entityTable, EntityField field, EntityColumn entityColumn, Config config) {
        if (field.isAnnotationPresent(SequenceGenerator.class)) {
            SequenceGenerator sequenceGenerator = field.getAnnotation(SequenceGenerator.class);
            if (sequenceGenerator.sequenceName().equals("")) {
                throw new MapperException(entityTable.getEntityClass() + "字段" + field.getName() + "的注解@SequenceGenerator未指定sequenceName!");
            }
            entityColumn.setSequenceName(sequenceGenerator.sequenceName());
        } else if (field.isAnnotationPresent(GeneratedValue.class)) {
            GeneratedValue generatedValue = field.getAnnotation(GeneratedValue.class);
            if (generatedValue.generator().equals("UUID")) {
                entityColumn.setUuid(true);
            } else if (generatedValue.generator().equals("JDBC")) {
                DbType dbType = config.getDbType();
                if (DbType.SQLSERVER.equals(dbType)) {
                    entityColumn.setInsertable(false);
                }
                entityColumn.setIdentity(true);
                entityColumn.setGenerator("JDBC");
                entityTable.setKeyProperties(entityColumn.getProperty());
                entityTable.setKeyColumns(entityColumn.getColumn());
            } else {
                //允许通过generator来设置获取id的sql,例如mysql=CALL IDENTITY(),hsqldb=SELECT SCOPE_IDENTITY()
                //允许通过拦截器参数设置公共的generator
                DbType dbType = config.getDbType();
                if (DbType.SQLSERVER.equals(dbType)) {
                    entityColumn.setInsertable(false);
                }
                dealByGeneratedValueStrategy(entityTable, entityColumn, generatedValue);
            }
        }
    }

    private static void dealByGeneratedValueStrategy(EntityTable entityTable,
                                                     EntityColumn entityColumn, GeneratedValue generatedValue) {
        if (generatedValue.strategy() == GenerationType.IDENTITY) {
            //mysql的自动增长
            entityColumn.setIdentity(true);
            if (!generatedValue.generator().equals("")) {
                String generator = null;
                IdentityDialect identityDialect =
                        IdentityDialect.getDatabaseDialect(generatedValue.generator());
                if (identityDialect != null) {
                    generator = identityDialect.getIdentityRetrievalStatement();
                } else {
                    generator = generatedValue.generator();
                }
                entityColumn.setGenerator(generator);
            }
        } else {
            entityColumn.setIdentity(true);
            //entityColumn.setGenerator("JDBC");
            entityTable.setKeyProperties(entityColumn.getProperty());
            entityTable.setKeyColumns(entityColumn.getColumn());
        }
    }
}