package io.choerodon.actuator.metadata.dto;

public class MetadataColumn {
    private Long id;
    private String tableName;
    private String columnName;
    private String description;
    private String typeName;
    private Integer columnSize;
    private Boolean primaryKey;
    private Boolean multiLanguage;
    private Boolean nullable;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
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

    public Integer getColumnSize() {
        return columnSize;
    }

    public void setColumnSize(Integer columnSize) {
        this.columnSize = columnSize;
    }

    public Boolean getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(Boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public Boolean getMultiLanguage() {
        return multiLanguage;
    }

    public void setMultiLanguage(Boolean multiLanguage) {
        this.multiLanguage = multiLanguage;
    }

    public Boolean getNullable() {
        return nullable;
    }

    public void setNullable(Boolean nullable) {
        this.nullable = nullable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MetadataColumn column = (MetadataColumn) o;

        if (columnName != null ? !columnName.equals(column.columnName) : column.columnName != null) return false;
        if (typeName != null ? !typeName.equals(column.typeName) : column.typeName != null) return false;
        if (columnSize != null ? !columnSize.equals(column.columnSize) : column.columnSize != null) return false;
        if (primaryKey != null ? !primaryKey.equals(column.primaryKey) : column.primaryKey != null) return false;
        if (multiLanguage != null ? !multiLanguage.equals(column.multiLanguage) : column.multiLanguage != null)
            return false;
        return nullable != null ? nullable.equals(column.nullable) : column.nullable == null;
    }

    @Override
    public int hashCode() {
        int result = columnName != null ? columnName.hashCode() : 0;
        result = 31 * result + (typeName != null ? typeName.hashCode() : 0);
        result = 31 * result + (columnSize != null ? columnSize.hashCode() : 0);
        result = 31 * result + (primaryKey != null ? primaryKey.hashCode() : 0);
        result = 31 * result + (multiLanguage != null ? multiLanguage.hashCode() : 0);
        result = 31 * result + (nullable != null ? nullable.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s[%s(%d)]", columnName, typeName, columnSize);
    }
}
