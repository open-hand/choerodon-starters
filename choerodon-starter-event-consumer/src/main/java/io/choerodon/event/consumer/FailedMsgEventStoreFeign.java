package io.choerodon.event.consumer;

import io.choerodon.event.consumer.domain.FailedMsg;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * event store服务调用feign
 *
 * @author zhipeng.zuo
 * 2018/2/5
 */
@FeignClient("${choerodon.event.store.service:event-store-service}")
public interface FailedMsgEventStoreFeign {

    /**
     * 调用feign将消息回传给event store
     *
     * @param failedMsg 失败的消息
     * @return feign调用结果，响应实体为空
     */
    @RequestMapping(value = "v1/messages/failed", method = RequestMethod.POST)
    ResponseEntity<Void> storeFailedMsg(@RequestBody FailedMsg failedMsg);

}