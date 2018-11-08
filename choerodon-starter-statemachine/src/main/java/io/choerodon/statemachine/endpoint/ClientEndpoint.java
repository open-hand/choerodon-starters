package io.choerodon.statemachine.endpoint;

import io.choerodon.statemachine.dto.ExecuteResult;
import io.choerodon.statemachine.dto.InputDTO;
import io.choerodon.statemachine.dto.PropertyData;
import io.choerodon.statemachine.dto.TransformInfo;
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
     * @return PropertyData
     */
    @GetMapping(value = "/statemachine/load_config_code", produces = {APPLICATION_JSON_VALUE})
    public PropertyData loadConfigCode() {
        return stateMachinePropertyData;
    }

    /**
     * 执行条件
     *
     * @param targetStatusId
     * @param conditionStrategy
     * @param inputDTO
     * @return
     */
    @PostMapping(value = "v1/statemachine/execute_config_condition")
    public ResponseEntity<ExecuteResult> executeConfigCondition(@RequestParam(value = "target_status_id", required = false) Long targetStatusId,
                                                                @RequestParam(value = "condition_strategy") String conditionStrategy,
                                                                @RequestBody InputDTO inputDTO) {
        ExecuteResult executeResult = clientService.configExecuteCondition(targetStatusId, conditionStrategy, inputDTO);
        return new ResponseEntity<>(executeResult, HttpStatus.OK);
    }

    public ClientEndpoint() {
        super();
    }

    /**
     * 执行验证
     *
     * @param targetStatusId
     * @param inputDTO
     * @return
     */
    @PostMapping(value = "v1/statemachine/execute_config_validator")
    public ResponseEntity<ExecuteResult> executeConfigValidator(@RequestParam(value = "target_status_id", required = false) Long targetStatusId,
                                                                @RequestBody InputDTO inputDTO) {
        ExecuteResult executeResult = clientService.configExecuteValidator(targetStatusId, inputDTO);
        return new ResponseEntity<>(executeResult, HttpStatus.OK);
    }

    /**
     * 执行后置动作
     *
     * @param targetStatusId
     * @param transformType
     * @param inputDTO
     * @return
     */
    @PostMapping(value = "v1/statemachine/execute_config_action")
    public ResponseEntity<ExecuteResult> executeConfigAction(@RequestParam(value = "target_status_id") Long targetStatusId,
                                                             @RequestParam(value = "transform_type") String transformType,
                                                             @RequestBody InputDTO inputDTO) {
        ExecuteResult executeResult = clientService.configExecutePostAction(targetStatusId, transformType, inputDTO);
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
