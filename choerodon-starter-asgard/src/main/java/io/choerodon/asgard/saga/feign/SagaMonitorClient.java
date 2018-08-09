package io.choerodon.asgard.saga.feign;

import io.choerodon.asgard.saga.dto.PollBatchDTO;
import io.choerodon.asgard.saga.dto.SagaTaskInstanceDTO;
import io.choerodon.asgard.saga.dto.SagaTaskInstanceStatusDTO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@FeignClient(name = "${choerodon.saga.service:asgard-service}", fallback = SagaMonitorClientCallback.class)
public interface SagaMonitorClient {

    @PostMapping("/v1/sagas/tasks/instances/poll/batch")
    Set<SagaTaskInstanceDTO> pollBatch(@RequestBody PollBatchDTO pollBatchDTO);


    @PutMapping("/v1/sagas/tasks/instances/{id}/status")
    SagaTaskInstanceDTO updateStatus(@PathVariable("id") Long id,
                                     @RequestBody SagaTaskInstanceStatusDTO statusDTO);

}
