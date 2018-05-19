package io.choerodon.event.consumer;

import java.sql.Timestamp;

import io.choerodon.event.consumer.domain.EventConsumerRecord;
import io.choerodon.event.consumer.mapper.EventConsumerRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 默认去重复的实现
 * @author flyleft
 * 2017/10/26
 */
public class DefaultDuplicateRemoveListener implements DuplicateRemoveListener {

    private EventConsumerRecordMapper mapper;

    @Autowired
    public DefaultDuplicateRemoveListener(EventConsumerRecordMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public boolean hasBeanConsumed(String uuid) {
        return mapper.countUuid(uuid) > 0;
    }

    @Override
    public void after(String uuid) {
        mapper.insert(new EventConsumerRecord(uuid, new Timestamp(System.currentTimeMillis())));
    }

}
