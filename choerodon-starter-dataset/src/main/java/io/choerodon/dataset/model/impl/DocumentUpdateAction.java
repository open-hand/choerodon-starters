package io.choerodon.dataset.model.impl;

import io.choerodon.dataset.exception.DatasetException;
import io.choerodon.dataset.model.Action;
import io.choerodon.mybatis.common.CustomProvider;
import io.choerodon.mybatis.util.OGNL;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.springframework.util.Assert;
import org.w3c.dom.Element;
import tk.mybatis.mapper.util.StringUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * @author xausky
 */
public class DocumentUpdateAction extends Action {
    private static final XMLLanguageDriver languageDriver = new XMLLanguageDriver();
    private String key;
    private String statement;
    private String statement_tl;
    private Set<String> tlColumns;

    public DocumentUpdateAction(String name, Element e, String key, String table, Set<String> tlColumns, Configuration configuration) {
        setPrefilter(e.getAttribute("prefilter").isEmpty()?null:e.getAttribute("prefilter"));
        setPostfilter(e.getAttribute("postfilter").isEmpty()?null:e.getAttribute("postfilter"));
        this.key = key;
        this.statement = "dataset." + name + ".update";
        this.tlColumns = new TreeSet<>();
        this.tlColumns.addAll(tlColumns);
        String columnsString = e.getAttribute("columns");
        Assert.isTrue(!columnsString.isEmpty(), "columns is required.");
        Set<String> columns = Arrays.stream(columnsString.split(",")).collect(Collectors.toSet());
        columns.add("lastUpdateDate");
        columns.add("lastUpdatedBy");
        StringBuilder xml = new StringBuilder();
        xml.append("<script>");
        xml.append("UPDATE ");
        xml.append(table);
        xml.append(" SET ");
        for (String column : columns) {
            if (this.tlColumns.contains(column)) {
                continue;
            }
            xml.append("<if test=\"");
            xml.append(column);
            xml.append(" != null \">");
            xml.append(StringUtil.camelhumpToUnderline(column));
            xml.append("=#{");
            xml.append(column);
            xml.append("},");
            xml.append("</if>");
        }
        xml.append("object_version_number = object_version_number + 1 WHERE ");
        xml.append(StringUtil.camelhumpToUnderline(key));
        xml.append("=#{");
        xml.append(key);
        xml.append("} AND object_version_number=#{objectVersionNumber}");
        xml.append("</script>");
        MappedStatement.Builder builder = new MappedStatement.Builder(configuration, statement,
                languageDriver.createSqlSource(configuration, xml.toString(), null), SqlCommandType.UPDATE);
        builder.resultMaps(Collections.singletonList(new ResultMap.Builder(configuration, statement, Map.class, Collections.emptyList()).build()));
        configuration.addMappedStatement(builder.build());
        if (!this.tlColumns.isEmpty()) {
            String tableTl = table.substring(0, table.length() - 2) + "_tl";
            this.tlColumns.add("lastUpdateDate");
            this.tlColumns.add("lastUpdatedBy");
            statement_tl = statement + "_tl";
            xml = new StringBuilder();
            xml.append("<script>");
            xml.append("UPDATE ");
            xml.append(tableTl);
            xml.append(" SET ");
            xml.append("<trim suffixOverrides=\",\">");
            for (String column : this.tlColumns) {
                xml.append("<if test=\"");
                xml.append(column);
                xml.append(" != null \">");
                xml.append(StringUtil.camelhumpToUnderline(column));
                xml.append("=#{");
                xml.append(column);
                xml.append("},</if>");
            }
            xml.append("</trim>");
            xml.append("WHERE ");
            xml.append(StringUtil.camelhumpToUnderline(key));
            xml.append("=#{");
            xml.append(key);
            xml.append("} AND LANG=#{__locale_lang}");
            xml.append("</script>");
            builder = new MappedStatement.Builder(configuration, statement_tl,
                    languageDriver.createSqlSource(configuration, xml.toString(), null), SqlCommandType.UPDATE);
            builder.resultMaps(Collections.singletonList(new ResultMap.Builder(configuration, statement_tl, Map.class, Collections.emptyList()).build()));
            configuration.addMappedStatement(builder.build());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object invoke(SqlSession session, Map<String, Object> parameter) {
        parameter.put("lastUpdateDate", new Date());
        parameter.put("lastUpdatedBy", OGNL.principal());
        if (session.update(statement, parameter) != 1) {
            throw new DatasetException("dataset.table.update.error");
        }
        parameter.put("objectVersionNumber", (Integer) parameter.get("objectVersionNumber") + 1);
        if (statement_tl == null) {
            return parameter;
        }
        Map<String, Object> tlParameter = new TreeMap<>();
        Map<String, Map<String, String>> lts = Collections.emptyMap();
        if (parameter.get("__tls") instanceof Map) {
            lts = (Map<String, Map<String, String>>) parameter.get("__tls");
        }
        String currentLangCode = OGNL.language();
        for (String langCode : OGNL.getSupportedLanguages()) {
            tlParameter.clear();
            for (String column : tlColumns) {
                Object value = parameter.get(column);
                Map<String, String> map = lts.get(column);
                if (map != null && map.containsKey(langCode)) {
                    tlParameter.put(column, map.get(langCode));
                } else if (parameter.containsKey(column) && currentLangCode.equals(langCode)) {
                    tlParameter.put(column, value);
                }
            }
            if (tlParameter.isEmpty()) {
                continue;
            }
            tlParameter.put("__locale_lang", langCode);
            tlParameter.put(key, parameter.get(key));
            tlParameter.put("lastUpdateDate", new Date());
            tlParameter.put("lastUpdatedBy", OGNL.principal());
            if (session.update(statement_tl, tlParameter) != 1) {
                throw new DatasetException("dataset.language.update.error");
            }
        }
        return parameter;
    }
}