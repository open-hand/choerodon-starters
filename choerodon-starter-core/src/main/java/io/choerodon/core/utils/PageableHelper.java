package io.choerodon.core.utils;

import java.util.Iterator;

import org.springframework.data.domain.Sort;

public class PageableHelper {
    public static String getSortSql(Sort sort){
        StringBuilder sql = new StringBuilder();
        Iterator<Sort.Order> iterator = sort.iterator();
        while (iterator.hasNext()){
            Sort.Order order = iterator.next();
            sql.append(order.getProperty());
            sql.append(' ');
            sql.append(order.getDirection());
            if (iterator.hasNext()) {
                sql.append(',');
            }
        }
        return sql.toString();
    }
}
