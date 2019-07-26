package io.choerodon.statemachine.service;

import io.choerodon.statemachine.dto.ExecuteResult;
import io.choerodon.statemachine.dto.InputDTO;
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
     * @param targetStatusId
     * @param conditionStrategy
     * @param inputDTO
     * @return
     */
    ExecuteResult configExecuteCondition(Long targetStatusId, String conditionStrategy, InputDTO inputDTO);

    /**
     * 执行验证
     *
     * @param targetStatusId
     * @param inputDTO
     * @return
     */
    ExecuteResult configExecuteValidator(Long targetStatusId, InputDTO inputDTO);

    /**
     * 执行后置动作，单独出来，才能生效回归
     *
     * @param targetStatusId
     * @param transformType
     * @param inputDTO
     * @return
     */
    ExecuteResult configExecutePostAction(Long targetStatusId, String transformType, InputDTO inputDTO);
}
