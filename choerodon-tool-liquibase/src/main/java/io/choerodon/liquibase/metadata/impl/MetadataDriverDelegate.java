package io.choerodon.liquibase.metadata.impl;

import io.choerodon.liquibase.metadata.IMetadataDriver;
import io.choerodon.liquibase.metadata.dto.MetadataColumn;
import io.choerodon.liquibase.metadata.dto.MetadataTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;

/**
 * 负责代理具体数据库DDL驱动实现类以及同步元数据表数据,封装一定多语言操作
 */
@Service
public class MetadataDriverDelegate implements IMetadataDriver, InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(MetadataDriverDelegate.class);

    private IMetadataDriver delegate;

    @Autowired
    private DataSource source;

    @Override
    public Map<String, MetadataTable> selectTables() throws SQLException {
        return delegate.selectTables();
    }

    private static IMetadataDriver getDelegate(DataSource source) throws SQLException {
        IMetadataDriver delegate;
        try (Connection connect = source.getConnection()) {
            String database = connect.getMetaData().getDatabaseProductName();
            switch (database) {
                case "MySQL":
                    delegate = new MysqlMetadataDriver(source);
                    break;
                case "Microsoft SQL Server":
                    delegate = new MSSQLMetadataDriver(source);
                    break;
                default:
                    delegate = new BaseMetadataDriver(source);
                    break;
            }
        }
        return delegate;
    }

    public static void syncMetadata(DataSource source) throws SQLException {
        LOGGER.info("sync metadata start.");
        IMetadataDriver delegate = getDelegate(source);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(source);
        Map<String, Set<String>> tableColumns = new TreeMap<>();
        jdbcTemplate.query("SELECT TABLE_NAME, COLUMN_NAME FROM metadata_column_b", new RowMapper<Object>() {
            @Override
            public Object mapRow(ResultSet resultSet, int i) throws SQLException {
                String tableName = resultSet.getString("TABLE_NAME");
                String columnName = resultSet.getString("COLUMN_NAME");
                Set<String> columns = tableColumns.computeIfAbsent(tableName, k -> new TreeSet<>());
                columns.add(columnName);
                return null;
            }
        });
        Map<String, MetadataTable> tables = delegate.selectTables();
        try (Connection connection = source.getConnection();
             PreparedStatement insertTablePS = connection.prepareStatement("INSERT INTO metadata_table_b(ID,TABLE_NAME,DESCRIPTION,MULTI_LANGUAGE) VALUES (?,?,?,?)");
             PreparedStatement insertTableTLPS = connection.prepareStatement("INSERT INTO metadata_table_tl(ID,LANG,DESCRIPTION) VALUES (?,'zh_CN',?)")) {
            for (MetadataTable table : tables.values()) {
                if (table.getTableName().endsWith("_tl")) {
                    continue;
                }
                if (!tableColumns.containsKey(table.getTableName())) {
                    String id = UUID.randomUUID().toString().replace("-", "");
                    insertTablePS.setString(1, id);
                    insertTablePS.setString(2, table.getTableName());
                    insertTablePS.setString(3, table.getDescription());
                    insertTablePS.setBoolean(4, table.getMultiLanguage());
                    insertTablePS.addBatch();
                    insertTableTLPS.setString(1, id);
                    insertTableTLPS.setString(2, table.getDescription());
                    insertTableTLPS.addBatch();
                }
            }
            insertTablePS.executeBatch();
            insertTableTLPS.executeBatch();
        }
        try (Connection connection = source.getConnection();
             PreparedStatement insertColumnPS = connection.prepareStatement("INSERT INTO metadata_column_b(ID,TABLE_NAME,COLUMN_NAME,TYPE_NAME,COLUMN_SIZE,PRIMARY_KEY,MULTI_LANGUAGE,NULLABLE,DESCRIPTION) VALUES (?,?,?,?,?,?,?,?,?)");
             PreparedStatement insertColumnTLPS = connection.prepareStatement("INSERT INTO metadata_column_tl(ID,LANG,DESCRIPTION) VALUES (?, 'zh_CN', ?)")) {
            for (MetadataTable table : tables.values()) {
                if (table.getTableName().endsWith("_tl")) {
                    continue;
                }
                for (MetadataColumn column : table.getColumns()) {
                    if (!tableColumns.containsKey(column.getTableName()) || !tableColumns.get(column.getTableName()).contains(column.getColumnName())) {
                        String id = UUID.randomUUID().toString().replace("-", "");
                        insertColumnPS.setString(1, id);
                        insertColumnPS.setString(2, column.getTableName());
                        insertColumnPS.setString(3, column.getColumnName());
                        insertColumnPS.setString(4, column.getTypeName());
                        insertColumnPS.setLong(5, column.getColumnSize());
                        column.setPrimaryKey(table.getPrimaryColumns().contains(column.getColumnName()));
                        insertColumnPS.setBoolean(6, column.getPrimaryKey());
                        insertColumnPS.setBoolean(7, column.getMultiLanguage());
                        insertColumnPS.setBoolean(8, column.getNullable());
                        insertColumnPS.setString(9, column.getDescription());
                        insertColumnPS.addBatch();
                        insertColumnTLPS.setString(1, id);
                        insertColumnTLPS.setString(2, column.getDescription());
                        insertColumnTLPS.addBatch();
                    }
                }
            }
            insertColumnPS.executeBatch();
            insertColumnTLPS.executeBatch();
        }
        LOGGER.info("sync metadata done.");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        delegate = getDelegate(source);
    }
}
