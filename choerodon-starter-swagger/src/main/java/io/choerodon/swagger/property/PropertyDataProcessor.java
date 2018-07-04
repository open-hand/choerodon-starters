package io.choerodon.swagger.property;

import io.choerodon.core.saga.Saga;
import io.choerodon.core.saga.SagaTask;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

public class PropertyDataProcessor implements BeanPostProcessor {

    private PropertyData propertyData;

    public PropertyDataProcessor(PropertyData propertyData) {
        this.propertyData = propertyData;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {


        Method[] methods = ReflectionUtils.getAllDeclaredMethods(bean.getClass());
        if (methods != null) {
            for (Method method : methods) {
                Saga saga = AnnotationUtils.findAnnotation(method, Saga.class);
                if (saga != null) {
                    propertyData.addSaga(new PropertyData.Saga(saga.code(), saga.description(), saga.inputKeys(), saga.outputKeys()));
                }
                SagaTask sagaTask = AnnotationUtils.findAnnotation(method, SagaTask.class);
                if (sagaTask != null) {
                    propertyData.addSagaTask(new PropertyData.SagaTask(sagaTask.code(), sagaTask.description(), sagaTask.sagaCode(),
                            sagaTask.seq(), sagaTask.concurrentExecLimit(), sagaTask.maxRetryCount()));
                }
            }
        }
        return bean;
    }

}
