package io.choerodon.asgard.saga;

import io.choerodon.core.exception.CommonException;

import java.util.List;
import java.util.Set;

public class SagaClientCallback implements SagaClient {

    @Override
    public List<DataObject.SagaTaskInstanceDTO> pollBatch(String code, String instance, Set<Long> filterIds) {
        throw new CommonException("error.sagaTaskInstance.poll");
    }

    @Override
    public List<DataObject.SagaTaskInstanceDTO> updateStatus(Long id, DataObject.SagaTaskInstanceStatusDTO statusDTO) {
        throw new CommonException("error.sagaTaskInstance.updateStatus");
    }

}
