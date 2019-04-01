package io.choerodon.dataset.model.impl;

import io.choerodon.dataset.exception.DatasetException;
import io.choerodon.dataset.metadata.dto.MetadataColumn;
import io.choerodon.dataset.metadata.dto.MetadataTable;
import io.choerodon.dataset.model.Action;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import tk.mybatis.mapper.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CommonValidateAction extends Action {
    private static final XMLLanguageDriver languageDriver = new XMLLanguageDriver();
    private String statement;
    public CommonValidateAction(String name, MetadataTable table, Configuration configuration) {
        this.statement = "dataset." + name + ".validate";
        StringBuilder xml = new StringBuilder();
        xml.append("<script>");
        xml.append("SELECT COUNT(*) FROM ");
        xml.append(table.getTableName());
        xml.append("<trim prefix=\" WHERE \" suffixOverrides=\"AND\">");
        for (MetadataColumn column : table.getColumns()) {
            xml.append("<if test=\"");
            xml.append(StringUtil.underlineToCamelhump(column.getColumnName().toLowerCase()));
            xml.append(" != null \">");
            xml.append(column.getColumnName());
            xml.append("=");
            xml.append("#{");
            xml.append(StringUtil.underlineToCamelhump(column.getColumnName().toLowerCase()));
            xml.append('}');
            xml.append(" AND ");
            xml.append("</if>");
        }
        xml.append("</trim></script>");
        MappedStatement.Builder builder = new MappedStatement.Builder(configuration, statement,
                languageDriver.createSqlSource(configuration, xml.toString(), null), SqlCommandType.SELECT);
        builder.resultMaps(Collections.singletonList(new ResultMap.Builder(configuration, statement, Map.class, Collections.emptyList()).build()));
        configuration.addMappedStatement(builder.build());
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Object invoke(SqlSession session, Map<String, Object> parameter) throws DatasetException {
        List<Boolean> result = new ArrayList<>();
        try {
            if (parameter.get("unique") instanceof List) {
                List<Map<String, String>> uniqueGroups = ((List) parameter.get("unique"));
                for (Map<String, String> uniqueGroup : uniqueGroups){
                    Object object = session.selectOne(statement, uniqueGroup);
                    if(((Map<String, Long>)object).get("COUNT(*)").equals(0L)){
                        result.add(Boolean.TRUE);
                    } else {
                        result.add(Boolean.FALSE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new DatasetException("dataset.error", e);
        }
        return result;
    }
}
