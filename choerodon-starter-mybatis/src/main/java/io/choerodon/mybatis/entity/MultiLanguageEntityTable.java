package io.choerodon.mybatis.entity;

import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.entity.EntityTable;

import java.util.HashMap;
import java.util.LinkedHashSet;

public class MultiLanguageEntityTable extends EntityTable {
    private boolean multiLanguage = false;
    private LinkedHashSet<EntityColumn> multiLanguageColumns;

    public MultiLanguageEntityTable(Class<?> entityClass) {
        super(entityClass);
        propertyMap = new HashMap<>();
    }

    public boolean isMultiLanguage() {
        return multiLanguage;
    }

    public void setMultiLanguage(boolean multiLanguage) {
        this.multiLanguage = multiLanguage;
    }

    public String getMultiLanguageTableName(){
        if(getName().toUpperCase().endsWith("_B")){
            return getName().toUpperCase().substring(0, getName().length() - 2) + "_TL";
        } else {
            return getName().toUpperCase() + "_TL";
        }
    }

    public LinkedHashSet<EntityColumn> getMultiLanguageColumns() {
        return multiLanguageColumns;
    }

    public void setMultiLanguageColumns(LinkedHashSet<EntityColumn> multiLanguageColumns) {
        this.multiLanguageColumns = multiLanguageColumns;
    }

    @Override
    public void initPropertyMap() {
        for (EntityColumn column : getEntityClassColumns()) {
            propertyMap.put(column.getProperty(), column);
        }
    }
}
