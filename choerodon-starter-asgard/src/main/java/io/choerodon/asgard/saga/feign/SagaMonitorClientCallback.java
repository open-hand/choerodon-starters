package io.choerodon.asgard.saga.feign;

import io.choerodon.asgard.saga.dto.PollBatchDTO;
import io.choerodon.asgard.saga.dto.SagaTaskInstanceDTO;
import io.choerodon.asgard.saga.dto.SagaTaskInstanceStatusDTO;
import io.choerodon.asgard.saga.exception.SagaUpdateStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;

public class SagaMonitorClientCallback implements SagaMonitorClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(SagaClient.class);

    @Override
    public Set<SagaTaskInstanceDTO> pollBatch(PollBatchDTO pollBatchDTO) {
        LOGGER.warn("error.sagaTaskInstance.poll, pollBatchDTO {}", pollBatchDTO);
        return Collections.emptySet();
    }

    @Override
    public SagaTaskInstanceDTO updateStatus(Long id, SagaTaskInstanceStatusDTO statusDTO) {
        throw new SagaUpdateStatusException(id, statusDTO.getStatus());
    }

}
