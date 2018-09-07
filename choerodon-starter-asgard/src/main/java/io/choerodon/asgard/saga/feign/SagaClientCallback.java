package io.choerodon.asgard.saga.feign;

import io.choerodon.asgard.saga.dto.SagaInstanceDTO;
import io.choerodon.asgard.saga.dto.StartInstanceDTO;
import io.choerodon.core.exception.CommonException;
import org.springframework.cloud.netflix.feign.FeignClient;

@FeignClient(name = "${choerodon.saga.service:asgard-service}", fallback = SagaClientCallback.class)
public class SagaClientCallback implements SagaClient {

    @Override
    public SagaInstanceDTO startSaga(String code, StartInstanceDTO dto) {
        throw new CommonException("error.saga.start");
    }

}
