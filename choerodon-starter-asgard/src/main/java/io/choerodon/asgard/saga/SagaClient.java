package io.choerodon.asgard.saga;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@FeignClient(name = "${choerodon.saga.service:asgard-service}", fallback = SagaClientCallback.class)
public interface SagaClient {

    @GetMapping("/v1/saga/tasks/instances/{codes}/batch")
    List<DataObject.SagaTaskInstanceDTO> pollBatch(@PathVariable("codes") Set<String> codes,
                                                   @RequestParam("instance") String instance);


    @PutMapping("/v1/saga/tasks/instances/{id}/status")
    List<DataObject.SagaTaskInstanceDTO> updateStatus(@PathVariable("id") Long id,
                                                      @RequestBody DataObject.SagaTaskInstanceStatusDTO statusDTO);

    @PostMapping("/v1/saga/{code}/instances")
    DataObject.SagaInstance startSaga(@PathVariable("code") String code,
                                      @RequestBody DataObject.StartInstanceDTO dto);


}
