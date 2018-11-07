package io.choerodon.statemachine;

import io.choerodon.statemachine.annotation.*;
import io.choerodon.statemachine.dto.ConfigCodeDTO;
import io.choerodon.statemachine.dto.InvokeBean;
import io.choerodon.statemachine.dto.PropertyData;
import io.choerodon.statemachine.enums.StateMachineConfigType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * @author shinan.chen
 * @author dinghuang123@gmail.com
 * @since 2018/10/9
 */
public class ClientProcessor implements BeanPostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientProcessor.class);

    private StateMachineApplicationContextHelper stateMachineApplicationContextHelper;

    private PropertyData stateMachinePropertyData;

    public ClientProcessor(StateMachineApplicationContextHelper stateMachineApplicationContextHelper, PropertyData stateMachinePropertyData) {
        this.stateMachineApplicationContextHelper = stateMachineApplicationContextHelper;
        this.stateMachinePropertyData = stateMachinePropertyData;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        Method[] methods = ReflectionUtils.getAllDeclaredMethods(bean.getClass());
        for (Method method : methods) {
            Condition condition = AnnotationUtils.getAnnotation(method, Condition.class);
            if (condition != null) {
                LOGGER.info("stateMachine client annotation condition:{}", condition);
                ConfigCodeDTO configCodeDTO = new ConfigCodeDTO(condition.code(), condition.name(), condition.description(), StateMachineConfigType.CONDITION);
                stateMachinePropertyData.getList().add(configCodeDTO);
                Object object = stateMachineApplicationContextHelper.getContext().getBean(method.getDeclaringClass());
                StateMachineConfigMonitor.checkUniqueCode(configCodeDTO);
                StateMachineConfigMonitor.invokeBeanMap.put(condition.code(), new InvokeBean(method, object, configCodeDTO));
            }
            PostAction postAction = AnnotationUtils.getAnnotation(method, PostAction.class);
            if (postAction != null) {
                LOGGER.info("stateMachine client annotation postAction:{}", postAction);
                ConfigCodeDTO configCodeDTO = new ConfigCodeDTO(postAction.code(), postAction.name(), postAction.description(), StateMachineConfigType.POSTPOSITION);
                stateMachinePropertyData.getList().add(configCodeDTO);
                Object object = stateMachineApplicationContextHelper.getContext().getBean(method.getDeclaringClass());
                StateMachineConfigMonitor.checkUniqueCode(configCodeDTO);
                StateMachineConfigMonitor.invokeBeanMap.put(postAction.code(), new InvokeBean(method, object, configCodeDTO));
            }
            Validator validator = AnnotationUtils.getAnnotation(method, Validator.class);
            if (validator != null) {
                LOGGER.info("stateMachine client annotation validator:{}", validator);
                ConfigCodeDTO configCodeDTO = new ConfigCodeDTO(validator.code(), validator.name(), validator.description(), StateMachineConfigType.VALIDATOR);
                stateMachinePropertyData.getList().add(configCodeDTO);
                Object object = stateMachineApplicationContextHelper.getContext().getBean(method.getDeclaringClass());
                StateMachineConfigMonitor.checkUniqueCode(configCodeDTO);
                StateMachineConfigMonitor.invokeBeanMap.put(validator.code(), new InvokeBean(method, object, configCodeDTO));
            }
            Trigger trigger = AnnotationUtils.getAnnotation(method, Trigger.class);
            if (trigger != null) {
                LOGGER.info("stateMachine client annotation trigger:{}", trigger);
                ConfigCodeDTO configCodeDTO = new ConfigCodeDTO(trigger.code(), trigger.name(), trigger.description(), StateMachineConfigType.TRIGGER);
                stateMachinePropertyData.getList().add(configCodeDTO);
                Object object = stateMachineApplicationContextHelper.getContext().getBean(method.getDeclaringClass());
                StateMachineConfigMonitor.checkUniqueCode(configCodeDTO);
                StateMachineConfigMonitor.invokeBeanMap.put(trigger.code(), new InvokeBean(method, object, configCodeDTO));
            }
            //扫描UpdateStatus注解的方法
            UpdateStatus updateStatus = AnnotationUtils.getAnnotation(method, UpdateStatus.class);
            if (updateStatus != null) {
                LOGGER.info("stateMachine client annotation updateStatus:{}", updateStatus);
                Object object = stateMachineApplicationContextHelper.getContext().getBean(method.getDeclaringClass());
                StateMachineConfigMonitor.setUpdateStatusBean(new InvokeBean(method, object, null));
            }
            //扫描StartInstance注解的方法
            StartInstance startInstance = AnnotationUtils.getAnnotation(method, StartInstance.class);
            if (startInstance != null) {
                LOGGER.info("stateMachine client annotation startInstance:{}", startInstance);
                Object object = stateMachineApplicationContextHelper.getContext().getBean(method.getDeclaringClass());
                StateMachineConfigMonitor.setStartInstanceBean(new InvokeBean(method, object, null));
            }
        }
        return bean;
    }
}
