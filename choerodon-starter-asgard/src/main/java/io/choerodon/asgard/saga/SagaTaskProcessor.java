package io.choerodon.asgard.saga;

import io.choerodon.asgard.SagaApplicationContextHelper;
import io.choerodon.core.saga.SagaTask;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

import static io.choerodon.asgard.saga.SagaMonitor.invokeBeanMap;

public class SagaTaskProcessor implements BeanPostProcessor {

    private SagaApplicationContextHelper applicationContextHelper;

    public SagaTaskProcessor(SagaApplicationContextHelper applicationContextHelper) {
        this.applicationContextHelper = applicationContextHelper;
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
                    if (invokeBeanMap.entrySet().stream().noneMatch(t -> t.getValue().key.equals(key))) {
                        Object object = applicationContextHelper.getSpringFactory().getBean(method.getDeclaringClass());
                        invokeBeanMap.put(key, new SagaTaskInvokeBean(method, object, sagaTask, key));
                    }
                }
            }
        }
        return bean;
    }


}
