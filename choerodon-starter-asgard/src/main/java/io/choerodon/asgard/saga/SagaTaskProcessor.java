package io.choerodon.asgard.saga;

import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.asgard.saga.exception.SagaTaskCodeNotUniqueError;
import io.choerodon.asgard.saga.exception.SagaTaskInstanceTableNotExistError;
import io.choerodon.asgard.saga.exception.SagaTaskMethodParameterError;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

import static io.choerodon.asgard.saga.SagaMonitor.invokeBeanMap;

public class SagaTaskProcessor implements BeanPostProcessor {

    private final SagaTaskInstanceStore taskInstanceStore;

    public SagaTaskProcessor(SagaTaskInstanceStore taskInstanceStore) {
        this.taskInstanceStore = taskInstanceStore;
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        Method[] methods = ReflectionUtils.getAllDeclaredMethods(bean.getClass());
        if (methods != null) {
            for (Method method : methods) {
                SagaTask sagaTask = AnnotationUtils.findAnnotation(method, SagaTask.class);
                if (sagaTask != null) {
                    String key = sagaTask.sagaCode() + sagaTask.code();
                    errorCheck(method, sagaTask, key);
                    invokeBeanMap.put(key, new SagaTaskInvokeBean(method, bean, sagaTask, key));
                }
            }
        }
        return bean;
    }

    private void errorCheck(final Method method, final SagaTask sagaTask, final String key) {
        if (method.getParameterCount() != 1 || !method.getParameterTypes()[0].equals(String.class)) {
            throw new SagaTaskMethodParameterError(method);
        }
        if (invokeBeanMap.entrySet().stream().anyMatch(t -> t.getValue().key.equals(key))) {
            throw new SagaTaskCodeNotUniqueError(sagaTask);
        }
        if (sagaTask.enabledDbRecord()) {
            SagaMonitor.setEnabledDbRecordTrue();
            if (taskInstanceStore.tableNotExist()) {
                throw new SagaTaskInstanceTableNotExistError(sagaTask);
            }
        }
    }
}
