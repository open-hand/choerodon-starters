package io.choerodon.asgard.schedule.feign;

import io.choerodon.asgard.AsgardUpdateStatusException;
import io.choerodon.asgard.UpdateTaskInstanceStatusDTO;
import io.choerodon.asgard.schedule.dto.ScheduleTaskInstanceDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Slf4j
public class ScheduleMonitorClientCallback implements ScheduleMonitorClient {

    @Override
    public List<ScheduleTaskInstanceDTO> pollBatch(Set<String> methods) {
        log.warn("error.scheduleTaskInstance.poll, pollMethods {}", methods);
        return Collections.emptyList();
    }

    @Override
    public void updateStatus(Long id, UpdateTaskInstanceStatusDTO statusDTO) {
        throw new AsgardUpdateStatusException(id, statusDTO.getStatus());
    }
}

