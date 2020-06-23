package io.choerodon.core.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 根据page, size参数获取数据库start的行
 */
public class PageUtils {

    private PageUtils() {
    }

    public static int getBegin(int page, int size) {
        // 现在页码是从0开始了
        page = Math.max(page, 0);
        return page * size;
    }

    public static List<String> getPageableSorts(Pageable Pageable) {
        List<String> sorts = new ArrayList<>();
        Iterator<Sort.Order> sortIterator = Pageable.getSort().iterator();
        while (sortIterator.hasNext()) {
            Sort.Order order = sortIterator.next();
            sorts.add(order.getProperty() + "," + order.getDirection());
        }
        return sorts;
    }

    /**
     * 装配Page对象
     *
     * @param all      包含所有内容的列表
     * @param Pageable 分页参数
     * @return PageInfo
     */
    public static <T> Page<T> createPageFromList(List<T> all, Pageable Pageable) {
        Page<T> result = new Page<>();
        boolean queryAll = Pageable.getPageNumber() == 0 || Pageable.getPageSize() == 0;
        result.setSize(queryAll ? all.size() : Pageable.getPageSize());
        result.setNumber(Pageable.getPageNumber());
        result.setTotalElements(all.size());
        result.setTotalPages(queryAll ? 1 : (int) (Math.ceil(all.size() / (Pageable.getPageSize() * 1.0))));
        int fromIndex = Pageable.getPageSize() * (Pageable.getPageNumber() - 1);
        int size;
        if (all.size() >= fromIndex) {
            if (all.size() <= fromIndex + Pageable.getPageSize()) {
                size = all.size() - fromIndex;
            } else {
                size = Pageable.getPageSize();
            }
            result.setSize(queryAll ? all.size() : size);
            result.setContent(queryAll ? all : all.subList(fromIndex, fromIndex + result.getSize()));
        } else {
            size = 0;
            result.setSize(queryAll ? all.size() : size);
            result.setContent(new ArrayList<>());
        }
        return result;
    }

    /**
     * 复制分页参数并重新设置内容
     *
     * @param rpage
     * @param list
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> Page<T> copyPropertiesAndResetContent(Page<R> rpage, List<T> list) {
        Page<T> tPage = new Page<>();
        BeanUtils.copyProperties(rpage, tPage);
        tPage.setContent(list);
        return tPage;
    }

    public static <T> Page<T> buildPage(int page, int size) {
        Page<T> result = new Page<>();
        result.setNumber(page);
        result.setSize(size);
        return result;
    }

    /**
     * 构建Page对象
     *
     * @param page    页数,第几页,从0开始
     * @param size    页大小,可为0
     * @param total   纪录总数
     * @param content 此页纪录
     * @param <T>     泛型
     * @return Page对象
     */
    public static <T> Page<T> buildPage(int page, int size, int total, List<T> content) {
        Page<T> result = buildPage(page, size);
        result.setTotalElements(total);
        result.setContent(content);
        result.setNumberOfElements(content.size());
        if (size != 0) {
            int rawPage = total / size;
            int remains = total % size;
            result.setTotalPages(remains == 0 ? rawPage : rawPage + 1);
        } else {
            result.setTotalPages(1);
        }
        return result;
    }


    /**
     * 把list转为分页查询的对象
     *
     * @param all 结果集
     * @param <T> 结果泛型
     * @return 分页结果
     */
    public static <T> Page<T> listAsPage(List<T> all) {
        Page<T> result = new Page<>();
        result.setContent(all);
        result.setTotalPages(1);
        result.setTotalElements(all.size());
        result.setNumber(1);
        result.setNumberOfElements(all.size());
        result.setSize(all.size());
        return result;
    }

    /**
     * 根据分页信息从所有的结果中设置Page对象
     *
     * @param all      包含所有内容的列表
     * @param pageable 分页参数
     * @return 根据分页参数所得分页内容
     */
    public static <T> Page<T> createPageFromList(List<T> all, PageRequest pageable) {
        Page<T> result = new Page<>();
        boolean queryAll = pageable.getSize() == 0;
        //当前页大小
        result.setNumberOfElements(queryAll ? all.size() : pageable.getSize());
        //当前页
        result.setNumber(pageable.getPage());
        //总共的大小
        result.setTotalElements(all.size());
        //总页数
        result.setTotalPages(queryAll ? 1 : (int) (Math.ceil(all.size() / (pageable.getSize() * 1.0))));
        //元素起始索引
        // 第一页从0开始
        int fromIndex = pageable.getSize() * pageable.getPage();
        int size;
        if (all.size() >= fromIndex) {
            if (all.size() <= fromIndex + pageable.getSize()) {
                size = all.size() - fromIndex;
            } else {
                size = pageable.getSize();
            }
            result.setSize(queryAll ? all.size() : size);
            result.setContent(queryAll ? all : all.subList(fromIndex, fromIndex + result.getSize()));
        } else {
            size = 0;
            result.setSize(queryAll ? all.size() : size);
            result.setContent(new ArrayList<>());
        }
        return result;
    }
}
