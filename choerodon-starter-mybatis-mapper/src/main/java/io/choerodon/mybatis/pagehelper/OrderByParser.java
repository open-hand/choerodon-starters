package io.choerodon.mybatis.pagehelper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;

import org.apache.ibatis.mapping.MappedStatement;

import io.choerodon.mybatis.helper.MapperTemplate;
import io.choerodon.mybatis.helper.SqlHelper;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.mybatis.util.StringUtil;


/**
 * Sort对象转sql工具类
 *
 * @author superleader8@gmail.com
 */
public class OrderByParser {

    /**
     * Sort对象转sql
     *
     * @param sort sort
     * @param ms   MappedStatement
     * @return Sort对象转的sql
     */
    public String sortToString(Sort sort, MappedStatement ms) {
        Iterator<Sort.Order> iterator = sort.iterator();
        StringBuilder stringBuilder = new StringBuilder();
        while (iterator.hasNext()) {
            Sort.Order order = iterator.next();
            //前端request字段为驼峰，转为下划线
            String column = null;
            if (!order.isPropertyChanged()) {
                sort.setMultiTableQueryFlag(false);
                //没有做pageHelper.restOrder操作，isPropertyChanged为false
                //单表列名会和数据库列名对比，若数据库中不存在的列，则抛异常，所以不进行sql注入校验
                Class<?> mapperClass = MapperTemplate.getMapperClass(ms.getId());
                Type[] types = mapperClass.getGenericInterfaces();
                ParameterizedType t = (ParameterizedType) types[0];
                //获得实体类
                Class<?> entityClass = (Class<?>) t.getActualTypeArguments()[0];
                //获得实体类所有的列
                String columns = SqlHelper.getAllColumns(entityClass);
                //单表校验传参是否合法
                column = StringUtil.camelhumpToUnderline(order.getProperty());
                if (!columns.contains(column)) {
                    //传参不是domain类的字段，抛异常
                    throw new IllegalArgumentException("error.Sort.Order.property["
                            + order.getProperty() + "].illegal");
                }
                stringBuilder.append(column);
                stringBuilder.append(" ");
                stringBuilder.append(order.getDirection());
                stringBuilder.append(",");
            } else {
                sort.setMultiTableQueryFlag(true);
                //多表联查，拼接order by暂未做校验，可能会报sql语句错误或sql注入
                stringBuilder.append(order.getProperty());
                stringBuilder.append(" ");
                stringBuilder.append(order.getDirection());
                stringBuilder.append(",");
            }
        }
        stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(","));
        return stringBuilder.toString();
    }
}
