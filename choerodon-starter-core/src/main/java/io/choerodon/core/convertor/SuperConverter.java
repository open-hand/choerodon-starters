package io.choerodon.core.convertor;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import org.springframework.util.Assert;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 抽象转换器
 *
 * @author superlee
 * @since 2019-07-31
 */
public interface SuperConverter<T, R> extends Function<T, R> {

    default List<R> convertToList(final List<T> input) {
        Assert.notNull(input, "error.converter.illegal.input");
        return input.stream().map(this::apply).collect(Collectors.toList());
    }

    default PageInfo<R> convertToPageInfo(final PageInfo<T> input) {
        Assert.notNull(input, "error.converter.illegal.input");
        Page<R> page = new Page<>(input.getPageNum(), input.getSize());
        try {
            page.setTotal(input.getTotal());
            page.addAll(convertToList(input.getList()));
            return page.toPageInfo();
        } finally {
            page.close();
        }
    }

}
