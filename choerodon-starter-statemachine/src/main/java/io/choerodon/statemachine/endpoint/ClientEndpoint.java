package io.choerodon.statemachine.endpoint;

import io.choerodon.statemachine.dto.ExecuteResult;
import io.choerodon.statemachine.dto.PropertyData;
import io.choerodon.statemachine.dto.StateMachineConfigDTO;
import io.choerodon.statemachine.dto.TransformInfo;
import io.choerodon.statemachine.enums.StateMachineConfigType;
import io.choerodon.statemachine.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * 状态机客户端回调端点
 *
 * @author shinan.chen
 * @author dinghuang123@gmail.com
 * @since 2018/10/9
 */

@RestController
public class ClientEndpoint {

    @Autowired
    @Qualifier("clientService")
    private ClientService clientService;

    private PropertyData stateMachinePropertyData;

    public ClientEndpoint(PropertyData stateMachinePropertyData) {
        this.stateMachinePropertyData = stateMachinePropertyData;
    }

    /**
     * 加载扫描的configCode
     *
     * @return
     */
    @GetMapping(value = "/statemachine/load_config_code", produces = {APPLICATION_JSON_VALUE})
    public PropertyData loadConfigCode() {
        return stateMachinePropertyData;
    }

    /**
     * 执行条件
     *
     * @param instanceId
     * @param targetStatusId
     * @param conditionStrategy
     * @param configDTOS
     * @return
     */
    @PostMapping(value = "v1/statemachine/execute_config_condition")
    public ResponseEntity<ExecuteResult> executeConfigCondition(@RequestParam(value = "instance_id") Long instanceId,
                                                                @RequestParam(value = "target_status_id", required = false) Long targetStatusId,
                                                                @RequestParam(value = "condition_strategy") String conditionStrategy,
                                                                @RequestBody List<StateMachineConfigDTO> configDTOS) {
        ExecuteResult executeResult = clientService.configExecuteCondition(instanceId, targetStatusId, conditionStrategy, configDTOS);
        return new ResponseEntity<>(executeResult, HttpStatus.OK);
    }

    /**
     * 执行验证
     *
     * @param instanceId
     * @param targetStatusId
     * @param configDTOS
     * @return
     */
    @PostMapping(value = "v1/statemachine/execute_config_validator")
    public ResponseEntity<ExecuteResult> executeConfigValidator(@RequestParam(value = "instance_id") Long instanceId,
                                                       @RequestParam(value = "target_status_id", required = false) Long targetStatusId,
                                                       @RequestBody List<StateMachineConfigDTO> configDTOS) {
        ExecuteResult executeResult = clientService.configExecuteValidator(instanceId, targetStatusId, configDTOS);
        return new ResponseEntity<>(executeResult, HttpStatus.OK);
    }

    /**
     * 执行后置动作
     *
     * @param instanceId
     * @param targetStatusId
     * @param configDTOS
     * @return
     */
    @PostMapping(value = "v1/statemachine/execute_config_action")
    public ResponseEntity<ExecuteResult> executeConfigAction(@RequestParam(value = "instance_id") Long instanceId,
                                                       @RequestParam(value = "target_status_id") Long targetStatusId,
                                                       @RequestParam(value = "transform_type") String transformType,
                                                       @RequestBody List<StateMachineConfigDTO> configDTOS) {
        ExecuteResult executeResult = clientService.configExecutePostAction(instanceId, targetStatusId, transformType, configDTOS);
        return new ResponseEntity<>(executeResult, HttpStatus.OK);
    }

    /**
     * 根据条件过滤转换
     *
     * @param instanceId    instanceId
     * @param transformDTOS transformDTOS
     * @return TransformInfo
     */
    @PostMapping(value = "v1/statemachine/filter_transform")
    public ResponseEntity<List<TransformInfo>> filterTransform(@RequestParam(value = "instance_id") Long instanceId,
                                                               @RequestBody List<TransformInfo> transformDTOS) {
        return new ResponseEntity<>(clientService.conditionFilter(instanceId, transformDTOS), HttpStatus.OK);
    }

}
