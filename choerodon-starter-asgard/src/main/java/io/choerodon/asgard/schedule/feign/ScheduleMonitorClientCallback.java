package io.choerodon.asgard.schedule.feign;

import io.choerodon.asgard.UpdateTaskInstanceStatusDTO;
import io.choerodon.asgard.saga.dto.SagaTaskInstanceDTO;

import java.util.List;
import java.util.Set;

class ScheduleMonitorClientCallback implements ScheduleMonitorClient {

    @Override
    public List<SagaTaskInstanceDTO> pollBatch(Set<String> method) {
        return null;
    }

    @Override
    public SagaTaskInstanceDTO updateStatus(Long id, UpdateTaskInstanceStatusDTO statusDTO) {
        return null;
    }
}

