package io.choerodon.asgard.saga;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "${choerodon.saga.service:asgard-service}", fallback = SagaClientCallback.class)
public interface SagaClient {

    @PostMapping("/v1/saga/tasks/instances/poll/batch")
    List<DataObject.SagaTaskInstanceDTO> pollBatch(@RequestBody DataObject.PollBatchDTO pollBatchDTO);


    @PutMapping("/v1/saga/tasks/instances/{id}/status")
    List<DataObject.SagaTaskInstanceDTO> updateStatus(@PathVariable("id") Long id,
                                                      @RequestBody DataObject.SagaTaskInstanceStatusDTO statusDTO);

    @PostMapping("/v1/saga/{code}/instances")
    DataObject.SagaInstance startSaga(@PathVariable("code") String code,
                                      @RequestBody DataObject.StartInstanceDTO dto);


}
