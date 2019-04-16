package io.choerodon.mybatis.service;

import io.choerodon.mybatis.entity.Criteria;

import java.util.List;

/**
 * @author shengyang.zhou@hand-china.com
 */
public interface IBaseService<T> {

    default List<T> select(T condition, int pageNum, int pageSize){
        throw new UnsupportedOperationException();
    }

    default T insert(T record){
        throw new UnsupportedOperationException();
    }

    default T insertSelective(T record){
        throw new UnsupportedOperationException();
    }

    default T updateByPrimaryKey(T record){
        throw new UnsupportedOperationException();
    }

    default T updateByPrimaryKeySelective(T record){
        throw new UnsupportedOperationException();
    }

    default T updateByPrimaryKeyOptions(T record, Criteria criteria){
        throw new UnsupportedOperationException();
    }

    default T selectByPrimaryKey(T record){
        throw new UnsupportedOperationException();
    }

    default int deleteByPrimaryKey(T record){
        throw new UnsupportedOperationException();
    }

    default List<T> selectAll(){
        throw new UnsupportedOperationException();
    }

    default List<T> batchUpdate(List<T> list){
        throw new UnsupportedOperationException();
    }

    default int batchDelete(List<T> list){
        throw new UnsupportedOperationException();
    }

    default List<T> selectOptions(T record, Criteria criteria){
        throw new UnsupportedOperationException();
    }

    default List<T> selectOptions(T record, Criteria criteria, Integer pageNum, Integer pageSize){
        throw new UnsupportedOperationException();
    }

}
