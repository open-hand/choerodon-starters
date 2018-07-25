package io.choerodon.asgard.saga;

import io.choerodon.asgard.saga.dto.*;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "${choerodon.saga.service:asgard-service}", fallback = SagaClientCallback.class)
public interface SagaClient {

    @PostMapping("/v1/sagas/tasks/instances/poll/batch")
    List<SagaTaskInstanceDTO> pollBatch(@RequestBody PollBatchDTO pollBatchDTO);


    @PutMapping("/v1/sagas/tasks/instances/{id}/status")
    List<SagaTaskInstanceDTO> updateStatus(@PathVariable("id") Long id,
                                           @RequestBody SagaTaskInstanceStatusDTO statusDTO);

    @PostMapping("/v1/sagas/instances/{code}")
    SagaInstanceDTO startSaga(@PathVariable("code") String code,
                              @RequestBody StartInstanceDTO dto);


}
