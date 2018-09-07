package io.choerodon.asgard.schedule.feign;

import io.choerodon.asgard.UpdateTaskInstanceStatusDTO;
import io.choerodon.asgard.schedule.dto.ScheduleTaskInstanceDTO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Set;

@FeignClient(name = "${choerodon.saga.service:asgard-service}", fallback = ScheduleMonitorClientCallback.class)
public interface ScheduleMonitorClient {

    @PostMapping("/v1/schedules/tasks/instances/poll/batch")
    List<ScheduleTaskInstanceDTO> pollBatch(@RequestBody Set<String> methods);


    @PutMapping("/v1/schedules/tasks/instances/{id}/status")
    void updateStatus(@PathVariable("id") Long id,
                      @RequestBody UpdateTaskInstanceStatusDTO statusDTO);

}
