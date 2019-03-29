package io.choerodon.statemachine.client;

import io.choerodon.core.exception.CommonException;
import io.choerodon.statemachine.dto.ExecuteResult;
import io.choerodon.statemachine.dto.InputDTO;
import io.choerodon.statemachine.dto.StateMachineConfigDTO;
import io.choerodon.statemachine.dto.StateMachineTransformDTO;
import io.choerodon.statemachine.feign.InstanceFeignClient;
import io.choerodon.statemachine.service.ClientService;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/2/19
 */
public class StateMachineClient {
    private InstanceFeignClient instanceFeignClient;
    private ClientService clientService;

    public StateMachineClient(InstanceFeignClient instanceFeignClient, ClientService clientService) {
        this.instanceFeignClient = instanceFeignClient;
        this.clientService = clientService;
    }

    /**
     * 通过状态机客户端创建实例，不走状态机服务端流程，避免事务问题和过多的实例产生
     *
     * @param organizationId
     * @param stateMachineId
     * @param inputDTO
     */
    public void createInstance(Long organizationId, Long stateMachineId, InputDTO inputDTO) {
        //获取状态机的初始转换
        StateMachineTransformDTO transformDTO = instanceFeignClient.queryInitTransform(organizationId, stateMachineId).getBody();
        //获取初始状态
        Long statusId = transformDTO.getEndStatusId();
        //执行条件
        List<StateMachineConfigDTO> conditions = transformDTO.getConditions();
        if (conditions != null && conditions.isEmpty()) {
            inputDTO.setConfigs(conditions);
            ExecuteResult result = clientService.configExecuteCondition(statusId, transformDTO.getConditionStrategy(), inputDTO);
            if (!result.getSuccess()) {
                throw new CommonException("error.stateMachineClient.createInstance.condition.fail");
            }
        }
        //执行验证
        List<StateMachineConfigDTO> validators = transformDTO.getValidators();
        if (validators != null && validators.isEmpty()) {
            inputDTO.setConfigs(validators);
            ExecuteResult result = clientService.configExecuteValidator(statusId, inputDTO);
            if (!result.getSuccess()) {
                throw new CommonException("error.stateMachineClient.createInstance.validator.fail");
            }
        }
        //执行后置动作
        List<StateMachineConfigDTO> postpositions = transformDTO.getPostpositions();
        if (postpositions != null && postpositions.isEmpty()) {
            inputDTO.setConfigs(postpositions);
            ExecuteResult result = clientService.configExecutePostAction(statusId, transformDTO.getType(), inputDTO);
            if (!result.getSuccess()) {
                throw new CommonException("error.stateMachineClient.createInstance.action.fail");
            }
        }

    }
}
