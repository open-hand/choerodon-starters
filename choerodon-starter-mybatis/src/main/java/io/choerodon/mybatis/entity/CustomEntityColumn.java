package io.choerodon.mybatis.entity;

import io.choerodon.mybatis.common.query.JoinColumn;
import io.choerodon.mybatis.common.query.JoinTable;
import io.choerodon.mybatis.common.query.Where;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.entity.EntityField;
import tk.mybatis.mapper.entity.EntityTable;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CustomEntityColumn extends EntityColumn {
    private boolean multiLanguage = false;
    private List<JoinTable> joinTables;
    private JoinColumn joinColumn;
    private Where where;

    //可查询
    private boolean selectable = true;

    public CustomEntityColumn(EntityTable table) {
        super(table);
    }

    public boolean isMultiLanguage() {
        return multiLanguage;
    }

    public void setMultiLanguage(boolean multiLanguage) {
        this.multiLanguage = multiLanguage;
    }


    public JoinColumn getJoinColumn() {
        return joinColumn;
    }

    public void setJoinColumn(JoinColumn joinColumn) {
        this.joinColumn = joinColumn;
    }

    public List<JoinTable> getJoinTables() {
        return joinTables;
    }

    public void setJoinTables(List<JoinTable> joinTables) {
        this.joinTables = joinTables;
    }

    public Where getWhere() {
        return where;
    }

    public void setWhere(Where where) {
        this.where = where;
    }

    public void addJoinTable(JoinTable joinTable) {
        if(this.joinTables == null){
            this.joinTables = new ArrayList<>();
        }
        this.joinTables.add(joinTable);
    }

    public JoinTable findJoinTableByName(String joinName){
        JoinTable joinTable = null;
        if(this.joinTables!=null && joinName!=null){
            for(JoinTable jt:this.joinTables){
                if(joinName.equalsIgnoreCase(jt.name())) {
                    joinTable = jt;
                    break;
                }
            }
        }
        return joinTable;
    }

    public boolean isSelectable() {
        return selectable;
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }
}
