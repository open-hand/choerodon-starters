package io.choerodon.mybatis.pagehelper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Set;

import io.choerodon.mybatis.MapperException;
import io.choerodon.mybatis.domain.EntityColumn;
import io.choerodon.mybatis.helper.EntityHelper;
import org.apache.ibatis.mapping.MappedStatement;

import io.choerodon.mybatis.helper.MapperTemplate;
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
     * @return Sort对象转sql
     */
    public String sortToString(Sort sort, MappedStatement ms) {
        Iterator<Sort.Order> iterator = sort.iterator();
        StringBuilder stringBuilder = new StringBuilder();
        while (iterator.hasNext()) {
            Sort.Order order = iterator.next();
            //前端url传入的排序列名
            String property = order.getProperty();
            String direction = order.getDirection().toString();
            if (!order.isPropertyChanged()) {
                //根据ms获取entityClass,反射获取所有字段和注解
                Class<?> entityClass = getEntityClass(ms);
                String column = getColumn(entityClass, property);
                splicingSql(stringBuilder, direction, column);
            } else {
                //多表联查，拼接order by暂未做校验，可能会报sql语句错误或sql注入
                splicingSql(stringBuilder, direction, property);
            }
        }
        stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(","));
        return stringBuilder.toString();
    }

    private void splicingSql(StringBuilder stringBuilder, String direction, String column) {
        stringBuilder.append(column);
        stringBuilder.append(" ");
        stringBuilder.append(direction);
        stringBuilder.append(",");
    }

    private String getColumn(Class<?> entityClass, String property) {
        //支持前端传入字段为下划线格式
        String camelHumpProperty = StringUtil.underlineToCamelhump(property);
        Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
        //前端传入字段与do对象字段对比，不匹配抛非法参数异常
        for (EntityColumn entityColumn : columnList) {
            if (entityColumn.getProperty().toLowerCase().equals(camelHumpProperty.toLowerCase())) {
                return entityColumn.getColumn();
            }
        }
        throw new IllegalArgumentException("Illegal sort argument: " + property);
    }

    private Class<?> getEntityClass(MappedStatement ms) {
        String msId = ms.getId();
        Class<?> newMapperClass = MapperTemplate.getMapperClass(msId);
        Type[] types = newMapperClass.getGenericInterfaces();
        for (Type type : types) {
            if (type instanceof ParameterizedType) {
                ParameterizedType t = (ParameterizedType) type;
                Class<?> returnType = (Class<?>) t.getActualTypeArguments()[0];
                return returnType;
            }
        }
        throw new MapperException("无法获取Mapper<T>泛型类型:" + msId);
    }
}
