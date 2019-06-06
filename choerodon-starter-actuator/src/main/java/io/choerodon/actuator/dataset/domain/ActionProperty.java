package io.choerodon.actuator.dataset.domain;

public class ActionProperty {
    private String name;
    private String column;
    private ActionTable table;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public ActionTable getTable() {
        return table;
    }

    public void setTable(ActionTable table) {
        this.table = table;
    }
}
