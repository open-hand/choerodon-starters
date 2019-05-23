package io.choerodon.actuator.dataset.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DatabasePageAction {
    private ActionTable masterActionTable = new ActionTable();
    private Set<ActionProperty> properties = new HashSet<>();
    private Set<ActionJoinTable> joinTables = new HashSet<>();
    private Long page;
    private Long pageSize;
    private Long count;
    private List<Map<String, Object>> result;

    public DatabasePageAction() {
    }

    public DatabasePageAction(DatabasePageAction action) {
        this.properties = action.properties;
        this.masterActionTable = action.masterActionTable;
        this.joinTables = action.joinTables;
        this.page = action.page;
        this.pageSize = action.pageSize;
        this.result = action.result;
        this.count = action.count;
    }

    public ActionTable getMasterActionTable() {
        return masterActionTable;
    }

    public void setMasterActionTable(ActionTable masterActionTable) {
        this.masterActionTable = masterActionTable;
    }

    public Set<ActionProperty> getProperties() {
        return properties;
    }

    public void setProperties(Set<ActionProperty> properties) {
        this.properties = properties;
    }

    public Long getPage() {
        return page;
    }

    public void setPage(Long page) {
        this.page = page;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public List<Map<String, Object>> getResult() {
        return result;
    }

    public void setResult(List<Map<String, Object>> result) {
        this.result = result;
    }

    public Set<ActionJoinTable> getJoinTables() {
        return joinTables;
    }

    public void setJoinTables(Set<ActionJoinTable> joinTables) {
        this.joinTables = joinTables;
    }
}
