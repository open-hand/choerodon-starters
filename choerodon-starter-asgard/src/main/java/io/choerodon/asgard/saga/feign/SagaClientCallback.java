package io.choerodon.asgard.saga.feign;

import io.choerodon.asgard.saga.dto.SagaInstanceDTO;
import io.choerodon.asgard.saga.dto.StartInstanceDTO;
import io.choerodon.core.exception.CommonException;

public class SagaClientCallback implements SagaClient {

    @Override
    public SagaInstanceDTO startSaga(String code, StartInstanceDTO dto) {
        throw new CommonException("error.saga.start");
    }

}
