package io.choerodon.saga;

import io.choerodon.core.exception.CommonException;

import java.util.List;

public class SagaClientCallback implements SagaClient {

    @Override
    public List<DataObject.SagaTaskInstanceDTO> pollBatch(String code, String instance, List<Long> filterIds) {
        throw new CommonException("error.sagaTaskInstance.poll");
    }

    @Override
    public List<DataObject.SagaTaskInstanceDTO> updateStatus(Long id, DataObject.SagaTaskInstanceStatusDTO statusDTO) {
        throw new CommonException("error.sagaTaskInstance.updateStatus");
    }

}
