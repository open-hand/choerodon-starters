package io.choerodon.swagger.property;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.saga.GenerateJsonExampleUtil;
import io.choerodon.core.saga.Saga;
import io.choerodon.core.saga.SagaTask;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

public class PropertyDataProcessor implements BeanPostProcessor {

    private final ObjectMapper mapper = new ObjectMapper();

    private PropertyData propertyData;

    public PropertyDataProcessor(PropertyData propertyData) {
        this.propertyData = propertyData;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {

        Saga typeSaga = AnnotationUtils.findAnnotation(bean.getClass(), Saga.class);
        if (typeSaga != null) {
            propertyData.addSaga(new PropertyData.Saga(typeSaga.code(), typeSaga.description(), calculateInputSchema(typeSaga)));
        }
        Method[] methods = ReflectionUtils.getAllDeclaredMethods(bean.getClass());
        if (methods != null) {
            for (Method method : methods) {
                Saga saga = AnnotationUtils.findAnnotation(method, Saga.class);
                if (saga != null) {
                    propertyData.addSaga(new PropertyData.Saga(saga.code(), saga.description(), calculateInputSchema(saga)));
                }
                SagaTask sagaTask = AnnotationUtils.findAnnotation(method, SagaTask.class);
                if (sagaTask != null) {
                    PropertyData.SagaTask task = new PropertyData.SagaTask(sagaTask.code(), sagaTask.description(),
                            sagaTask.sagaCode(), sagaTask.seq(), sagaTask.maxRetryCount());
                    task.setConcurrentLimitNum(sagaTask.concurrentLimitNum());
                    task.setConcurrentLimitPolicy(sagaTask.concurrentLimitPolicy().name());
                    task.setTimeoutPolicy(sagaTask.timeoutPolicy().name());
                    task.setTimeoutSeconds(sagaTask.timeoutSeconds());
                    String outputSchema = GenerateJsonExampleUtil.generate(method.getReturnType(), mapper, true);
                    if (!StringUtils.isEmpty(outputSchema)) {
                        task.setOutputSchema(outputSchema);
                    }
                    propertyData.addSagaTask(task);
                }
            }
        }
        return bean;
    }


    private String calculateInputSchema(final Saga saga) {
        if (!StringUtils.isEmpty(saga.inputSchema())) {
            return saga.inputSchema();
        }
        if (!saga.inputSchemaClass().equals(Object.class)) {
            return GenerateJsonExampleUtil.generate(saga.inputSchemaClass(), mapper, false);
        }
        return "";
    }

}
