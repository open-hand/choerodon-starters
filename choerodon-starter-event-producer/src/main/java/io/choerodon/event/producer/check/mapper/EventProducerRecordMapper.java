package io.choerodon.event.producer.check.mapper;

import io.choerodon.event.producer.check.EventProducerRecord;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author flyleft
 * 2018/5/17
 */
public interface EventProducerRecordMapper extends BaseMapper<EventProducerRecord> {

    /**
     * 查询uuid的个数
     *
     * @param uuid 消息的个数uuid
     * @return 个数
     */
    @Select({
            "select count(*) from event_producer_record where uuid = #{uuid}"
    })
    int countUuid(@Param("uuid") String uuid);

}
