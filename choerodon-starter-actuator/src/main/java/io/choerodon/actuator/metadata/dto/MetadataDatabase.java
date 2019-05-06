package io.choerodon.actuator.metadata.dto;

import java.util.Map;

public class MetadataDatabase {
    private String type;
    private String tenantColumn;
    private Map<String, MetadataTable> tables;

    public Map<String, MetadataTable> getTables() {
        return tables;
    }

    public void setTables(Map<String, MetadataTable> tables) {
        this.tables = tables;
    }

    public String getTenantColumn() {
        return tenantColumn;
    }

    public void setTenantColumn(String tenantColumn) {
        this.tenantColumn = tenantColumn;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
