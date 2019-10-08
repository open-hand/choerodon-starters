package io.choerodon.web.util;

import org.springframework.data.domain.Sort;

import java.util.Iterator;

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
