package io.choerodon.actuator.metadata.dto;

import java.util.List;
import java.util.Set;

public class MetadataTable {
    private Long id;

    private String tableName;

    private String description;

    private Boolean multiLanguage;

    private String schema;

    private String lockedBy;

    private Set<String> primaryColumns;

    private List<MetadataColumn> columns;

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getLockedBy() {
        return lockedBy;
    }

    public void setLockedBy(String lockedBy) {
        this.lockedBy = lockedBy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Set<String> getPrimaryColumns() {
        return primaryColumns;
    }

    public void setPrimaryColumns(Set<String> primaryColumns) {
        this.primaryColumns = primaryColumns;
    }

    public List<MetadataColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<MetadataColumn> columns) {
        this.columns = columns;
    }

    public MetadataColumn getColumn(String name){
        for(MetadataColumn column: getColumns()){
            if(column.getColumnName().equals(name)){
                return column;
            }
        }
        return null;
    }

    public Boolean getMultiLanguage() {
        return multiLanguage;
    }

    public void setMultiLanguage(Boolean multiLanguage) {
        this.multiLanguage = multiLanguage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MetadataTable table = (MetadataTable) o;

        if (id != null ? !id.equals(table.id) : table.id != null) return false;
        return tableName != null ? tableName.equals(table.tableName) : table.tableName == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (tableName != null ? tableName.hashCode() : 0);
        return result;
    }
}
