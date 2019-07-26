package io.choerodon.statemachine.client;

import io.choerodon.core.exception.CommonException;
import io.choerodon.statemachine.dto.*;
import io.choerodon.statemachine.service.ClientService;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/2/19
 */
public class StateMachineClient {

    public StateMachineClient(ClientService clientService, PropertyData stateMachinePropertyData) {
        this.clientService = clientService;
        this.stateMachinePropertyData = stateMachinePropertyData;
    }

    private ClientService clientService;
    private PropertyData stateMachinePropertyData;

    /**
     * 获取状态机客户端的属性配置
     *
     * @return
     */
    public PropertyData getStateMachinePropertyData() {
        return stateMachinePropertyData;
    }

    /**
     * 通过状态机客户端创建实例，不走状态机服务端流程，避免事务问题和过多的实例产生
     *
     * @param initTransform
     * @param inputDTO
     */
    public void createInstance(StateMachineTransformDTO initTransform, InputDTO inputDTO) {
        //获取初始状态
        Long statusId = initTransform.getEndStatusId();
        //执行条件
        List<StateMachineConfigDTO> conditions = initTransform.getConditions();
        if (conditions != null && conditions.isEmpty()) {
            inputDTO.setConfigs(conditions);
            ExecuteResult result = clientService.configExecuteCondition(statusId, initTransform.getConditionStrategy(), inputDTO);
            if (!result.getSuccess()) {
                throw new CommonException("error.stateMachineClient.createInstance.condition.fail");
            }
        }
        //执行验证
        List<StateMachineConfigDTO> validators = initTransform.getValidators();
        if (validators != null && validators.isEmpty()) {
            inputDTO.setConfigs(validators);
            ExecuteResult result = clientService.configExecuteValidator(statusId, inputDTO);
            if (!result.getSuccess()) {
                throw new CommonException("error.stateMachineClient.createInstance.validator.fail");
            }
        }
        //执行后置动作
        List<StateMachineConfigDTO> postpositions = initTransform.getPostpositions();
        if (postpositions != null && postpositions.isEmpty()) {
            inputDTO.setConfigs(postpositions);
            ExecuteResult result = clientService.configExecutePostAction(statusId, initTransform.getType(), inputDTO);
            if (!result.getSuccess()) {
                throw new CommonException("error.stateMachineClient.createInstance.action.fail");
            }
        }

    }
}
