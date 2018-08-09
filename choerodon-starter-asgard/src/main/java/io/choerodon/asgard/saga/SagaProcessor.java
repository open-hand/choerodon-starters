package io.choerodon.asgard.saga;

import io.choerodon.asgard.saga.exception.SagaTaskCodeNotUniqueError;
import io.choerodon.asgard.saga.exception.SagaTaskInstanceTableNotExistError;
import io.choerodon.asgard.saga.exception.SagaTaskMethodParameterError;
import io.choerodon.asgard.saga.annotation.SagaTask;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

import static io.choerodon.asgard.saga.SagaMonitor.invokeBeanMap;

public class SagaProcessor implements BeanPostProcessor {

    private final SagaApplicationContextHelper applicationContextHelper;

    private final SagaTaskInstanceStore taskInstanceStore;

    public SagaProcessor(SagaApplicationContextHelper applicationContextHelper,
                         SagaTaskInstanceStore taskInstanceStore) {
        this.applicationContextHelper = applicationContextHelper;
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
                    Object object = applicationContextHelper.getSpringFactory().getBean(method.getDeclaringClass());
                    invokeBeanMap.put(key, new SagaTaskInvokeBean(method, object, sagaTask, key));
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
