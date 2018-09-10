package io.choerodon.asgard.schedule.feign;

import io.choerodon.asgard.UpdateTaskInstanceStatusDTO;
import io.choerodon.asgard.schedule.dto.ScheduleInstanceConsumerDTO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@FeignClient(name = "${choerodon.saga.service:asgard-service}", fallback = ScheduleMonitorClientCallback.class)
public interface ScheduleMonitorClient {

    @PostMapping("/v1/schedules/tasks/instances/poll/batch")
    List<ScheduleInstanceConsumerDTO> pollBatch(@RequestBody Set<String> methods,
                                                @RequestParam("instance") String instance);


    @PutMapping("/v1/schedules/tasks/instances/{id}/status")
    void updateStatus(@PathVariable("id") Long id,
                      @RequestBody UpdateTaskInstanceStatusDTO statusDTO);

}
