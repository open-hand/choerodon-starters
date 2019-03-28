package io.choerodon.base.service;

import io.choerodon.mybatis.entity.Criteria;

import java.util.List;

/**
 * @author shengyang.zhou@hand-china.com
 */
public interface IBaseService<T> {

    List<T> select(T condition, int pageNum, int pageSize);

    T insert(T record);

    T insertSelective(T record);

    T updateByPrimaryKey(T record);

    T updateByPrimaryKeySelective(T record);

    T updateByPrimaryKeyOptions(T record, Criteria criteria);

    T selectByPrimaryKey(T record);

    int deleteByPrimaryKey(T record);

    List<T> selectAll();

    List<T> batchUpdate(List<T> list);

    int batchDelete(List<T> list);

    List<T> selectOptions(T record, Criteria criteria);

    List<T> selectOptions(T record, Criteria criteria, Integer pageNum, Integer pageSize);

}
