package io.choerodon.statemachine.feign;

import io.choerodon.statemachine.dto.ExecuteResult;
import io.choerodon.statemachine.dto.InputDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

/**
 * @author peng.jiang, dinghuang123@gmail.com
 * @since 2018/10/29
 */
@FeignClient(value = "state-machine-service",
        fallback = InstanceFeignClientFallback.class)
@Component
public interface InstanceFeignClient {

    /**
     * 创建状态机实例
     *
     * @param organizationId organizationId
     * @param serviceCode    serviceCode
     * @param stateMachineId stateMachineId
     * @return ExecuteResult
     */
    @RequestMapping(value = "/v1/organizations/{organization_id}/instances/start_instance", method = RequestMethod.POST)
    ResponseEntity<ExecuteResult> startInstance(@PathVariable("organization_id") Long organizationId,
                                                @RequestParam("service_code") String serviceCode,
                                                @RequestParam("state_machine_id") Long stateMachineId,
                                                @RequestBody InputDTO inputDTO);

    /**
     * 执行转换
     *
     * @param organizationId  organizationId
     * @param serviceCode     serviceCode
     * @param stateMachineId  stateMachineId
     * @param currentStatusId currentStatusId
     * @param transformId     transformId
     * @return ExecuteResult
     */
    @RequestMapping(value = "/v1/organizations/{organization_id}/instances/execute_transform", method = RequestMethod.POST)
    ResponseEntity<ExecuteResult> executeTransform(@PathVariable("organization_id") Long organizationId,
                                                   @RequestParam("service_code") String serviceCode,
                                                   @RequestParam("state_machine_id") Long stateMachineId,
                                                   @RequestParam("current_status_id") Long currentStatusId,
                                                   @RequestParam("transform_id") Long transformId,
                                                   @RequestBody InputDTO inputDTO);

    /**
     * 获取状态机的初始状态
     *
     * @param organizationId organizationId
     * @param stateMachineId organizationId
     * @return Long
     */
    @GetMapping(value = "/v1/organizations/{organization_id}/instances/query_init_status_id")
    ResponseEntity<Long> queryInitStatusId(@PathVariable("organization_id") Long organizationId,
                                           @RequestParam("state_machine_id") Long stateMachineId);
}
