package io.choerodon.actuator.dataset.domain;

public class ActionJoinTable {
    private ActionTable joinTable;
    private String masterColumn;
    private String joinColumn;

    public ActionTable getJoinTable() {
        return joinTable;
    }

    public void setJoinTable(ActionTable joinTable) {
        this.joinTable = joinTable;
    }

    public String getMasterColumn() {
        return masterColumn;
    }

    public void setMasterColumn(String masterColumn) {
        this.masterColumn = masterColumn;
    }

    public String getJoinColumn() {
        return joinColumn;
    }

    public void setJoinColumn(String joinColumn) {
        this.joinColumn = joinColumn;
    }
}
