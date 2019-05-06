package io.choerodon.actuator.metadata;


import io.choerodon.actuator.metadata.dto.MetadataTable;

import java.sql.SQLException;
import java.util.Map;

public interface IMetadataDriver {
    Map<String, MetadataTable> selectTables() throws SQLException;
}
