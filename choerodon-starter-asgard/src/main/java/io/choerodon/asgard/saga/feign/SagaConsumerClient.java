package io.choerodon.asgard.saga.feign;

import io.choerodon.asgard.common.UpdateStatusDTO;
import io.choerodon.asgard.saga.dto.PollSagaTaskInstanceDTO;
import io.choerodon.asgard.saga.dto.SagaTaskInstanceDTO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "${choerodon.saga.service:go-asgard-service}", fallback = SagaConsumerClientCallback.class)
public interface SagaConsumerClient {

    @PostMapping("/v1/sagas/tasks/instances/poll")
    List<SagaTaskInstanceDTO> pollBatch(@RequestBody PollSagaTaskInstanceDTO pollSagaTaskInstanceDTO);


    @PutMapping("/v1/sagas/tasks/instances/{id}/status")
    void updateStatus(@PathVariable("id") Long id,
                      @RequestBody UpdateStatusDTO statusDTO);

    @PutMapping("/v1/sagas/tasks/instances/{id}/status")
    void retryUpdateStatus(@PathVariable("id") Long id,
                           @RequestBody UpdateStatusDTO statusDTO);


    @GetMapping("/v1/sagas/tasks/instances/{id}")
    SagaTaskInstanceDTO queryStatus(@PathVariable("id") Long id);

}
