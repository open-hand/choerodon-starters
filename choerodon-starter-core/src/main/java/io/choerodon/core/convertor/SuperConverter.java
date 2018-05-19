package io.choerodon.core.convertor;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 实体对象转换超类
 *
 * @author superleader8@gmail.com
 */
public interface SuperConverter<T, R> extends Function<T, R> {

    default List<R> convertToList(final List<T> input) {
        return input.stream().map(this::apply).collect(Collectors.toList());
    }

}