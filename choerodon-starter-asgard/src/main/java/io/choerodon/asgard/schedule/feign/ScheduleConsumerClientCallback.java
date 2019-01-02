package io.choerodon.asgard.schedule.feign;

import io.choerodon.asgard.common.UpdateStatusDTO;
import io.choerodon.asgard.common.UpdateStatusException;
import io.choerodon.asgard.schedule.dto.PollScheduleInstanceDTO;
import io.choerodon.asgard.schedule.dto.ScheduleInstanceConsumerDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

public class ScheduleConsumerClientCallback implements ScheduleConsumerClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleConsumerClientCallback.class);

    @Override
    public List<ScheduleInstanceConsumerDTO> pollBatch(PollScheduleInstanceDTO pollScheduleInstanceDTO) {
        LOGGER.warn("error.scheduleTaskInstance.poll, pollScheduleInstanceDTO {}", pollScheduleInstanceDTO);
        return Collections.emptyList();
    }

    @Override
    public void updateStatus(Long id, UpdateStatusDTO statusDTO) {
        throw new UpdateStatusException(id, statusDTO.getStatus());
    }
}

