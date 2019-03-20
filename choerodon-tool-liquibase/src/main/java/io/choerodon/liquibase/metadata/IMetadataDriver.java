package io.choerodon.liquibase.metadata;

import io.choerodon.liquibase.metadata.dto.MetadataTable;

import java.sql.SQLException;
import java.util.Map;

public interface IMetadataDriver {
    Map<String, MetadataTable> selectTables() throws SQLException;
}
