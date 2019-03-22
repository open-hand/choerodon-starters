package io.choerodon.mybatis.entity;


import io.choerodon.mybatis.common.query.SQLField;
import io.choerodon.mybatis.common.query.Selection;
import io.choerodon.mybatis.common.query.SortField;
import io.choerodon.mybatis.common.query.SortType;
import io.choerodon.mybatis.common.query.WhereField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author njq.niu@hand-china.com
 */
public class Criteria {

    private List<Selection> selectFields;

    private List<SortField> sortFields;

    private List<WhereField> whereFields;

    private List<String> excludeSelectFields;

    private List<String> updateFields;

    public Criteria() {
    }

    public Criteria(Object obj) {
        if(obj instanceof BaseDTO) {
            BaseDTO dto = (BaseDTO)obj;
            if (dto != null && dto.getSortname() != null) {
                sort(dto.getSortname(), dto.getSortorder() != null ? SortType.valueOf(dto.getSortorder().toUpperCase()) : SortType.ASC);
            }
        }
    }

    public Criteria sort(String field, SortType sortType) {
        if (sortFields == null) sortFields = new ArrayList<>();
        if (!containField(sortFields, field))
            sortFields.add(new SortField(field, sortType));
        return this;
    }

    public Criteria select(String... fields) {
        excludeSelectFields = null;
        if (selectFields == null) selectFields = new ArrayList<>(50);
        if (fields.length > 0 && !selectFields.contains(BaseDTO.FIELD_OBJECT_VERSION_NUMBER)) {
            selectFields.add(new Selection(BaseDTO.FIELD_OBJECT_VERSION_NUMBER));
        }
        for (String field : fields) {
            if (!containField(selectFields, field))
                selectFields.add(new Selection(field));
        }
        return this;
    }

    /**
     * 查询扩展字段
     * @return
     */
    public Criteria selectExtensionAttribute() {
        excludeSelectFields = null;
        if (selectFields == null) selectFields = new ArrayList<>(50);
        selectFields.addAll(Arrays.asList(new Selection(BaseDTO.FIELD_ATTRIBUTE1),new Selection(BaseDTO.FIELD_ATTRIBUTE2),new Selection(BaseDTO.FIELD_ATTRIBUTE3),
                new Selection(BaseDTO.FIELD_ATTRIBUTE4),new Selection(BaseDTO.FIELD_ATTRIBUTE5),new Selection(BaseDTO.FIELD_ATTRIBUTE6),
                new Selection(BaseDTO.FIELD_ATTRIBUTE7),new Selection(BaseDTO.FIELD_ATTRIBUTE8),new Selection(BaseDTO.FIELD_ATTRIBUTE9),
                new Selection(BaseDTO.FIELD_ATTRIBUTE10),new Selection(BaseDTO.FIELD_ATTRIBUTE11),new Selection(BaseDTO.FIELD_ATTRIBUTE12),new Selection(BaseDTO.FIELD_ATTRIBUTE13),
                new Selection(BaseDTO.FIELD_ATTRIBUTE14),new Selection(BaseDTO.FIELD_ATTRIBUTE15),new Selection(BaseDTO.FIELD_ATTRIBUTE_CATEGORY)));
        return this;
    }

    public Criteria unSelect(String... fields) {
        selectFields = null;
        if (excludeSelectFields == null) excludeSelectFields = new ArrayList<>(50);
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
        if (whereFields == null) whereFields = new ArrayList<>(15);
        for (WhereField field : fields) {
            if (!whereFields.contains(field))
                whereFields.add(field);
        }
        return this;
    }

    public Criteria where(String... fields) {
        if (whereFields == null) whereFields = new ArrayList<>(15);
        for (String field : fields) {
            if (!whereFields.contains(field))
                whereFields.add(new WhereField(field));
        }
        return this;
    }


    private boolean containField(List<? extends SQLField> list, String field) {
        boolean found = false;
        for (SQLField sqlField : list) {
            if (sqlField.getField().equals(field)) {
                found = true;
                break;
            }
        }
        return found;
    }

    public void update(String... fields) {
        if (updateFields == null) {
            updateFields = new ArrayList<>(50);
        }
        if (fields.length > 0 && !updateFields.contains(BaseDTO.FIELD_LAST_UPDATE_DATE)) {
            updateFields.addAll(Arrays.asList(BaseDTO.FIELD_LAST_UPDATE_DATE, BaseDTO.FIELD_LAST_UPDATED_BY));
        }
        for (String field : fields) {
            if (!updateFields.contains(field)) {
                updateFields.add(field);
            }
        }
    }


    /**
     * 更新扩展字段
     */
    public void updateExtensionAttribute(){
        if (updateFields == null) {
            updateFields = new ArrayList<>(50);
        }
        updateFields.addAll(Arrays.asList(BaseDTO.FIELD_ATTRIBUTE1,BaseDTO.FIELD_ATTRIBUTE2,BaseDTO.FIELD_ATTRIBUTE3,BaseDTO.FIELD_ATTRIBUTE4,
                BaseDTO.FIELD_ATTRIBUTE5,BaseDTO.FIELD_ATTRIBUTE6,BaseDTO.FIELD_ATTRIBUTE7,BaseDTO.FIELD_ATTRIBUTE8,BaseDTO.FIELD_ATTRIBUTE9,
                BaseDTO.FIELD_ATTRIBUTE10,BaseDTO.FIELD_ATTRIBUTE11,BaseDTO.FIELD_ATTRIBUTE12,BaseDTO.FIELD_ATTRIBUTE13,BaseDTO.FIELD_ATTRIBUTE14,
                BaseDTO.FIELD_ATTRIBUTE15,BaseDTO.FIELD_ATTRIBUTE_CATEGORY));
    }

    public List<String> getUpdateFields() {
        return updateFields;
    }

    public List<Selection> getSelectFields() {
        return selectFields;
    }

    public void setSelectFields(List<Selection> selectFields) {
        this.selectFields = selectFields;
    }

    public List<SortField> getSortFields() {
        return sortFields;
    }

    public void setSortFields(List<SortField> sortFields) {
        this.sortFields = sortFields;
    }

    public List<WhereField> getWhereFields() {
        return whereFields;
    }

    public void setWhereFields(List<WhereField> whereFields) {
        this.whereFields = whereFields;
    }

    public List<String> getExcludeSelectFields() {
        return excludeSelectFields;
    }

    public void setExcludeSelectFields(List<String> excludeSelectFields) {
        this.excludeSelectFields = excludeSelectFields;
    }
}
