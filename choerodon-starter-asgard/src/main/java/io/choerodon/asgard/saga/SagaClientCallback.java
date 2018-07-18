package io.choerodon.asgard.saga;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

public class SagaClientCallback implements SagaClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(SagaClient.class);


    @Override
    public List<DataObject.SagaTaskInstanceDTO> pollBatch(DataObject.PollBatchDTO pollBatchDTO) {
        LOGGER.info("error.sagaTaskInstance.poll, pollBatchDTO {}", pollBatchDTO);
        return Collections.emptyList();
    }

    @Override
    public List<DataObject.SagaTaskInstanceDTO> updateStatus(Long id, DataObject.SagaTaskInstanceStatusDTO statusDTO) {
        LOGGER.info("error.sagaTaskInstance.updateStatus, id {}, status {}", id, statusDTO);
        return Collections.emptyList();
    }

    @Override
    public DataObject.SagaInstance startSaga(String code, DataObject.StartInstanceDTO dto) {
        LOGGER.info("error.saga.start, code {} , startDto {}", code, dto);
        return null;
    }
}
