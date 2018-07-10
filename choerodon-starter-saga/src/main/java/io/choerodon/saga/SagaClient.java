package io.choerodon.saga;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "${choerodon.saga.service:asgard-service}", fallback = SagaClientCallback.class)
public interface SagaClient {

    @PostMapping("/v1/saga/tasks/instances/{code:.*}/batch")
    List<DataObject.SagaTaskInstanceDTO> pollBatch(@PathVariable String code,
                                                   @RequestParam("instance") String instance,
                                                   @RequestParam(name = "filter_ids", required = false) List<Long> filterIds);



    @PostMapping("/v1/saga/tasks/instances/{id}/status")
    List<DataObject.SagaTaskInstanceDTO> updateStatus(@PathVariable Long id,
                                                      @RequestBody DataObject.SagaTaskInstanceStatusDTO statusDTO);


}
