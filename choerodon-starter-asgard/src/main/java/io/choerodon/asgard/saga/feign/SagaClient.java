package io.choerodon.asgard.saga.feign;

import io.choerodon.asgard.saga.dto.SagaInstanceDTO;
import io.choerodon.asgard.saga.dto.StartInstanceDTO;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "${choerodon.saga.service:choerodon-asgard}", fallback = SagaClientCallback.class)
public interface SagaClient {

    /**
     * @deprecated 过期，请使用TransactionalProducer
     */
    @Deprecated
    @PostMapping("/v1/sagas/instances/{code}")
    SagaInstanceDTO startSaga(@PathVariable("code") String code,
                              @RequestBody StartInstanceDTO dto);


    @PostMapping("/v1/sagas/instances")
    SagaInstanceDTO preCreateSaga(@RequestBody StartInstanceDTO instanceDTO);


    @PostMapping("/v1/sagas/instances/{uuid}/confirm")
    void confirmSaga(@PathVariable("uuid") String uuid, @RequestBody StartInstanceDTO dto);


    @PutMapping("/v1/sagas/instances/{uuid}/cancel")
    void cancelSaga(@PathVariable("uuid") String uuid);

}