package io.choerodon.mybatis.mapperhelper;

import io.choerodon.mybatis.annotation.EnableExtensionAttribute;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.common.query.JoinColumn;
import io.choerodon.mybatis.common.query.JoinOn;
import io.choerodon.mybatis.common.query.JoinTable;
import io.choerodon.mybatis.common.query.Where;
import io.choerodon.mybatis.entity.BaseDTO;
import io.choerodon.mybatis.entity.CustomEntityColumn;
import io.choerodon.mybatis.entity.CustomEntityTable;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.UnknownTypeHandler;
import tk.mybatis.mapper.MapperException;
import tk.mybatis.mapper.annotation.ColumnType;
import tk.mybatis.mapper.annotation.KeySql;
import tk.mybatis.mapper.annotation.NameStyle;
import tk.mybatis.mapper.annotation.Order;
import tk.mybatis.mapper.code.IdentityDialect;
import tk.mybatis.mapper.code.ORDER;
import tk.mybatis.mapper.code.Style;
import tk.mybatis.mapper.entity.Config;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.entity.EntityField;
import tk.mybatis.mapper.entity.EntityTable;
import tk.mybatis.mapper.genid.GenId;
import tk.mybatis.mapper.gensql.GenSql;
import tk.mybatis.mapper.mapperhelper.FieldHelper;
import tk.mybatis.mapper.mapperhelper.resolve.EntityResolve;
import tk.mybatis.mapper.util.SimpleTypeUtil;
import tk.mybatis.mapper.util.SqlReservedWords;
import tk.mybatis.mapper.util.StringUtil;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.criteria.JoinType;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * @author liuzh
 */
public class CustomEntityResolve implements EntityResolve {
    private static final Log log = LogFactory.getLog(CustomEntityResolve.class);

    @Override
    public EntityTable resolveEntity(Class<?> entityClass, Config config) {
        Style style = config.getStyle();
        //style，该注解优先于全局配置
        if (entityClass.isAnnotationPresent(NameStyle.class)) {
            NameStyle nameStyle = entityClass.getAnnotation(NameStyle.class);
            style = nameStyle.value();
        }

        //创建并缓存EntityTable
        CustomEntityTable entityTable = null;
        if (entityClass.isAnnotationPresent(Table.class)) {
            Table table = entityClass.getAnnotation(Table.class);
            if (!"".equals(table.name())) {
                entityTable = new CustomEntityTable(entityClass);
                entityTable.setTable(table);
            }
        }
        if (entityTable == null) {
            entityTable = new CustomEntityTable(entityClass);
            //可以通过stye控制
            String tableName = StringUtil.convertByStyle(entityClass.getSimpleName(), style);
            //自动处理关键字
            if (StringUtil.isNotEmpty(config.getWrapKeyword()) && SqlReservedWords.containsWord(tableName)) {
                tableName = MessageFormat.format(config.getWrapKeyword(), tableName);
            }
            entityTable.setName(tableName);
        }
        if (entityClass.isAnnotationPresent(MultiLanguage.class)) {
            entityTable.setMultiLanguage(true);
            entityTable.setMultiLanguageColumns(new LinkedHashSet<EntityColumn>());
        }
        entityTable.setEntityClassColumns(new LinkedHashSet<EntityColumn>());
        entityTable.setEntityClassPKColumns(new LinkedHashSet<EntityColumn>());
        //处理所有列
        List<EntityField> fields = null;
        if (config.isEnableMethodAnnotation()) {
            fields = FieldHelper.getAll(entityClass);
        } else {
            fields = FieldHelper.getFields(entityClass);
        }
        boolean useExt = entityClass.isAnnotationPresent(EnableExtensionAttribute.class);
        for (EntityField field : fields) {
            //如果启用了简单类型，就做简单类型校验，如果不是简单类型，直接跳过
            //3.5.0 如果启用了枚举作为简单类型，就不会自动忽略枚举类型
            //4.0 如果标记了 Column 或 ColumnType 注解，也不忽略
            if (config.isUseSimpleType()
                    && !field.isAnnotationPresent(Column.class)
                    && !field.isAnnotationPresent(ColumnType.class)
                    && !(SimpleTypeUtil.isSimpleType(field.getJavaType())
                    ||
                    (config.isEnumAsSimpleType() && Enum.class.isAssignableFrom(field.getJavaType())))
                    || (!useExt && field.getName().matches("attribute(\\d+|Category)"))) {
                continue;
            }
            processField(entityTable, field, config, style);
        }
        //当pk.size=0的时候使用所有列作为主键
        if (entityTable.getEntityClassPKColumns().size() == 0) {
            entityTable.setEntityClassPKColumns(entityTable.getEntityClassColumns());
        }
        entityTable.initPropertyMap();
        return entityTable;
    }

    /**
     * 处理字段
     *
     * @param entityTable
     * @param field
     * @param config
     * @param style
     */
    protected void processField(CustomEntityTable entityTable, EntityField field, Config config, Style style) {
        //Id
        CustomEntityColumn entityColumn = new CustomEntityColumn(entityTable);
        //是否使用 {xx, javaType=xxx}
        entityColumn.setUseJavaType(config.isUseJavaType());
        //记录 field 信息，方便后续扩展使用
        entityColumn.setEntityField(field);
        if (field.isAnnotationPresent(Id.class)) {
            entityColumn.setId(true);
        }

        //排除字段
        if (field.isAnnotationPresent(Transient.class)) {
            entityColumn.setSelectable(false);
            entityColumn.setInsertable(false);
            entityColumn.setUpdatable(false);
        }

        //JoinTable
        if (entityTable.isMultiLanguage()) {
            if (field.isAnnotationPresent(Id.class)) {
                JoinTable jt = new JoinTable() {
                    @Override
                    public Class<? extends java.lang.annotation.Annotation> annotationType() {
                        return JoinTable.class;
                    }

                    @Override
                    public String name() {
                        return "multiLanguageJoin";
                    }

                    @Override
                    public boolean joinMultiLanguageTable() {
                        return true;
                    }

                    @Override
                    public Class<?> target() {
                        return entityTable.getEntityClass();
                    }

                    @Override
                    public JoinType type() {
                        return JoinType.INNER;
                    }

                    @Override
                    public JoinOn[] on() {
                        JoinOn on1 = new JoinOn() {
                            @Override
                            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                                return JoinOn.class;
                            }

                            @Override
                            public String joinField() {
                                return field.getName();
                            }

                            @Override
                            public String joinExpression() {
                                return "";
                            }
                        };

                        JoinOn on2 = new JoinOn() {
                            @Override
                            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                                return JoinOn.class;
                            }

                            @Override
                            public String joinField() {
                                return BaseDTO.FIELD_LANG;
                            }

                            @Override
                            public String joinExpression() {
                                return "__current_locale";
                            }
                        };


                        return new JoinOn[]{on1, on2};
                    }


                };

                entityColumn.addJoinTable(jt);
                entityTable.createAlias(buildJoinKey(jt));
                entityTable.getJoinMapping().put(jt.name(), entityColumn);
            }

            if (field.isAnnotationPresent(MultiLanguageField.class)) {
                JoinColumn jc = new JoinColumn() {

                    @Override
                    public Class<? extends java.lang.annotation.Annotation> annotationType() {
                        return JoinColumn.class;
                    }

                    @Override
                    public String joinName() {
                        return "multiLanguageJoin";
                    }

                    @Override
                    public String field() {
                        return field.getName();
                    }

                    @Override
                    public String expression() {
                        return "";
                    }
                };
                entityColumn.setJoinColumn(jc);
                entityColumn.setSelectable(true);
            }
        }
        // @JoinTable
        try {
            Field entityField = EntityField.class.getDeclaredField("field");
            entityField.setAccessible(true);
            Field metaFiled = (Field) entityField.get(field);
            if (metaFiled == null) {
                log.warn("the field property is empty in the entityField: " + entityField.getName());
            } else {
                JoinTable[] jts = metaFiled.getAnnotationsByType(JoinTable.class);
                if (jts != null) {
                    for (JoinTable joinTable : jts) {
                        entityColumn.addJoinTable(joinTable);
                        entityTable.createAlias(buildJoinKey(joinTable));
                        entityTable.getJoinMapping().put(joinTable.name(), entityColumn);
                    }
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        // @JoinColumn
        if (field.isAnnotationPresent(JoinColumn.class)) {
            JoinColumn jc = field.getAnnotation(JoinColumn.class);
            entityColumn.setJoinColumn(jc);
            entityColumn.setSelectable(true);
            entityColumn.setInsertable(false);
            entityColumn.setUpdatable(false);
        }

        // @Where
        if (field.isAnnotationPresent(Where.class)) {
            Where where = field.getAnnotation(Where.class);
            entityColumn.setWhere(where);
            entityTable.getWhereColumns().add(entityColumn);
        }
        //Column
        String columnName = null;
        if (field.isAnnotationPresent(Column.class)) {
            Column column = field.getAnnotation(Column.class);
            columnName = column.name();
            entityColumn.setUpdatable(column.updatable());
            entityColumn.setInsertable(column.insertable());
        }
        if (field.isAnnotationPresent(MultiLanguageField.class)) {
            entityColumn.setMultiLanguage(true);
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
        //列名
        if (StringUtil.isEmpty(columnName)) {
            columnName = StringUtil.convertByStyle(field.getName(), style);
        }
        //自动处理关键字
        if (StringUtil.isNotEmpty(config.getWrapKeyword()) && SqlReservedWords.containsWord(columnName)) {
            columnName = MessageFormat.format(config.getWrapKeyword(), columnName);
        }
        entityColumn.setProperty(field.getName());
        entityColumn.setColumn(columnName);
        entityColumn.setJavaType(field.getJavaType());
        if (field.getJavaType().isPrimitive()) {
            log.warn("通用 Mapper 警告信息: <[" + entityColumn + "]> 使用了基本类型，基本类型在动态 SQL 中由于存在默认值，因此任何时候都不等于 null，建议修改基本类型为对应的包装类型!");
        }
        //OrderBy
        processOrderBy(entityTable, field, entityColumn);
        //处理主键策略
        processKeyGenerator(entityTable, field, entityColumn);
        if (entityColumn.isId()) {
            entityTable.getEntityClassPKColumns().add(entityColumn);
        }
        if (entityColumn.isMultiLanguage()) {
            entityTable.getMultiLanguageColumns().add(entityColumn);
        }
        if (!field.isAnnotationPresent(Transient.class)) {
            entityTable.getEntityClassColumns().add(entityColumn);
        }
        if (entityColumn.isSelectable()) {
            entityTable.getAllColumns().add(entityColumn);
        }
    }

    /**
     * 处理排序
     *
     * @param entityTable
     * @param field
     * @param entityColumn
     */
    protected void processOrderBy(EntityTable entityTable, EntityField field, EntityColumn entityColumn) {
        String orderBy = "";
        if (field.isAnnotationPresent(OrderBy.class)) {
            orderBy = field.getAnnotation(OrderBy.class).value();
            if ("".equals(orderBy)) {
                orderBy = "ASC";
            }
            log.warn(OrderBy.class + " is outdated, use " + Order.class + " instead!");
        }
        if (field.isAnnotationPresent(Order.class)) {
            Order order = field.getAnnotation(Order.class);
            if ("".equals(order.value()) && "".equals(orderBy)) {
                orderBy = "ASC";
            } else {
                orderBy = order.value();
            }
            entityColumn.setOrderPriority(order.priority());
        }
        if (StringUtil.isNotEmpty(orderBy)) {
            entityColumn.setOrderBy(orderBy);
        }
    }

    /**
     * 处理主键策略
     *
     * @param entityTable
     * @param field
     * @param entityColumn
     */
    protected void processKeyGenerator(EntityTable entityTable, EntityField field, EntityColumn entityColumn) {
        //KeySql 优先级最高
        if (field.isAnnotationPresent(KeySql.class)) {
            processKeySql(entityTable, entityColumn, field.getAnnotation(KeySql.class));
        } else if (field.isAnnotationPresent(GeneratedValue.class)) {
            //执行 sql - selectKey
            processGeneratedValue(entityTable, entityColumn, field.getAnnotation(GeneratedValue.class));
        }
    }

    /**
     * 处理 GeneratedValue 注解
     *
     * @param entityTable
     * @param entityColumn
     * @param generatedValue
     */
    protected void processGeneratedValue(EntityTable entityTable, EntityColumn entityColumn, GeneratedValue generatedValue) {
        if ("JDBC".equals(generatedValue.generator())) {
            entityColumn.setIdentity(true);
            entityColumn.setGenerator("JDBC");
            entityTable.setKeyProperties(entityColumn.getProperty());
            entityTable.setKeyColumns(entityColumn.getColumn());
        } else {
            //允许通过generator来设置获取id的sql,例如mysql=CALL IDENTITY(),hsqldb=SELECT SCOPE_IDENTITY()
            //允许通过拦截器参数设置公共的generator
            if (generatedValue.strategy() == GenerationType.IDENTITY) {
                //mysql的自动增长
                entityColumn.setIdentity(true);
                if (!"".equals(generatedValue.generator())) {
                    String generator = null;
                    IdentityDialect identityDialect = IdentityDialect.getDatabaseDialect(generatedValue.generator());
                    if (identityDialect != null) {
                        generator = identityDialect.getIdentityRetrievalStatement();
                    } else {
                        generator = generatedValue.generator();
                    }
                    entityColumn.setGenerator(generator);
                }
            } else {
                throw new MapperException(entityColumn.getProperty()
                        + " - 该字段@GeneratedValue配置只允许以下几种形式:" +
                        "\n1.useGeneratedKeys的@GeneratedValue(generator=\\\"JDBC\\\")  " +
                        "\n2.类似mysql数据库的@GeneratedValue(strategy=GenerationType.IDENTITY[,generator=\"Mysql\"])");
            }
        }
    }

    /**
     * 处理 KeySql 注解
     *
     * @param entityTable
     * @param entityColumn
     * @param keySql
     */
    protected void processKeySql(EntityTable entityTable, EntityColumn entityColumn, KeySql keySql) {
        if (keySql.useGeneratedKeys()) {
            entityColumn.setIdentity(true);
            entityColumn.setGenerator("JDBC");
            entityTable.setKeyProperties(entityColumn.getProperty());
            entityTable.setKeyColumns(entityColumn.getColumn());
        } else if (keySql.dialect() == IdentityDialect.DEFAULT) {
            entityColumn.setIdentity(true);
            entityColumn.setOrder(ORDER.AFTER);
        } else if (keySql.dialect() != IdentityDialect.NULL) {
            //自动增长
            entityColumn.setIdentity(true);
            entityColumn.setOrder(ORDER.AFTER);
            entityColumn.setGenerator(keySql.dialect().getIdentityRetrievalStatement());
        } else if (StringUtil.isNotEmpty(keySql.sql())) {

            entityColumn.setIdentity(true);
            entityColumn.setOrder(keySql.order());
            entityColumn.setGenerator(keySql.sql());
        } else if (keySql.genSql() != GenSql.NULL.class) {
            entityColumn.setIdentity(true);
            entityColumn.setOrder(keySql.order());
            try {
                GenSql genSql = keySql.genSql().newInstance();
                entityColumn.setGenerator(genSql.genSql(entityTable, entityColumn));
            } catch (Exception e) {
                log.error("实例化 GenSql 失败: " + e, e);
                throw new MapperException("实例化 GenSql 失败: " + e, e);
            }
        } else if (keySql.genId() != GenId.NULL.class) {
            entityColumn.setIdentity(false);
            entityColumn.setGenIdClass(keySql.genId());
        } else {
            throw new MapperException(entityTable.getEntityClass().getCanonicalName()
                    + " 类中的 @KeySql 注解配置无效!");
        }
    }

    public static String buildJoinKey(JoinTable jt) {
        return jt.target().getCanonicalName() + "." + jt.name();
    }

}
