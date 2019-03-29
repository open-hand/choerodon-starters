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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        SortField sortField = (SortField) o;

        return sortType == sortField.sortType;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (sortType != null ? sortType.hashCode() : 0);
        return result;
    }
}
