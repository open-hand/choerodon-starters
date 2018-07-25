package io.choerodon.asgard.saga;

import io.choerodon.asgard.saga.dto.*;
import io.choerodon.core.exception.CommonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

public class SagaClientCallback implements SagaClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(SagaClient.class);


    @Override
    public List<SagaTaskInstanceDTO> pollBatch(PollBatchDTO pollBatchDTO) {
        LOGGER.info("error.sagaTaskInstance.poll, pollBatchDTO {}", pollBatchDTO);
        return Collections.emptyList();
    }

    @Override
    public List<SagaTaskInstanceDTO> updateStatus(Long id, SagaTaskInstanceStatusDTO statusDTO) {
        LOGGER.info("error.sagaTaskInstance.updateStatus, id {}, status {}", id, statusDTO);
        return Collections.emptyList();
    }

    @Override
    public SagaInstanceDTO startSaga(String code, StartInstanceDTO dto) {
        throw new CommonException("error.saga.start");
    }
}
