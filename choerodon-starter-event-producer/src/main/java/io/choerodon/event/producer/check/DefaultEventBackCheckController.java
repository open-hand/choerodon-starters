package io.choerodon.event.producer.check;

import io.choerodon.core.event.EventBackCheckRecord;
import io.choerodon.core.event.EventStatus;
import io.choerodon.event.producer.check.mapper.EventProducerRecordMapper;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author flyleft
 * 2018/5/17
 */
@RestController
public class DefaultEventBackCheckController {

    private EventProducerRecordMapper eventProducerRecordMapper;

    public DefaultEventBackCheckController(EventProducerRecordMapper eventProducerRecordMapper) {
        this.eventProducerRecordMapper = eventProducerRecordMapper;
    }

    @GetMapping("/v1/events/{uuid}/{type}")
    public EventBackCheckRecord queryEventStatus(@PathVariable("uuid") String uuid, @PathVariable("type") String type) {
        if (StringUtils.isEmpty(uuid)) {
            return new EventBackCheckRecord(uuid, EventStatus.CANCELED);
        }
        if (eventProducerRecordMapper.countUuid(uuid) > 0) {
            return new EventBackCheckRecord(uuid, EventStatus.CONFIRMED);
        }
        return new EventBackCheckRecord(uuid, EventStatus.CANCELED);
    }

}
