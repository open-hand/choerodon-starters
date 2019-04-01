package io.choerodon.dataset.service;

import io.choerodon.dataset.model.DatasetExecutor;

import java.util.List;
import java.util.Map;

public interface IDatasetRepositoryService {
    List<?> queries(String name, Map<String, Object> body, int pageNum, int pageSize, String sortname, boolean isDesc);

    List<?> mutations(String name, String body);

    Map<String, Object> languages(String name, Map<String, Object> body);

    List<Boolean> validate(String name, Map<String, Object> body);

    DatasetExecutor getExecutor(String name);
}
