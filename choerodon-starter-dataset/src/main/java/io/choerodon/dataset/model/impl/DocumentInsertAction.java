package io.choerodon.dataset.model.impl;


import io.choerodon.dataset.exception.DatasetException;
import io.choerodon.dataset.model.Action;
import io.choerodon.mybatis.util.OGNL;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.scripting.defaults.RawSqlSource;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.w3c.dom.Element;
import tk.mybatis.mapper.util.StringUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author vista
 * @since 18-11-13 上午11:07
 */
public class DocumentInsertAction extends Action {
    private static final Logger logger = LoggerFactory.getLogger(DocumentInsertAction.class);

    private static XMLLanguageDriver languageDriver = new XMLLanguageDriver();
    private String key;
    private String statement;
    private String statementTl;
    private Set<String> tlColumns;
    private DocumentDatasetExector.KeyType keyType;

    private static final String DB_TYPE_ORACLE = "oracle";
    private static final String DB_TYPE_POSTGRESQL = "postgresql";
    private static final String DB_TYPE_HANA = "hdb";
    private static final String FIELD_LAST_UPDATE_DATE = "lastUpdateDate";
    private static final String FIELD_LAST_UPDATED_BY = "lastUpdatedBy";
    private static final String FIELD_CREATED_BY = "createdBy";
    private static final String FIELD_CREATION_DATE = "creationDate";
    private static final String TRIM_LEFT_CLOSED = "<trim suffixOverrides=\",\">";
    private static final String TRIM_RIGHT_CLOSED = "</trim>";
    private static final String IF_LEFT_CLOSED = "<if test=\"";
    private static final String IF_JUDGE_CLOSED = " != null \">";
    private static final String IF_RIGHT_CLOSED = ",</if>";


    public DocumentInsertAction(String name, Element e, String key,
                                String tableName, Set<String> tlColumns,
                                Configuration configuration, DocumentDatasetExector.KeyType keyType) {
        setPrefilter(e.getAttribute("prefilter").isEmpty() ? null : e.getAttribute("prefilter"));
        setPostfilter(e.getAttribute("postfilter").isEmpty() ? null : e.getAttribute("postfilter"));
        this.key = key;
        this.keyType = keyType;
        this.statement = "dataset." + name + ".insert";
        this.tlColumns = new TreeSet<>();
        this.tlColumns.addAll(tlColumns);
        String columnsString = e.getAttribute("columns");
        Assert.isTrue(!columnsString.isEmpty(), "columns is required.");
        Set<String> columns = Arrays.stream(columnsString.split(",")).collect(Collectors.toSet());
        columns.add(FIELD_LAST_UPDATE_DATE);
        columns.add(FIELD_LAST_UPDATED_BY);
        columns.add(FIELD_CREATED_BY);
        columns.add(FIELD_CREATION_DATE);
        if (DocumentDatasetExector.KeyType.UUID == keyType) {
            columns.add(key);
        }
        StringBuilder xml = new StringBuilder();
        xml.append("<script>");
        xml.append("INSERT INTO ");
        xml.append(tableName);
        xml.append(" ( ");
        xml.append(TRIM_LEFT_CLOSED);
        for (String column : columns) {
            xml.append(IF_LEFT_CLOSED);
            xml.append(column);
            xml.append(IF_JUDGE_CLOSED);
            xml.append(StringUtil.camelhumpToUnderline(column));
            xml.append(IF_RIGHT_CLOSED);
        }
        if (getSupportSequenceDbType(configuration) != null) {
            xml.append(StringUtil.camelhumpToUnderline(key));
        }
        xml.append(TRIM_RIGHT_CLOSED);
        xml.append(" ) ");
        xml.append(" VALUES ( ");
        xml.append(TRIM_LEFT_CLOSED);
        for (String column : columns) {
            xml.append(IF_LEFT_CLOSED);
            xml.append(column);
            xml.append(IF_JUDGE_CLOSED);
            xml.append("#{");
            xml.append(column);
            xml.append("} ");
            xml.append(IF_RIGHT_CLOSED);
        }
        if (getSupportSequenceDbType(configuration) != null) {
            xml.append("#{");
            xml.append(key);
            xml.append("} ");
        }
        xml.append(TRIM_RIGHT_CLOSED);
        xml.append(" ) ");
        xml.append("</script>");
        MappedStatement.Builder builder = new MappedStatement.Builder(configuration, statement,
                languageDriver.createSqlSource(configuration, xml.toString(), null), SqlCommandType.INSERT);
        builder.keyProperty(key);
        builder.resultMaps(Collections.singletonList(new ResultMap.Builder(configuration, statement, Map.class, Collections.emptyList()).build()));
        MappedStatement insert = builder.build();
        configuration.addMappedStatement(insert);
        createSelectKeyMappedStatement(configuration, tableName, insert);
        if (!this.tlColumns.isEmpty()) {
            String tableTl = tableName.substring(0, tableName.length() - 2) + "_tl";
            this.tlColumns.add(FIELD_LAST_UPDATE_DATE);
            this.tlColumns.add(FIELD_LAST_UPDATED_BY);
            this.tlColumns.add(FIELD_CREATED_BY);
            this.tlColumns.add(FIELD_CREATION_DATE);
            statementTl = statement + "_tl";
            xml = new StringBuilder();
            xml.append("<script>");
            xml.append("INSERT INTO ");
            xml.append(tableTl);
            xml.append(" ( ");
            xml.append(TRIM_LEFT_CLOSED);
            xml.append("LANG,");
            xml.append(StringUtil.camelhumpToUnderline(key));
            xml.append(",");
            for (String column : this.tlColumns) {
                xml.append(IF_LEFT_CLOSED);
                xml.append(column);
                xml.append(IF_JUDGE_CLOSED);
                xml.append(StringUtil.camelhumpToUnderline(column));
                xml.append(IF_RIGHT_CLOSED);
            }
            xml.append(TRIM_RIGHT_CLOSED);
            xml.append(" ) ");
            xml.append(" VALUES ( ");
            xml.append(TRIM_LEFT_CLOSED);
            xml.append("#{__locale_lang}, #{");
            xml.append(key);
            xml.append("},");
            for (String column : this.tlColumns) {
                xml.append(IF_LEFT_CLOSED);
                xml.append(column);
                xml.append(IF_JUDGE_CLOSED);
                xml.append("#{");
                xml.append(column);
                xml.append("} ");
                xml.append(IF_RIGHT_CLOSED);
            }
            xml.append(TRIM_RIGHT_CLOSED);
            xml.append(" ) ");
            xml.append("</script>");
            builder = new MappedStatement.Builder(configuration, statementTl,
                    languageDriver.createSqlSource(configuration, xml.toString(), null), SqlCommandType.INSERT);
            builder.keyGenerator(new NoKeyGenerator());
            builder.resultMaps(Collections.singletonList(new ResultMap.Builder(configuration, statementTl, Map.class, Collections.emptyList()).build()));
            configuration.addMappedStatement(builder.build());
        }
    }

    @Override
    public Object invoke(SqlSession session, Map<String, Object> parameter) {
        setStdParameter(parameter);
        if (keyType == DocumentDatasetExector.KeyType.UUID) {
            parameter.put(key, UUID.randomUUID().toString().replace("-", ""));
        }
        if (session.insert(statement, parameter) != 1) {
            throw new DatasetException("dataset.table.insert.error", null);
        }
        parameter.put("objectVersionNumber", 1);
        if (statementTl == null) {
            return parameter;
        }
        insertTl(session, parameter);
        return parameter;
    }

    /**
     * 插入多语言.
     *
     * @param session   SqlSession
     * @param parameter 参数
     */
    @SuppressWarnings("unchecked")
    private void insertTl(SqlSession session, Map<String, Object> parameter) {
        Map<String, Object> tlParameter = new TreeMap<>();
        Map<String, Map<String, String>> tls = Collections.emptyMap();
        if (parameter.get("__tls") instanceof Map) {
            tls = (Map<String, Map<String, String>>) parameter.get("__tls");
        }
        for (String langCode : OGNL.getSupportedLanguages()) {
            tlParameter.clear();
            for (String column : tlColumns) {
                Object value = parameter.get(column);
                Map<String, String> tlsMap = tls.get(column);
                if (tlsMap != null && tlsMap.containsKey(langCode)) {
                    tlParameter.put(column, tlsMap.get(langCode));
                } else if (value != null) {
                    tlParameter.put(column, value);
                }
            }
            if (tlParameter.isEmpty()) {
                continue;
            }
            tlParameter.put("__locale_lang", langCode);
            tlParameter.put(key, parameter.get(key));
            setStdParameter(tlParameter);
            if (session.insert(statementTl, tlParameter) != 1) {
                throw new DatasetException("dataset.language.insert.error", null);
            }
        }
    }

    /**
     * 设置 WHO 字段值.
     *
     * @param parameter 参数
     */
    private void setStdParameter(Map<String, Object> parameter) {
        parameter.put(FIELD_LAST_UPDATED_BY, OGNL.principal());
        parameter.put(FIELD_LAST_UPDATE_DATE, new Date());
        parameter.put(FIELD_CREATED_BY, OGNL.principal());
        parameter.put(FIELD_CREATION_DATE, new Date());
    }

    /**
     * 创建序列 selectKey 映射语句.
     *
     * @param configuration         数据库配置类
     * @param tableName             数据库表名
     * @param insertMappedStatement 插入映射语句
     */
    private void createSelectKeyMappedStatement(Configuration configuration, String tableName, MappedStatement insertMappedStatement) {
        String identity;
        String dbType = getSupportSequenceDbType(configuration);
        if (dbType != null) {
            switch (dbType.toLowerCase()) {
                case DB_TYPE_HANA:
                    identity = "SELECT " + tableName + "_s.nextval FROM DUMMY";
                    break;
                case DB_TYPE_POSTGRESQL:
                    identity = "SELECT nextval('" + tableName + "_s')";
                    break;
                default:
                    identity = "SELECT " + tableName + "_s.nextval FROM DUAL";
                    break;
            }
            String keyId = statement + SelectKeyGenerator.SELECT_KEY_SUFFIX;
            MappedStatement.Builder statementBuilder = new MappedStatement.Builder(configuration, keyId, new RawSqlSource(configuration, identity, null), SqlCommandType.SELECT);

            statementBuilder.statementType(StatementType.STATEMENT);
            statementBuilder.keyGenerator(new NoKeyGenerator());
            statementBuilder.keyProperty(key);
            statementBuilder.keyColumn(key);
            statementBuilder.resultMaps(Collections.singletonList(new ResultMap.Builder(configuration, statement, Long.class, Collections.emptyList()).build()));

            configuration.addMappedStatement(statementBuilder.build());
            MappedStatement keyStatement = configuration.getMappedStatement(keyId, false);
            KeyGenerator keyGenerator = new SelectKeyGenerator(keyStatement, true);
            configuration.addKeyGenerator(keyId, keyGenerator);
            MetaObject msObject = SystemMetaObject.forObject(insertMappedStatement);
            msObject.setValue("keyGenerator", keyGenerator);
            msObject.setValue("keyProperties", key.split(","));
            msObject.setValue("keyColumns", key.split(","));
        }
    }

    /**
     * 返回支持序列的数据库类型.
     *
     * @param configuration 数据库配置类
     * @return 数据库类型或null
     */
    private String getSupportSequenceDbType(Configuration configuration) {
        String dbType;
        Connection connection = null;
        try {
            connection = configuration.getEnvironment().getDataSource().getConnection();
            dbType = connection.getMetaData().getDatabaseProductName();
            if (DB_TYPE_ORACLE.equalsIgnoreCase(dbType) || DB_TYPE_POSTGRESQL.equalsIgnoreCase(dbType) || DB_TYPE_HANA.equalsIgnoreCase(dbType)) {
                return dbType;
            }
        } catch (SQLException e) {
            throw new DatasetException("dataset.error", e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return null;
    }
}
