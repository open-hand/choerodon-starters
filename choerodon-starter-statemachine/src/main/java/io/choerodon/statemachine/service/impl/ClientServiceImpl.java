package io.choerodon.statemachine.service.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.statemachine.StateMachineConfigMonitor;
import io.choerodon.statemachine.dto.ExecuteResult;
import io.choerodon.statemachine.dto.InvokeBean;
import io.choerodon.statemachine.dto.StateMachineConfigDTO;
import io.choerodon.statemachine.dto.TransformInfo;
import io.choerodon.statemachine.enums.StateMachineConfigType;
import io.choerodon.statemachine.enums.TransformConditionStrategy;
import io.choerodon.statemachine.enums.TransformType;
import io.choerodon.statemachine.service.ClientService;

/**
 * @author shinan.chen
 * @author dinghuang123@gmail.com
 * @since 2018/10/11
 */
public class ClientServiceImpl implements ClientService {

    private static final Logger logger = LoggerFactory.getLogger(ClientServiceImpl.class);

    private static final String CONFIGURE_TYPE_CONDITION = "配置类型【条件】:";
    private static final String CONFIGURE_TYPE_VALIDATION = "配置类型【验证】:";
    private static final String CONFIGURE_TYPE_POST_ACTION = "配置类型【后置动作】:";
    private static final String CONDITION_ALL_MATCH = CONFIGURE_TYPE_CONDITION + "条件全部符合";
    private static final String CONDITION_NOT_MATCH = CONFIGURE_TYPE_CONDITION + "没有符合的条件类型";
    private static final String NO_PASS = "执行不通过";
    private static final String PASS = "执行通过";
    private static final String UPDATE_STATUS_FAIL = "状态更新失败";

    /**
     * 根据条件过滤转换
     *
     * @param instanceId    instanceId
     * @param transformDTOS transformDTOS
     * @return List
     */
    @Override
    public List<TransformInfo> conditionFilter(Long instanceId, List<TransformInfo> transformDTOS) {
        logger.info("stateMachine client conditionFilter start: instanceId:{}, transformInfos:{}", instanceId, transformDTOS);
        if (transformDTOS == null || transformDTOS.isEmpty()) {
            return Collections.emptyList();
        }
        List<TransformInfo> resultTransforms = new ArrayList<>();
        transformDTOS.forEach(transformInfo -> {
            List<StateMachineConfigDTO> configDTOS = transformInfo.getConditions();
            ExecuteResult result = configExecuteCondition(instanceId, transformInfo.getEndStatusId(), transformInfo.getConditionStrategy(), configDTOS);
            if (result.getSuccess()) {
                logger.info("stateMachine client conditionFilter transform match condition: instanceId:{}, transformId:{}", instanceId, transformInfo.getId());
                resultTransforms.add(transformInfo);
            } else {
                logger.info("stateMachine client conditionFilter transform not match condition: instanceId:{}, transformId:{}", instanceId, transformInfo.getId());
            }
        });
        return resultTransforms;
    }

    /**
     * 执行条件
     *
     * @param instanceId        instanceId
     * @param targetStatusId    targetStatusId
     * @param conditionStrategy conditionStrategy
     * @param configDTOS        configDTOS
     * @return ExecuteResult
     */
    @Override
    public ExecuteResult configExecuteCondition(Long instanceId, Long targetStatusId, String conditionStrategy, List<StateMachineConfigDTO> configDTOS) {
        logger.info("stateMachine client configExecuteCondition start: instanceId:{}, configDTOS:{}", instanceId, configDTOS);
        ExecuteResult executeResult = new ExecuteResult();
        Boolean isSuccess = true;
        //执行代码中配置的条件
        for (StateMachineConfigDTO configDTO : configDTOS) {
            isSuccess = methodInvokeBean(StateMachineConfigType.CONDITION, configDTO, instanceId);
            //根据不同的条件策略返回不同结果
            if (conditionStrategy.equals(TransformConditionStrategy.ALL)) {
                if (!isSuccess) {
                    executeResult.setErrorMessage(CONFIGURE_TYPE_CONDITION + configDTO.getCode() + NO_PASS);
                    break;
                } else {
                    executeResult.setErrorMessage(CONDITION_ALL_MATCH);
                }
            } else {
                if (isSuccess) {
                    executeResult.setErrorMessage(CONFIGURE_TYPE_CONDITION + configDTO.getCode() + PASS);
                    break;
                } else {
                    executeResult.setErrorMessage(CONDITION_NOT_MATCH);
                }
            }
        }
        executeResult.setSuccess(isSuccess);
        return executeResult;
    }

    /**
     * 执行验证
     *
     * @param instanceId     instanceId
     * @param targetStatusId targetStatusId
     * @param configDTOS     configDTOS
     * @return ExecuteResult
     */
    @Override
    public ExecuteResult configExecuteValidator(Long instanceId, Long targetStatusId, List<StateMachineConfigDTO> configDTOS) {
        logger.info("stateMachine client configExecuteValidator start: instanceId:{}, configDTOS:{}", instanceId, configDTOS);
        ExecuteResult executeResult = new ExecuteResult();
        Boolean isSuccess = true;
        //执行代码中配置的验证
        for (StateMachineConfigDTO configDTO : configDTOS) {
            isSuccess = methodInvokeBean(StateMachineConfigType.VALIDATOR, configDTO, instanceId);
            if (!isSuccess) {
                executeResult.setErrorMessage(CONFIGURE_TYPE_VALIDATION + configDTO.getCode() + NO_PASS);
                break;
            }
        }
        executeResult.setSuccess(isSuccess);
        return executeResult;
    }

    /**
     * 执行后置动作
     *
     * @param instanceId     instanceId
     * @param targetStatusId targetStatusId
     * @param configDTOS     configDTOS
     * @param transformType  transformType
     * @return ExecuteResult
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExecuteResult configExecutePostAction(Long instanceId, Long targetStatusId, String transformType, List<StateMachineConfigDTO> configDTOS) {
        logger.info("stateMachine client configExecutePostAction start: instanceId:{}, configDTOS:{}", instanceId, configDTOS);
        ExecuteResult executeResult = new ExecuteResult();
        Boolean isSuccess = true;
        //执行后置动作，若是初始转换：反射startInstance，若是其他转换：反射updateStatus
        if (transformType.equals(TransformType.INIT)) {
            isSuccess = startInstanceInvokeBean(instanceId, targetStatusId);
        } else {
            isSuccess = updateStatusInvokeBean(instanceId, targetStatusId);
        }

        //执行代码中配置的后置动作
        if (isSuccess) {
            for (StateMachineConfigDTO configDTO : configDTOS) {
                isSuccess = methodInvokeBean(StateMachineConfigType.POSTPOSITION, configDTO, instanceId);
                if (!isSuccess) {
                    executeResult.setErrorMessage(CONFIGURE_TYPE_POST_ACTION + configDTO.getCode() + NO_PASS);
                    break;
                }
            }
        } else {
            executeResult.setErrorMessage(UPDATE_STATUS_FAIL);
        }
        executeResult.setSuccess(isSuccess);
        executeResult.setResultStatusId(targetStatusId);
        return executeResult;
    }

    /**
     * 执行配置的config方法
     *
     * @param type type
     * @param configDTO  configDTO
     * @param instanceId instanceId
     * @return Boolean
     */
    private Boolean methodInvokeBean(String type, StateMachineConfigDTO configDTO, Long instanceId) {
        Boolean isSuccess = true;
        InvokeBean invokeBean = StateMachineConfigMonitor.invokeBeanMap.get(configDTO.getCode());
        if (invokeBean != null) {
            Object object = invokeBean.getObject();
            Method method = invokeBean.getMethod();
            try {
                if (type.equals(StateMachineConfigType.POSTPOSITION)) {
                    method.invoke(object, instanceId, configDTO);
                } else {
                    isSuccess = (Boolean) method.invoke(object, instanceId, configDTO);
                }
                logger.info("stateMachine client {} {} with method {}: instanceId:{}, result:{}", type, configDTO.getCode(), method.getName(), instanceId, isSuccess);
            } catch (Exception e) {
                logger.error("stateMachine client {} {} with method {} invoke error {}", type, configDTO.getCode(), method.getName(), e);
                isSuccess = false;
            }
        } else {
            logger.error("stateMachine client {} {} invokeBean not found", type, configDTO.getCode());
            isSuccess = false;
        }
        return isSuccess;
    }

    /**
     * 执行转换更新状态方法
     *
     * @param instanceId     instanceId
     * @param targetStatusId targetStatusId
     * @return Boolean
     */
    private Boolean updateStatusInvokeBean(Long instanceId, Long targetStatusId) {
        InvokeBean updateInvokeBean = StateMachineConfigMonitor.updateStatusBean;
        if (updateInvokeBean != null) {
            Object object = updateInvokeBean.getObject();
            Method method = updateInvokeBean.getMethod();
            try {
                method.invoke(object, instanceId, targetStatusId);
                logger.info("stateMachine client configExecute updateStatus with method {}: instanceId:{}, targetStatusId:{}", method.getName(), instanceId, targetStatusId);
            } catch (Exception e) {
                logger.error("stateMachine client configExecute updateStatus invoke error {}", e);
                return false;
            }
        } else {
            logger.error("stateMachine client configExecute updateStatus invokeBean not found");
            return false;
        }
        return true;
    }

    /**
     * 执行创建实例初始化方法
     *
     * @param instanceId     instanceId
     * @param targetStatusId targetStatusId
     * @return Boolean
     */
    private Boolean startInstanceInvokeBean(Long instanceId, Long targetStatusId) {
        InvokeBean startInstanceBean = StateMachineConfigMonitor.startInstanceBean;
        if (startInstanceBean != null) {
            Object object = startInstanceBean.getObject();
            Method method = startInstanceBean.getMethod();
            try {
                method.invoke(object, instanceId, targetStatusId);
                logger.info("stateMachine client configExecute startInstance with method {}: instanceId:{}, targetStatusId:{}", method.getName(), instanceId, targetStatusId);
            } catch (Exception e) {
                logger.error("stateMachine client configExecute startInstance invoke error {}", e);
                return false;
            }
        }
        return true;
    }
}
