/**
 * Copyright 2016 www.extdo.com 
 */
package io.choerodon.mybatis.common.query;


/**
 * @author njq.niu@hand-china.com
 */
public class SortField extends SQLField {

    private SortType sortType;

    public SortField(String field, SortType SortType) {
        super(field);
        this.sortType = SortType;
    }

    public SortType getSortType() {
        return sortType;
    }
}
