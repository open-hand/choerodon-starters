package io.choerodon.actuator.dataset.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DatabaseJoinAction {
    private ActionProperty keyProperty;
    private ActionProperty joinProperty;
    private ActionTable masterActionTable = new ActionTable();
    private Set<ActionProperty> properties = new HashSet<>();

    private List<Object> params;
    private Map<Object, Map<String, Object>> result;

    public DatabaseJoinAction() {
    }

    public DatabaseJoinAction(DatabaseJoinAction action) {
        this.keyProperty = action.keyProperty;
        this.joinProperty = action.joinProperty;
        this.masterActionTable = action.masterActionTable;
        this.properties = action.properties;
        this.params = action.params;
        this.result = action.result;
    }

    public ActionProperty getKeyProperty() {
        return keyProperty;
    }

    public void setKeyProperty(ActionProperty keyProperty) {
        this.keyProperty = keyProperty;
    }

    public ActionProperty getJoinProperty() {
        return joinProperty;
    }

    public void setJoinProperty(ActionProperty joinProperty) {
        this.joinProperty = joinProperty;
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

    public List<Object> getParams() {
        return params;
    }

    public void setParams(List<Object> params) {
        this.params = params;
    }

    public Map<Object, Map<String, Object>> getResult() {
        return result;
    }

    public void setResult(Map<Object, Map<String, Object>> result) {
        this.result = result;
    }
}
