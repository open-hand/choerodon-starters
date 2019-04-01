package io.choerodon.dataset.model;

import io.choerodon.dataset.exception.DatasetException;
import org.apache.ibatis.session.SqlSession;

import java.util.List;
import java.util.Map;

/**
 * @author xausky
 */
public interface DatasetExecutor {
    /**
     * 查询方法
     */
    List<?> queries(SqlSession session, Map<String, Object> body, Integer pageNum, Integer pageSize, String sortname, Boolean isDesc) throws DatasetException;

    /**
     * 提交方法
     */
    List<?> mutations(SqlSession session, String json, Map.Entry<String, Object> parentKey) throws DatasetException;

    Map<String, Object> languages(SqlSession session, Map<String, Object> body) throws DatasetException;

    /**
     * 校验方法
     */
    List<Boolean> validate(SqlSession session, Map<String, Object> body, Map.Entry<String, Object> parentKey) throws DatasetException;
}