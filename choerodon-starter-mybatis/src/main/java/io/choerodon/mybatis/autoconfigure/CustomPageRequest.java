package io.choerodon.mybatis.autoconfigure;


import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.lang.Nullable;

public class CustomPageRequest extends CustomAbstractPageRequest {

    private static final long serialVersionUID = -4541509938956089562L;

    private final Sort sort;

    @Deprecated
    public CustomPageRequest(int page, int size) {
        this(page, size, Sort.unsorted());
    }

    @Deprecated
    public CustomPageRequest(int page, int size, Direction direction, String... properties) {
        this(page, size, Sort.by(direction, properties));
    }
    @Deprecated
    public CustomPageRequest(int page, int size, Sort sort) {

        super(page, size);

        this.sort = sort;
    }
    public static CustomPageRequest of(int page, int size) {
        return of(page, size, Sort.unsorted());
    }
    public static CustomPageRequest of(int page, int size, Sort sort) {
        return new CustomPageRequest(page, size, sort);
    }
    public static CustomPageRequest of(int page, int size, Direction direction, String... properties) {
        return of(page, size, Sort.by(direction, properties));
    }
    public Sort getSort() {
        return sort;
    }

    public Pageable next() {
        return new CustomPageRequest(getPageNumber() + 1, getPageSize(), getSort());
    }

    public CustomPageRequest previous() {
        return getPageNumber() == 0 ? this : new CustomPageRequest(getPageNumber() - 1, getPageSize(), getSort());
    }
    public Pageable first() {
        return new CustomPageRequest(0, getPageSize(), getSort());
    }

    @Override
    public boolean equals(@Nullable Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof CustomPageRequest)) {
            return false;
        }

        CustomPageRequest that = (CustomPageRequest) obj;

        return super.equals(that) && this.sort.equals(that.sort);
    }
    @Override
    public int hashCode() {
        return 31 * super.hashCode() + sort.hashCode();
    }

    @Override
    public String toString() {
        return String.format("Page request [number: %d, size %d, sort: %s]", getPageNumber(), getPageSize(), sort);
    }
}
