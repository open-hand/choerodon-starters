package io.choerodon.event.producer.execute;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 调用event-store-service的feign
 *
 * @author flyleft
 */
@FeignClient(name = "${choerodon.event.store.service:event-store-service}", fallback = EventStoreClientFallback.class)
public interface EventStoreClient {

    /**
     * 创建事件
     *
     * @param eventRecord 事件实体
     * @return uuid
     */
    @PostMapping("/v1/events")
    String createEvent(@RequestBody EventRecord eventRecord);

    /**
     * 确认事件
     *
     * @param uuid 事件的uuid
     * @param messages 发送到消息队列的存储信息(json形式)
     */
    @PostMapping("/v1/events/{uuid}/pre_confirm")
    void preConfirmEvent(@PathVariable("uuid") String uuid, @RequestBody String messages);

    /**
     * 确认事件
     *
     * @param uuid 事件的uuid
     */
    @PutMapping("/v1/events/{uuid}/confirm")
    void confirmEvent(@PathVariable("uuid") String uuid);

    /**
     * 取消事件
     *
     * @param uuid 事件的uuid
     */
    @PutMapping("/v1/events/{uuid}/cancel")
    void cancelEvent(@PathVariable("uuid") String uuid);

}
