package io.choerodon.base.domain;

/**
 * source : org.springframework.data.domain.PageRequest
 * 分页、排序请求封装对象
 *
 * @author NaccOll
 * 2018/1/30
 **/
public class PageRequest {
    private int page;
    private int size;
    private Sort sort;

    public PageRequest() {
    }

    public PageRequest(int page, int size) {
        this(page, size, null);
    }

    /**
     * 构造方法
     *
     * @param page page
     * @param size size
     * @param sort sort
     */
    public PageRequest(int page, int size, Sort sort) {
        this.page = page;
        this.size = size;
        this.sort = sort;
    }

    public PageRequest(int page, int size, Sort.Direction direction, String... properties) {
        this(page, size, new Sort(direction, properties));
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }
}
