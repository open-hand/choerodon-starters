package io.choerodon.dataset.service;

import java.util.List;
import java.util.Map;

public interface IDatasetService<T> {
    List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc);

    List<T> mutations(List<T> objs);
}
