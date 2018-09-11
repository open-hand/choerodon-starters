package io.choerodon.asgard.saga.feign;

import io.choerodon.asgard.saga.dto.PollBatchDTO;
import io.choerodon.asgard.saga.dto.SagaTaskInstanceDTO;
import io.choerodon.asgard.UpdateTaskInstanceStatusDTO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "${choerodon.saga.service:asgard-service}", fallback = SagaMonitorClientCallback.class)
public interface SagaMonitorClient {

    @PostMapping("/v1/sagas/tasks/instances/poll/batch")
    List<SagaTaskInstanceDTO> pollBatch(@RequestBody PollBatchDTO pollBatchDTO);


    @PutMapping("/v1/sagas/tasks/instances/{id}/status")
    SagaTaskInstanceDTO updateStatus(@PathVariable("id") Long id,
                                     @RequestBody UpdateTaskInstanceStatusDTO statusDTO);

}
