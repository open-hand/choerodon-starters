package io.choerodon.dataset.model.impl;

import com.github.pagehelper.PageHelper;
import io.choerodon.dataset.metadata.IMetadataTableService;
import io.choerodon.dataset.metadata.dto.MetadataColumn;
import io.choerodon.dataset.metadata.dto.MetadataTable;
import io.choerodon.dataset.model.Action;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.springframework.util.Assert;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tk.mybatis.mapper.util.StringUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xausky
 */
public class DocumentSelectAction extends Action {
    private static final XMLLanguageDriver languageDriver = new XMLLanguageDriver();
    private String statement;
    private Map<String, Field> fields = new TreeMap<>();
    private String sortColumn;
    private boolean desc;
    private class Field {
        public String name;
        public String columnName;
        public String table;
        public String operation;

        public Field(Element e) {
            this.name = e.getAttribute("name");
            this.table = e.getAttribute("table");
            this.columnName = e.getAttribute("columnName");
            this.operation = e.getAttribute("operation");
        }

        public Field(String name, String table, boolean sort, boolean desc) {
            this.name = name;
            this.table = table;
            this.columnName = StringUtil.camelhumpToUnderline(name).toUpperCase();
            this.operation = "contains";
        }
    }
    private class Filter {
        public String type;
        public String value;
        public String table;
        public String valueTable;
        public String operation;
        public String columnName;

        public Filter(Element e, String table) {
            this.table = e.getAttribute("table");
            if(this.table.isEmpty()){
                this.table = table;
            }
            this.type = e.getAttribute("type");
            this.value = e.getAttribute("value");
            this.valueTable = e.getAttribute("valueTable");
            this.operation = e.getAttribute("operation");
            this.columnName = e.getAttribute("columnName");
        }
    }
    private class Table {
        public String key;
        public String table;
        public String parentTable;
        public String relationType;
        public String masterColumnName;
        public String relationColumnName;
        public boolean multiLanguage;
        public Map<String, MetadataColumn> columns;
        public List<Filter> filters = new LinkedList<>();
        public String filterLogic;

        public Table(IMetadataTableService metadataTableService, String tableName) {
            MetadataTable metadataTable = metadataTableService.queryTable(tableName);
            this.table = metadataTable.getTableName();
            this.columns = metadataTable.getColumns().stream().collect(Collectors.toMap(MetadataColumn::getColumnName, c -> c));
            this.multiLanguage = Boolean.TRUE.equals(metadataTable.getMultiLanguage());
            this.key = metadataTable.getPrimaryColumns().iterator().next();
        }

        public Table(IMetadataTableService metadataTableService, Element e) {
            this.table = e.getAttribute("table");
            this.parentTable = e.getAttribute("parentTable");
            this.relationType = e.getAttribute("join");
            this.masterColumnName = e.getAttribute("masterColumnName");
            this.relationColumnName = e.getAttribute("relationColumnName");
            MetadataTable metadataTable = metadataTableService.queryTable(this.table);
            this.columns = metadataTable.getColumns().stream().collect(Collectors.toMap(MetadataColumn::getColumnName, c -> c));
            this.multiLanguage = Boolean.TRUE.equals(metadataTable.getMultiLanguage());
            this.key = metadataTable.getPrimaryColumns().iterator().next();
        }

        public Table setFilters(Element e) {
            NodeList filterNodes = e.getChildNodes();
            if(filterNodes.getLength() != 0){
                for (int i=0; i < filterNodes.getLength(); i++){
                    Node fieldNode = filterNodes.item(i);
                    if(fieldNode instanceof Element && ((Element) fieldNode).getTagName().equals("filter")){
                        filters.add(new Filter((Element)fieldNode, this.table));
                    }
                }
            }
            filterLogic = e.getAttribute("filterLogic");
            if (filterLogic.isEmpty()){
                StringBuilder logic = new StringBuilder("1");
                for(int i = 1; i < filters.size(); i++){
                    logic.append(" AND ");
                    logic.append(i);
                }
                filterLogic = logic.toString();
            }
            return this;
        }
    }
    public DocumentSelectAction(String name, Element e, String key, String tableName, Set<String> tlColumns, Configuration configuration, IMetadataTableService metadataTableService){
        setPrefilter(e.getAttribute("prefilter").isEmpty()?null:e.getAttribute("prefilter"));
        setPostfilter(e.getAttribute("postfilter").isEmpty()?null:e.getAttribute("postfilter"));
        this.statement = "dataset." + name + ".select";
        String columnsString = e.getAttribute("columns");
        this.sortColumn = e.getAttribute("sort");
        this.desc = e.getAttribute("desc").equals("true");
        fields.put("objectVersionNumber", new Field("objectVersionNumber", tableName, false, false));
        fields.put(key, new Field(key, tableName, false, false));
        List<Field> queryFields = new ArrayList<>();
        Map<String, Table> tables = new LinkedHashMap<>(); //这个map遍历顺序与put顺序一致，保证主表第一个遍历
        tables.put(tableName, new Table(metadataTableService, tableName).setFilters(e));
        if(columnsString.isEmpty()){
            Node fieldsNode = e.getElementsByTagName("fields").item(0);
            if(fieldsNode instanceof Element){
                NodeList fieldNodes = ((Element) fieldsNode).getElementsByTagName("field");
                for (int i=0; i < fieldNodes.getLength(); i++){
                    Node fieldNode = fieldNodes.item(i);
                    if(fieldNode instanceof Element){
                        fields.put(((Element) fieldNode).getAttribute("name"),new Field((Element)fieldNode));
                    }
                }
            }
            Node tablesNode = e.getElementsByTagName("tables").item(0);
            if(tablesNode instanceof Element){
                NodeList tableNodes = ((Element) tablesNode).getElementsByTagName("table");
                for (int i=0; i < tableNodes.getLength(); i++){
                    Node tableNode = tableNodes.item(i);
                    if(tableNode instanceof Element){
                        Table table = new Table(metadataTableService, (Element)tableNode).setFilters((Element)tableNode);
                        tables.put(table.table, table);
                    }
                }
            }
            Node queryFieldsNode = e.getElementsByTagName("queryFields").item(0);
            if(queryFieldsNode instanceof Element){
                NodeList fieldNodes = ((Element) queryFieldsNode).getElementsByTagName("field");
                for (int i=0; i < fieldNodes.getLength(); i++){
                    Node fieldNode = fieldNodes.item(i);
                    if(fieldNode instanceof Element){
                        queryFields.add(new Field((Element)fieldNode));
                    }
                }
            }
        } else {
            for(String column: columnsString.split(",")){
                fields.put(column, new Field(column, tableName, column.equals(sortColumn), desc));
            }
            queryFields.addAll(fields.values());
        }
        Assert.isTrue(!fields.isEmpty(), "columns or fields is required.");
        StringBuilder xml = new StringBuilder();
        xml.append("<script>");
        xml.append("<bind name=\"__locale_lang_bind\" value=\"@com.hand.hap.mybatis.util.OGNL@locale()\" />");
        xml.append("SELECT ");
        xml.append("<trim suffixOverrides=\",\">");
        for (Field field : fields.values()) {
            MetadataColumn column = tables.get(field.table).columns.get(field.columnName);
            if (column == null){
                //跳过表中不存在的列
                continue;
            }
            if(Boolean.TRUE.equals(column.getMultiLanguage())){
                xml.append(field.table.substring(0, field.table.length() - 2 ));
                xml.append("_tl");
            } else {
                xml.append(field.table);
            }
            xml.append('.');
            xml.append(field.columnName);
            xml.append(" AS ");
            xml.append(field.name);
            xml.append(',');
        }
        xml.append("</trim> FROM ");
        for (Table table: tables.values()){
            if(table.parentTable != null){
                if(table.relationType.equals("left")){
                    xml.append(" LEFT JOIN ");
                } else {
                    xml.append(" INNER JOIN ");
                }
                xml.append(table.table);
                xml.append(" ON (");
                xml.append(table.parentTable);
                xml.append('.');
                xml.append(table.masterColumnName);
                xml.append('=');
                xml.append(table.table);
                xml.append('.');
                xml.append(table.relationColumnName);
                if(!table.filters.isEmpty()){
                    xml.append(" AND (");
                    xml.append(buildFilterLogic(table.filterLogic, table.filters));
                    xml.append(')');
                }
                xml.append(')');
            } else {
                xml.append(table.table);
            }
            if (table.multiLanguage){
                xml.append(" LEFT JOIN ");
                xml.append(table.table.substring(0, table.table.length() - 2 ));
                xml.append("_tl ON (");
                xml.append(table.table);
                xml.append('.');
                xml.append(table.key);
                xml.append('=');
                xml.append(table.table.substring(0, table.table.length() - 2 ));
                xml.append("_tl.");
                xml.append(table.key);
                xml.append(" AND ");
                xml.append(table.table.substring(0, table.table.length() - 2 ));
                xml.append("_tl.LANG=#{__locale_lang_bind})");
            }
        }
        xml.append("<trim prefix=\" WHERE \" suffixOverrides=\"AND\">");
        for (Field field : queryFields) {
            MetadataColumn column = tables.get(field.table).columns.get(field.columnName);
            if (column == null){
                //跳过表中不存在的列
                continue;
            }
            xml.append("<if test=\"");
            xml.append(field.name);
            xml.append(" != null \">");
            if(Boolean.TRUE.equals(column.getMultiLanguage())){
                xml.append(field.table.substring(0, field.table.length() - 2 ));
                xml.append("_tl");
            } else {
                xml.append(field.table);
            }
            xml.append('.');
            xml.append(field.columnName);
            switch (field.operation) {
                case "contains":
                    xml.append(" like ");
                    xml.append("concat('%',concat(#{");
                    xml.append(field.name);
                    xml.append(",jdbcType=VARCHAR},'%'))");
                    break;
                case "startsWith":
                    xml.append(" like ");
                    xml.append("concat(#{");
                    xml.append(field.name);
                    xml.append(",jdbcType=VARCHAR},'%')");
                    break;
                default:
                    xml.append("=");
                    xml.append("#{");
                    xml.append(field.name);
                    xml.append('}');
                    break;
            }
            xml.append(" AND ");
            xml.append("</if>");
        }
        if(!tables.get(tableName).filters.isEmpty()){
            xml.append('(');
            xml.append(buildFilterLogic(tables.get(tableName).filterLogic, tables.get(tableName).filters));
            xml.append(") AND ");
        }
        xml.append("</trim>");

        xml.append("</script>");
        MappedStatement.Builder builder = new MappedStatement.Builder(configuration, statement,
                languageDriver.createSqlSource(configuration, xml.toString(), null), SqlCommandType.SELECT);
        builder.resultMaps(Collections.singletonList(new ResultMap.Builder(configuration, statement, Map.class, Collections.emptyList()).build()));
        configuration.addMappedStatement(builder.build());
    }

    private String buildFilterLogic(String exp, List<Filter> filters){
        for(int i = 0; i < filters.size(); i++){
            exp = exp.replace(String.valueOf(i + 1), buildFilter(filters.get(i)));
        }
        return exp;
    }

    private String buildFilter(Filter filter){
        StringBuilder xml = new StringBuilder();
        xml.append(filter.table);
        xml.append('.');
        xml.append(filter.columnName);
        if (filter.type.equals("column")){
            switch (filter.operation) {
                default:
                    xml.append('=');
                    xml.append(filter.valueTable);
                    xml.append('.');
                    xml.append(filter.value);
                    break;
            }
        } else {
            switch (filter.operation) {
                case "contains":
                    xml.append(" like '%");
                    xml.append(filter.value);
                    xml.append("%'");
                    break;
                case "startsWith":
                    xml.append(" like '");
                    xml.append(filter.value);
                    xml.append("%'");
                    break;
                default:
                    xml.append("=");
                    xml.append("'");
                    xml.append(filter.value);
                    xml.append("'");
                    break;
            }
        }
        return xml.toString();
    }

    @Override
    public Object invoke(SqlSession session, Map<String, Object> parameter) {
        String sort = (String) parameter.get("__sort");
        Boolean desc = parameter.get("__order") == null  ? null : "DESC".equals(parameter.get("__order"));
        if(sort == null){
            sort = sortColumn;
        }
        Field sortField = fields.get(sort);
        if(desc == null){
            desc = this.desc;
        }
        if(sortField != null) {
            PageHelper.orderBy(sortField.table + '.' + sortField.columnName + " " + (desc ? "DESC": "ASC"));
        }
        return session.selectList(statement, parameter);
    }

}