package io.choerodon.asgard.saga.consumer;

import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.asgard.saga.exception.SagaTaskCodeUniqueException;
import io.choerodon.asgard.saga.exception.SagaTaskMethodParameterException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

import static io.choerodon.asgard.saga.consumer.SagaConsumer.invokeBeanMap;

public class SagaTaskProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        Method[] methods = ReflectionUtils.getAllDeclaredMethods(bean.getClass());
        if (methods != null) {
            for (Method method : methods) {
                SagaTask sagaTask = AnnotationUtils.getAnnotation(method, SagaTask.class);
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
            throw new SagaTaskMethodParameterException(method);
        }
        if (invokeBeanMap.entrySet().stream().anyMatch(t -> t.getValue().key.equals(key))) {
            throw new SagaTaskCodeUniqueException(sagaTask);
        }
    }
}
