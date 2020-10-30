package io.choerodon.asgard.saga.feign;

import io.choerodon.asgard.common.UpdateStatusDTO;
import io.choerodon.asgard.saga.dto.PollSagaTaskInstanceDTO;
import io.choerodon.asgard.saga.dto.SagaTaskInstanceDTO;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "${choerodon.saga.service:choerodon-asgard}")
public interface SagaConsumerClient {

    @PostMapping("/v1/ext/sagas/tasks/instances/poll")
    List<SagaTaskInstanceDTO> pollBatch(@RequestBody PollSagaTaskInstanceDTO pollSagaTaskInstanceDTO);


    @PutMapping("/v1/sagas/tasks/instances/{id}/status")
    ResponseEntity<String> updateStatus(@PathVariable("id") Long id,
                                        @RequestBody UpdateStatusDTO statusDTO);


    @PutMapping("/v1/sagas/tasks/instances/{id}/status/failure_callback")
    void updateStatusFailureCallback(@PathVariable("id") Long id,
                                     @RequestParam("status") String status);

    @GetMapping("/v1/sagas/tasks/instances/{id}")
    SagaTaskInstanceDTO queryStatus(@PathVariable("id") Long id);

}
