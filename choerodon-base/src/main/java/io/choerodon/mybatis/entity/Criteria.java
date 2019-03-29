package io.choerodon.mybatis.entity;


import io.choerodon.base.entity.BaseEntity;
import io.choerodon.mybatis.common.query.SQLField;
import io.choerodon.mybatis.common.query.Selection;
import io.choerodon.mybatis.common.query.SortField;
import io.choerodon.mybatis.common.query.SortType;
import io.choerodon.mybatis.common.query.WhereField;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author njq.niu@hand-china.com
 */
public class Criteria {

    private Set<Selection> selectFields;

    private Set<SortField> sortFields;

    private Set<WhereField> whereFields;

    private Set<String> excludeSelectFields;

    private Set<String> updateFields;

    public Criteria() {
    }

    public Criteria(Object obj) {
        if (obj instanceof BaseEntity) {
            BaseEntity dto = (BaseEntity) obj;
            if (dto.getSortname() != null) {
                sort(dto.getSortname(), dto.getSortorder() != null ? SortType.valueOf(dto.getSortorder().toUpperCase()) : SortType.ASC);
            }
        }
    }

    public Criteria select(String... fields) {
        excludeSelectFields = null;
        if (selectFields == null) selectFields = new HashSet<>(50);
        if (fields.length > 0) {
            selectFields.add(new Selection(BaseEntity.FIELD_OBJECT_VERSION_NUMBER));
        }
        for (String field : fields) {
            if (!containField(selectFields, field))
                selectFields.add(new Selection(field));
        }
        return this;
    }

    /**
     * 查询扩展字段.
     *
     * @return Criteria
     */
    public Criteria selectExtensionAttribute() {
        excludeSelectFields = null;
        if (selectFields == null) selectFields = new HashSet<>(50);
        selectFields.addAll(Arrays.asList(new Selection(BaseEntity.FIELD_ATTRIBUTE1), new Selection(BaseEntity.FIELD_ATTRIBUTE2), new Selection(BaseEntity.FIELD_ATTRIBUTE3),
                new Selection(BaseEntity.FIELD_ATTRIBUTE4), new Selection(BaseEntity.FIELD_ATTRIBUTE5), new Selection(BaseEntity.FIELD_ATTRIBUTE6),
                new Selection(BaseEntity.FIELD_ATTRIBUTE7), new Selection(BaseEntity.FIELD_ATTRIBUTE8), new Selection(BaseEntity.FIELD_ATTRIBUTE9),
                new Selection(BaseEntity.FIELD_ATTRIBUTE10), new Selection(BaseEntity.FIELD_ATTRIBUTE11), new Selection(BaseEntity.FIELD_ATTRIBUTE12), new Selection(BaseEntity.FIELD_ATTRIBUTE13),
                new Selection(BaseEntity.FIELD_ATTRIBUTE14), new Selection(BaseEntity.FIELD_ATTRIBUTE15), new Selection(BaseEntity.FIELD_ATTRIBUTE_CATEGORY)));
        return this;
    }

    public Criteria unSelect(String... fields) {
        selectFields = null;
        if (excludeSelectFields == null) excludeSelectFields = new HashSet<>(50);
        for (String field : fields) {
            if (!excludeSelectFields.contains(field))
                excludeSelectFields.add(field);
        }
        return this;
    }

    public Criteria where(Object... fields) {
        for (Object field : fields) {
            if (field instanceof WhereField) {
                where((WhereField) field);
            } else if (field instanceof String) {
                where((String) field);
            }
        }
        return this;
    }

    public Criteria where(WhereField... fields) {
        if (whereFields == null) whereFields = new HashSet<>(15);
        Collections.addAll(whereFields, fields);
        return this;
    }

    public Criteria where(String... fields) {
        if (whereFields == null) whereFields = new HashSet<>(15);
        for (String field : fields) {
            whereFields.add(new WhereField(field));
        }
        return this;
    }

    public void update(String... fields) {
        if (updateFields == null) {
            updateFields = new HashSet<>(50);
        }
        if (fields.length > 0 && !updateFields.contains(BaseEntity.FIELD_LAST_UPDATE_DATE)) {
            updateFields.addAll(Arrays.asList(BaseEntity.FIELD_LAST_UPDATE_DATE, BaseEntity.FIELD_LAST_UPDATED_BY));
        }
        Collections.addAll(updateFields, fields);
    }


    /**
     * 更新扩展字段.
     */
    public void updateExtensionAttribute() {
        if (updateFields == null) {
            updateFields = new HashSet<>(50);
        }
        updateFields.addAll(Arrays.asList(BaseEntity.FIELD_ATTRIBUTE1, BaseEntity.FIELD_ATTRIBUTE2, BaseEntity.FIELD_ATTRIBUTE3, BaseEntity.FIELD_ATTRIBUTE4,
                BaseEntity.FIELD_ATTRIBUTE5, BaseEntity.FIELD_ATTRIBUTE6, BaseEntity.FIELD_ATTRIBUTE7, BaseEntity.FIELD_ATTRIBUTE8, BaseEntity.FIELD_ATTRIBUTE9,
                BaseEntity.FIELD_ATTRIBUTE10, BaseEntity.FIELD_ATTRIBUTE11, BaseEntity.FIELD_ATTRIBUTE12, BaseEntity.FIELD_ATTRIBUTE13, BaseEntity.FIELD_ATTRIBUTE14,
                BaseEntity.FIELD_ATTRIBUTE15, BaseEntity.FIELD_ATTRIBUTE_CATEGORY));
    }

    private void sort(String field, SortType sortType) {
        if (sortFields == null) sortFields = new HashSet<>();
        if (!containField(sortFields, field))
            sortFields.add(new SortField(field, sortType));
    }

    private boolean containField(Set<? extends SQLField> list, String field) {
        boolean found = false;
        for (SQLField sqlField : list) {
            if (sqlField.getField().equals(field)) {
                found = true;
                break;
            }
        }
        return found;
    }

    public Set<String> getUpdateFields() {
        return updateFields;
    }

    public Set<Selection> getSelectFields() {
        return selectFields;
    }

    public void setSelectFields(Set<Selection> selectFields) {
        this.selectFields = selectFields;
    }

    public Set<SortField> getSortFields() {
        return sortFields;
    }

    public void setSortFields(Set<SortField> sortFields) {
        this.sortFields = sortFields;
    }

    public Set<WhereField> getWhereFields() {
        return whereFields;
    }

    public void setWhereFields(Set<WhereField> whereFields) {
        this.whereFields = whereFields;
    }

    public Set<String> getExcludeSelectFields() {
        return excludeSelectFields;
    }

    public void setExcludeSelectFields(Set<String> excludeSelectFields) {
        this.excludeSelectFields = excludeSelectFields;
    }
}
