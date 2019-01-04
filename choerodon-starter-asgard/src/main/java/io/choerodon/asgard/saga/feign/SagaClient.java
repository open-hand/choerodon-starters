package io.choerodon.asgard.saga.feign;

import io.choerodon.asgard.saga.dto.SagaInstanceDTO;
import io.choerodon.asgard.saga.dto.StartInstanceDTO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "${choerodon.saga.service:asgard-service}", fallback = SagaClientCallback.class)
public interface SagaClient {

    @PostMapping("/v1/sagas/instances/{code}")
    SagaInstanceDTO startSaga(@PathVariable("code") String code,
                              @RequestBody StartInstanceDTO dto);


    @PostMapping("/v1/sagas/instances")
    SagaInstanceDTO preCreateSaga(@RequestBody StartInstanceDTO instanceDTO);


    @PostMapping("/v1/sagas/instances/{uuid}/confirm")
    void confirmSaga(@PathVariable("uuid") String uuid, @RequestBody String json);


    @PutMapping("/v1/sagas/instances/{uuid}/cancel")
    void cancelSaga(@PathVariable("uuid") String uuid);

}