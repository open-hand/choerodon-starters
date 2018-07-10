package io.choerodon.saga;

import java.util.List;

public class SagaClientCallback implements SagaClient {

    @Override
    public List<SagaTaskInstanceDTO> pollBatch(String code, String instance, List<Long> filterIds) {
        return null;
    }

    @Override
    public List<SagaTaskInstanceDTO> updateStatus(Long id, SagaTaskInstanceStatusDTO statusDTO) {
        return null;
    }
}
