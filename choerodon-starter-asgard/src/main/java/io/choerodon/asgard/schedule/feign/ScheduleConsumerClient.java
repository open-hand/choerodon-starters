package io.choerodon.asgard.schedule.feign;

import io.choerodon.asgard.common.UpdateStatusDTO;
import io.choerodon.asgard.schedule.dto.PollScheduleInstanceDTO;
import io.choerodon.asgard.schedule.dto.ScheduleInstanceConsumerDTO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "${choerodon.saga.service:go-asgard-service}", fallback = ScheduleConsumerClientCallback.class)
public interface ScheduleConsumerClient {

    @PostMapping("/v1/schedules/tasks/instances/poll/batch")
    List<ScheduleInstanceConsumerDTO> pollBatch(@RequestBody PollScheduleInstanceDTO pollScheduleInstanceDTO);


    @PutMapping("/v1/schedules/tasks/instances/{id}/status")
    void updateStatus(@PathVariable("id") Long id,
                      @RequestBody UpdateStatusDTO statusDTO);

}
