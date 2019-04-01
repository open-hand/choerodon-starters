package io.choerodon.dataset.metadata.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.annotation.ExtensionAttribute;
import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@MultiLanguage
@ExtensionAttribute(disable = true)
@Table(name = "metadata_column_b")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MetadataColumn extends BaseDTO {
    @Id
    private String id;
    private String tableName;
    private String columnName;
    @MultiLanguageField
    private String description;
    private String typeName;
    private Integer columnSize;
    private Boolean primaryKey;
    private Boolean multiLanguage;
    private Boolean nullable;

    @Transient
    private String displayType;

    public String getDisplayType() {
        return displayType;
    }

    public void setDisplayType(String displayType) {
        this.displayType = displayType;
    }

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public void solveDisplayType(){
        if("CHAR".equals(getTypeName()) || "VARCHAR".equals(getTypeName())){
            setDisplayType(String.format("%s(%d)", getTypeName(), getColumnSize()));
        } else {
            setDisplayType(getTypeName());
        }
    }
}
