package io.choerodon.core.util;

import io.choerodon.base.domain.PageRequest;
import io.choerodon.base.domain.Sort;

import java.util.*;

/**
 * feign param util
 *
 * @author superlee
 * @since 2019-07-11
 */
public class FeignParamUtils {

    private FeignParamUtils() {
    }

    /**
     * 将PageRequest编码为map
     * @param pageRequest
     * @return
     */
    public static Map<String, Object> encodePageRequest(PageRequest pageRequest) {
        Map<String, Object> map = new HashMap<>(3);
        map.put("page", pageRequest.getPage());
        map.put("size", pageRequest.getSize());
        Sort sort = pageRequest.getSort();
        if (sort != null) {
            List<String> values = new ArrayList<>();
            Iterator<Sort.Order> iterator = sort.iterator();
            while (iterator.hasNext()) {
                Sort.Order order = iterator.next();
                String value = order.getProperty() + "," + order.getDirection();
                values.add(value);
            }
            if (!values.isEmpty()) {
                map.put("sort", values);
            }
        }
        return map;
    }

}
