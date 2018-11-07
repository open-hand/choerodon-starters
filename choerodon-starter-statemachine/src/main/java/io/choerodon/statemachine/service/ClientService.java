package io.choerodon.statemachine.service;

import io.choerodon.statemachine.dto.ExecuteResult;
import io.choerodon.statemachine.dto.StateMachineConfigDTO;
import io.choerodon.statemachine.dto.TransformInfo;

import java.util.List;

/**
 * 状态机客户端回调service
 *
 * @author shinan.chen
 * @author dinghuang123@gmail.com
 * @since 2018/10/11
 */
public interface ClientService {

    /**
     * 根据条件过滤转换
     *
     * @param instanceId     instanceId
     * @param transformInfos transformInfos
     * @return TransformInfo
     */
    List<TransformInfo> conditionFilter(Long instanceId, List<TransformInfo> transformInfos);


    /**
     * 执行条件
     *
     * @param instanceId        instanceId
     * @param targetStatusId    targetStatusId
     * @param conditionStrategy conditionStrategy
     * @param configDTOS        configDTOS
     * @return ExecuteResult
     */
    ExecuteResult configExecuteCondition(Long instanceId, Long targetStatusId, String conditionStrategy, List<StateMachineConfigDTO> configDTOS);

    /**
     * 执行验证
     *
     * @param instanceId     instanceId
     * @param targetStatusId targetStatusId
     * @param configDTOS     configDTOS
     * @return ExecuteResult
     */
    ExecuteResult configExecuteValidator(Long instanceId, Long targetStatusId, List<StateMachineConfigDTO> configDTOS);

    /**
     * 执行后置动作，单独出来，才能生效回归
     *
     * @param instanceId     instanceId
     * @param targetStatusId targetStatusId
     * @param configDTOS     configDTOS
     * @return ExecuteResult
     */
    ExecuteResult configExecutePostAction(Long instanceId, Long targetStatusId, String transformType, List<StateMachineConfigDTO> configDTOS);
}
