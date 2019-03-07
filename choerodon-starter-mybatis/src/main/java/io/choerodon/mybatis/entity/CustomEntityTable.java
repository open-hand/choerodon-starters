package io.choerodon.mybatis.entity;

import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.entity.EntityTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CustomEntityTable extends EntityTable {
    private boolean multiLanguage = false;
    private LinkedHashSet<EntityColumn> multiLanguageColumns;

    // 全部可查询列
    private Set<CustomEntityColumn> allColumns = new LinkedHashSet<>();

    // 别名映射
    private Map<String, String> aliasMapping = new HashMap<>();
    // Join映射
    private Map<String, CustomEntityColumn> joinMapping = new HashMap<>();
    // Where列
    private List<EntityColumn> whereColumns = new ArrayList<>();
    // 可排序列
    private Set<EntityColumn> sortColumns = new LinkedHashSet<>();

    // 别名初始值
    private static final char ALIAS_START = 'A';
    // 别名序号
    private int currentAliasCharIndex = 0;

    public CustomEntityTable(Class<?> entityClass) {
        super(entityClass);
        propertyMap = new HashMap<>();
        createAlias(entityClass.getCanonicalName());
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

    public void createAlias(String key){
        if(!aliasMapping.containsKey(key)) {
            aliasMapping.put(key, generateAlias());
        }
    }
    private String generateAlias() {
        return (String.valueOf((char) (ALIAS_START + currentAliasCharIndex++)));
    }

    public Map<String, String> getAliasMapping() {
        return aliasMapping;
    }

    public void setAliasMapping(Map<String, String> aliasMapping) {
        this.aliasMapping = aliasMapping;
    }

    public Map<String, CustomEntityColumn> getJoinMapping() {
        return joinMapping;
    }

    public void setJoinMapping(Map<String, CustomEntityColumn> joinMapping) {
        this.joinMapping = joinMapping;
    }

    public String getAlias(String key){
        return key == null ? aliasMapping.get(getEntityClass().getCanonicalName()) : aliasMapping.get(key);
    }

    public String getAlias(){
        return getAlias(null);
    }

    public List<EntityColumn> getWhereColumns() {
        return whereColumns;
    }

    public void setWhereColumns(List<EntityColumn> whereColumns) {
        this.whereColumns = whereColumns;
    }

    public Set<EntityColumn> getSortColumns() {
        return sortColumns;
    }

    public void setSortColumns(Set<EntityColumn> sortColumns) {
        this.sortColumns = sortColumns;
    }

    public int getCurrentAliasCharIndex() {
        return currentAliasCharIndex;
    }

    public void setCurrentAliasCharIndex(int currentAliasCharIndex) {
        this.currentAliasCharIndex = currentAliasCharIndex;
    }

    public Set<CustomEntityColumn> getAllColumns() {
        return allColumns;
    }

    public void setAllColumns(Set<CustomEntityColumn> allColumns) {
        this.allColumns = allColumns;
    }

    public CustomEntityColumn findColumnByProperty(String property){
        CustomEntityColumn entityColumn = null;
        for (CustomEntityColumn column : getAllColumns()) {
            if (column.getProperty().equals(property)) {
                entityColumn = column;
                break;
            }
        }
        return entityColumn;
    }
}
