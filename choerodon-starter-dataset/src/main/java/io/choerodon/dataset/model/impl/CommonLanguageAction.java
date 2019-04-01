package io.choerodon.dataset.model.impl;

import io.choerodon.dataset.model.Action;
import io.choerodon.mybatis.util.OGNL;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import tk.mybatis.mapper.util.StringUtil;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class CommonLanguageAction extends Action {
    private static final XMLLanguageDriver languageDriver = new XMLLanguageDriver();
    private String statement;
    private Set<String> tlColumns;
    public CommonLanguageAction(String name, String key, String table, Set<String> tlColumns, Configuration configuration){
        this.tlColumns = tlColumns;
        this.statement = "dataset." + name + ".language";
        String tableTl = table.substring(0, table.length() - 2) + "_tl";
        StringBuilder xml = new StringBuilder();
        xml.append("<script>");
        xml.append("SELECT ");
        xml.append("<trim suffixOverrides=\",\">");
        for (String column : tlColumns) {
            xml.append(StringUtil.camelhumpToUnderline(column));
            xml.append(',');
        }
        xml.append("LANG,");
        xml.append("</trim>");
        xml.append(" FROM ");
        xml.append(tableTl);
        xml.append(" WHERE ");
        xml.append(StringUtil.camelhumpToUnderline(key));
        xml.append("=#{key}</script>");
        MappedStatement.Builder builder = new MappedStatement.Builder(configuration, statement,
                languageDriver.createSqlSource(configuration, xml.toString(), null), SqlCommandType.SELECT);
        builder.resultMaps(Collections.singletonList(new ResultMap.Builder(configuration, statement, Map.class,
                tlColumns.stream().map(c -> new ResultMapping.Builder(configuration, c,
                        StringUtil.camelhumpToUnderline(c), Object.class).build()).collect(Collectors.toList())
        ).build()));
        configuration.addMappedStatement(builder.build());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object invoke(SqlSession session, Map<String, Object> parameter) {
        Map<String, Object> result = new TreeMap<>();
        for(String column: tlColumns){
            Map<String, Object> columnMultiLanguage = new TreeMap<>();
            OGNL.getSupportedLanguages().forEach(l -> {
                columnMultiLanguage.put(l, null);
            });
            result.put(column, columnMultiLanguage);
        }
        List<Map<String, Object>> rows = session.selectList(statement, parameter);
        for(Map<String, Object> row: rows){
            String langCode = (String) row.get("LANG");
            for(String key: row.keySet()){
                if("LANG".equals(key)){
                    continue;
                }
                Map<String, Object> column = (Map<String, Object>) result.get(key);
                if(column != null){
                    column.put(langCode, row.get(key));
                }
            }
        }
        return result;
    }
}
