package io.choerodon.event.consumer.mapper;

import io.choerodon.event.consumer.domain.EventConsumerRecord;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * MsgRecord的mapper
 * @author flyleft
 * 2017/10/18
 */
public interface EventConsumerRecordMapper extends BaseMapper<EventConsumerRecord> {

    /**
     * 查询uuid的个数
     *
     * @param uuid 消息的个数uuid
     * @return 个数
     */
    @Select({
            "select count(*) from event_consumer_record where uuid = #{uuid}"
    })
    int countUuid(@Param("uuid") String uuid);

}
