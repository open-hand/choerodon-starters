package io.choerodon.asgard.saga.feign;

import io.choerodon.asgard.saga.dto.PollBatchDTO;
import io.choerodon.asgard.saga.dto.SagaTaskInstanceDTO;
import io.choerodon.asgard.UpdateTaskInstanceStatusDTO;
import io.choerodon.asgard.saga.exception.SagaUpdateStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

public class SagaMonitorClientCallback implements SagaMonitorClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(SagaClient.class);

    @Override
    public List<SagaTaskInstanceDTO> pollBatch(PollBatchDTO pollBatchDTO) {
        LOGGER.warn("error.sagaTaskInstance.poll, pollBatchDTO {}", pollBatchDTO);
        return Collections.emptyList();
    }

    @Override
    public SagaTaskInstanceDTO updateStatus(Long id, UpdateTaskInstanceStatusDTO statusDTO) {
        throw new SagaUpdateStatusException(id, statusDTO.getStatus());
    }

}
