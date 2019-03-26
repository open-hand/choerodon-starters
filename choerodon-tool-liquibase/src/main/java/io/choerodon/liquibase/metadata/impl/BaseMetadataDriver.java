package io.choerodon.liquibase.metadata.impl;

import com.zaxxer.hikari.pool.HikariProxyConnection;
import io.choerodon.liquibase.metadata.IMetadataDriver;
import io.choerodon.liquibase.metadata.dto.MetadataColumn;
import io.choerodon.liquibase.metadata.dto.MetadataTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oracle.jdbc.driver.OracleConnection;
import org.springframework.beans.BeanUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * 基本的DDL操作驱动类，基于Mysql编写，其他数据库继承重写。
 */
public class BaseMetadataDriver implements IMetadataDriver {
    public static final String TABLE_DELETE_RESTORE_PREFIX = "__del_";
    public static final String COLUMN_DELETE_RESTORE_PREFIX = "__DEL_";
    private static final String DB_TYPE_ORACLE = "oracle";
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseMetadataDriver.class);
    public static final Set<String> MULTI_LANGUAGE_SKIP_COLUMNS = new TreeSet<>();

    static {
        MULTI_LANGUAGE_SKIP_COLUMNS.add("OBJECT_VERSION_NUMBER");
        MULTI_LANGUAGE_SKIP_COLUMNS.add("REQUEST_ID");
        MULTI_LANGUAGE_SKIP_COLUMNS.add("PROGRAM_ID");
        MULTI_LANGUAGE_SKIP_COLUMNS.add("CREATED_BY");
        MULTI_LANGUAGE_SKIP_COLUMNS.add("CREATION_DATE");
        MULTI_LANGUAGE_SKIP_COLUMNS.add("LAST_UPDATED_BY");
        MULTI_LANGUAGE_SKIP_COLUMNS.add("LAST_UPDATE_DATE");
        MULTI_LANGUAGE_SKIP_COLUMNS.add("LAST_UPDATE_LOGIN");
        for (int i = 1; i <= 15; i++) {
            MULTI_LANGUAGE_SKIP_COLUMNS.add("ATTRIBUTE" + i);
        }
    }

    protected DataSource source;

    public BaseMetadataDriver(DataSource source) {
        this.source = source;
    }

    protected boolean isSkip(String name, String column) {
        return name.startsWith(TABLE_DELETE_RESTORE_PREFIX) ||
                column.startsWith(COLUMN_DELETE_RESTORE_PREFIX);
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
                column.setDescription(columnsResult.getString("REMARKS"));
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
                    LOGGER.warn("Cannot find {} corresponding multi-language table {}," +
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
                        LOGGER.warn("The multi-language column format in table {} does not match, expected that {} gets {}.",
                                tlTableName, column, tlColumn);
                    }
                    column.setMultiLanguage(true);
                    table.setMultiLanguage(true);
                }
            }
        }
        return result;
    }

    protected void safeClose(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
