package io.choerodon.dataset.model.impl;

import io.choerodon.dataset.exception.DatasetException;
import io.choerodon.dataset.model.Action;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.w3c.dom.Element;
import tk.mybatis.mapper.util.StringUtil;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @author vista
 * @time 18-11-12 下午4:47
 */
public class DocumentDeleteAction extends Action {
    private static final XMLLanguageDriver languageDriver = new XMLLanguageDriver();
    private String statement;
    private String statement_tl;

    public DocumentDeleteAction(String name, Element e, String key, String table, Set<String> tlColumns,
                                Configuration configuration) {
        this.statement = "dataset." + name + ".delete";
        setPrefilter(e.getAttribute("prefilter").isEmpty()?null:e.getAttribute("prefilter"));
        setPostfilter(e.getAttribute("postfilter").isEmpty()?null:e.getAttribute("postfilter"));
        StringBuilder xml = new StringBuilder();
        xml.append("<script>");
        xml.append("DELETE FROM ");
        xml.append(table);
        xml.append(" WHERE ");
        xml.append("<choose>");
        xml.append("<when test=\"");
        xml.append(key);
        xml.append(" != null\">");
        xml.append(StringUtil.camelhumpToUnderline(key));
        xml.append(" = ");
        xml.append("#{");
        xml.append(key);
        xml.append("} AND object_version_number=#{objectVersionNumber}");
        xml.append("</when>");
        xml.append("<otherwise>");
        xml.append("AND 0 = 1");
        xml.append("</otherwise>");
        xml.append("</choose>");
        xml.append("</script>");
        MappedStatement.Builder builder = new MappedStatement.Builder(configuration, statement,
                languageDriver.createSqlSource(configuration, xml.toString(), null), SqlCommandType.UPDATE);
        builder.resultMaps(Collections.singletonList(new ResultMap.Builder(configuration, statement, Map.class, Collections.emptyList()).build()));
        configuration.addMappedStatement(builder.build());
        if (!tlColumns.isEmpty()) {
            String tableTl = table.substring(0, table.length() - 2) + "_tl";
            // 多语言扩展表的删除语句
            statement_tl = statement + "_tl";
            xml = new StringBuilder();
            xml.append("<script>");
            xml.append("DELETE FROM ");
            xml.append(tableTl);
            xml.append(" WHERE ");
            xml.append("<choose>");
            xml.append("<when test=\"");
            xml.append(key);
            xml.append(" != null\">");
            xml.append(StringUtil.camelhumpToUnderline(key));
            xml.append(" = ");
            xml.append("#{");
            xml.append(key);
            xml.append("} ");
            xml.append("</when>");
            xml.append("<otherwise>");
            xml.append("AND 0 = 1");
            xml.append("</otherwise>");
            xml.append("</choose>");
            xml.append("</script>");
            builder = new MappedStatement.Builder(configuration, statement_tl,
                    languageDriver.createSqlSource(configuration, xml.toString(), null), SqlCommandType.UPDATE);
            builder.resultMaps(Collections.singletonList(new ResultMap.Builder(configuration, statement_tl, Map.class, Collections.emptyList()).build()));
            configuration.addMappedStatement(builder.build());
        }
    }

    @Override
    public Object invoke(SqlSession session, Map<String, Object> parameter) {
        if (session.delete(statement, parameter) != 1) {
            throw  new DatasetException("dataset.table.delete.error");
        }
        if(statement_tl == null){
            return parameter;
        }
        if (session.delete(statement_tl, parameter) <= 0) {
            throw new DatasetException("dataset.language.delete.error");
        }
        return parameter;
    }
}
