package io.choerodon.actuator.metadata.impl;

import io.choerodon.actuator.metadata.dto.MetadataColumn;
import io.choerodon.actuator.metadata.dto.MetadataTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Microsoft SQL Server基本的DDL操作驱动类。
 */
public class MSSQLMetadataDriver extends BaseMetadataDriver {
    private static final Logger logger = LoggerFactory.getLogger(MSSQLMetadataDriver.class);

    public MSSQLMetadataDriver(DataSource source) {
        super(source);
    }

    @Override
    public Map<String, MetadataTable> selectTables() throws SQLException {
        Map<String, MetadataTable> result = new TreeMap<>();
        Connection connection = null;
        ResultSet columnsResult = null;
        try {
            connection = source.getConnection();
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            columnsResult = databaseMetaData.getColumns(connection.getCatalog(), connection.getSchema(), null, null);
            Map<String, Map<String, String>> tableColumnDescriptions = getTableColumnDescriptions();
            while (columnsResult.next()) {
                String originTableName = columnsResult.getString("TABLE_NAME");
                String tableName = originTableName.toLowerCase();
                String columnName = columnsResult.getString("COLUMN_NAME").toUpperCase();
                if (isSkip(tableName, columnName)) {
                    continue;
                }
                MetadataTable table = result.get(tableName);
                if (table == null) {
                    table = new MetadataTable();
                    table.setTableName(tableName);
                    table.setMultiLanguage(false);
                    table.setColumns(new LinkedList<>());
                    table.setPrimaryColumns(new TreeSet<>());
                    result.put(tableName, table);
                    ResultSet primaryResult = databaseMetaData.getPrimaryKeys(connection.getCatalog(), connection.getSchema(), originTableName);
                    while (primaryResult.next()) {
                        table.getPrimaryColumns().add(primaryResult.getString("COLUMN_NAME").toUpperCase());
                    }
                    safeClose(primaryResult);
                }
                MetadataColumn column = new MetadataColumn();
                column.setTableName(tableName);
                column.setColumnName(columnName);
                column.setMultiLanguage(false);
                column.setPrimaryKey(false);
                column.setTypeName(columnsResult.getString("TYPE_NAME"));
                column.setColumnSize(columnsResult.getInt("COLUMN_SIZE"));
                column.setNullable(columnsResult.getBoolean("NULLABLE"));
                column.setDescription(getDescriptionByTableNameAndColumnName(tableName, columnName, tableColumnDescriptions));
                table.getColumns().add(column);
            }
        } finally {
            safeClose(columnsResult);
            safeClose(connection);
        }
        //处理多语言列
        for (String tableName : result.keySet()) {
            if (tableName.endsWith("_b")) {
                MetadataTable table = result.get(tableName);
                String tlTableName = tableName.substring(0, tableName.length() - 2) + "_tl";
                MetadataTable tlTable = result.get(tlTableName);
                if (tlTable == null) {
                    logger.warn("Cannot find {} corresponding multi-language table {}," +
                                    " if it is not a multi-language table, recommended to remove the _b suffix.",
                            tableName, tlTableName);
                    continue;
                }
                for (MetadataColumn column : table.getColumns()) {
                    if (MULTI_LANGUAGE_SKIP_COLUMNS.contains(column.getColumnName())) {
                        continue;
                    }
                    MetadataColumn tlColumn = tlTable.getColumn(column.getColumnName());
                    if (tlColumn == null || tlTable.getPrimaryColumns().contains(tlColumn.getColumnName())) {
                        continue;
                    }
                    if (!tlColumn.equals(column)) {
                        logger.warn("The multi-language column format in table {} does not match, expected that {} gets {}.",
                                tlTableName, column, tlColumn);
                    }
                    column.setMultiLanguage(true);
                    table.setMultiLanguage(true);
                }
            }
        }
        return result;
    }

    /**
     * 获取所有表列的备注信息.
     *
     * @return 所有表列的备注信息
     */
    private Map<String, Map<String, String>> getTableColumnDescriptions() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(source);
        Map<String, Map<String, String>> tableColumnDescriptions = new HashMap<>();
        String querySql = String.format("SELECT st.name [Table],sc.name [Column],CONVERT (VARCHAR(200), sep. VALUE) [Description] FROM sys.tables st INNER JOIN sys.columns sc ON st.object_id = sc.object_id LEFT JOIN sys.extended_properties sep ON st.object_id = sep.major_id AND sc.column_id = sep.minor_id AND sep.name = 'MS_Description'");
        jdbcTemplate.query(querySql, (ResultSet resultSet, int i) -> {
            String tableName = resultSet.getString("Table").toLowerCase();
            String columnName = resultSet.getString("Column").toUpperCase();
            String description = resultSet.getString("Description");
            Map<String, String> columns = tableColumnDescriptions.computeIfAbsent(tableName, k -> new HashMap<>());
            columns.put(columnName, description);
            return null;
        });
        return tableColumnDescriptions;
    }

    /**
     * 根据表名和列名获取列的备注信息.
     *
     * @param tableName               表名
     * @param columnName              列名
     * @param tableColumnDescriptions 所有表列的备注信息
     * @return 列的备注信息
     */
    private String getDescriptionByTableNameAndColumnName(String tableName, String columnName, Map<String, Map<String, String>> tableColumnDescriptions) {
        Map<String, String> columnDescriptions = tableColumnDescriptions.get(tableName);
        if (!columnDescriptions.isEmpty()) {
            return columnDescriptions.get(columnName);
        }
        return null;
    }
}
