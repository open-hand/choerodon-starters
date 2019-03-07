/**
 * Copyright 2016 www.extdo.com 
 */
package io.choerodon.mybatis.common.query;

/**
 * @author njq.niu@hand-china.com
 */
public class SQLField {

    private String field;
    
    public SQLField(String field){
        setField(field);
    }
    

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SQLField sqlField = (SQLField) o;

        return field != null ? field.equals(sqlField.field) : sqlField.field == null;
    }

    @Override
    public int hashCode() {
        return field != null ? field.hashCode() : 0;
    }
}
