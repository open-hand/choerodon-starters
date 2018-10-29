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

    @GetMapping(value = "/statemachine/load_config_code", produces = {APPLICATION_JSON_VALUE})
    public PropertyData loadConfigCode() {
        return stateMachinePropertyData;
    }

    /**
     * 执行条件，验证，后置处理
     *
     * @param instanceId        instanceId
     * @param targetStatusId    targetStatusId
     * @param type              type
     * @param conditionStrategy conditionStrategy
     * @param configDTOS        configDTOS
     * @return ExecuteResult
     */
    @PostMapping(value = "v1/statemachine/execute_config")
    public ResponseEntity<ExecuteResult> executeConfig(@RequestParam(value = "instance_id") Long instanceId,
                                                       @RequestParam(value = "target_status_id", required = false) Long targetStatusId,
                                                       @RequestParam(value = "type") String type,
                                                       @RequestParam(value = "condition_strategy", required = false) String conditionStrategy,
                                                       @RequestBody List<StateMachineConfigDTO> configDTOS) {

        ExecuteResult executeResult;
        switch (type) {
            case StateMachineConfigType.CONDITION:
                executeResult = clientService.configExecuteCondition(instanceId, targetStatusId, conditionStrategy, configDTOS);
                break;
            case StateMachineConfigType.VALIDATOR:
                executeResult = clientService.configExecuteValidator(instanceId, targetStatusId, configDTOS);
                break;
            default:
                executeResult = clientService.configExecutePostAction(instanceId, targetStatusId, configDTOS);
                break;
        }
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
