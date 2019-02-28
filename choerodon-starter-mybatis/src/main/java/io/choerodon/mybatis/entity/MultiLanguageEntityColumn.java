package io.choerodon.mybatis.entity;

import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.entity.EntityField;
import tk.mybatis.mapper.entity.EntityTable;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

public class MultiLanguageEntityColumn extends EntityColumn {
    private boolean multiLanguage = false;

    public MultiLanguageEntityColumn(EntityTable table) {
        super(table);
    }

    public boolean isMultiLanguage() {
        return multiLanguage;
    }

    public void setMultiLanguage(boolean multiLanguage) {
        this.multiLanguage = multiLanguage;
    }
}
