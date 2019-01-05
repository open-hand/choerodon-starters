package io.choerodon.asgard.saga.feign;

import io.choerodon.asgard.saga.dto.SagaInstanceDTO;
import io.choerodon.asgard.saga.dto.StartInstanceDTO;
import io.choerodon.asgard.saga.exception.SagaProducerException;

public class SagaClientCallback implements SagaClient {

    /**
     * @deprecated 过期，请使用TransactionalProducer
     */
    @Deprecated
    @Override
    public SagaInstanceDTO startSaga(String code, StartInstanceDTO dto) {
        throw new SagaProducerException("error.saga.start");
    }

    @Override
    public SagaInstanceDTO preCreateSaga(StartInstanceDTO instanceDTO) {
        throw new SagaProducerException("error.saga.preCreate, sagaCode: " + instanceDTO.getSagaCode());
    }

    @Override
    public void confirmSaga(String uuid, StartInstanceDTO dto) {
        throw new SagaProducerException("error.saga.confirm, uuid: " + uuid);
    }

    @Override
    public void cancelSaga(String uuid) {
        throw new SagaProducerException("error.saga.cancel, uuid: " + uuid);
    }

}
