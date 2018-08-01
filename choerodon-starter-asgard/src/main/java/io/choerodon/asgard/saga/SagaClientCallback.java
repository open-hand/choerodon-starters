package io.choerodon.asgard.saga;

import io.choerodon.asgard.saga.dto.*;
import io.choerodon.core.exception.CommonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;

public class SagaClientCallback implements SagaClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(SagaClient.class);


    @Override
    public Set<SagaTaskInstanceDTO> pollBatch(PollBatchDTO pollBatchDTO) {
        LOGGER.info("error.sagaTaskInstance.poll, pollBatchDTO {}", pollBatchDTO);
        return Collections.emptySet();
    }

    @Override
    public SagaTaskInstanceDTO updateStatus(Long id, SagaTaskInstanceStatusDTO statusDTO) {
        LOGGER.info("error.sagaTaskInstance.updateStatus, id {}, status {}", id, statusDTO);
        throw new CommonException("error.saga.updateStatus");
    }

    @Override
    public SagaInstanceDTO startSaga(String code, StartInstanceDTO dto) {
        throw new CommonException("error.saga.start");
    }
}
