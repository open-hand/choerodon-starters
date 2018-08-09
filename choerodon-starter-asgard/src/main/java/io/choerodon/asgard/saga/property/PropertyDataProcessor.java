package io.choerodon.asgard.saga.property;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.asgard.saga.GenerateJsonExampleUtil;
import io.choerodon.asgard.saga.SagaDefinition;
import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.annotation.SagaTask;
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
            PropertyData.Saga data = new PropertyData.Saga(typeSaga.code(), typeSaga.description());
            addInputSchema(typeSaga, data);
            propertyData.addSaga(data);
        }
        Method[] methods = ReflectionUtils.getAllDeclaredMethods(bean.getClass());
        if (methods != null) {
            for (Method method : methods) {
                Saga saga = AnnotationUtils.findAnnotation(method, Saga.class);
                if (saga != null) {
                    PropertyData.Saga data = new PropertyData.Saga(saga.code(), saga.description());
                    addInputSchema(saga, data);
                    propertyData.addSaga(data);
                }
                SagaTask sagaTask = AnnotationUtils.findAnnotation(method, SagaTask.class);
                if (sagaTask != null) {
                    PropertyData.SagaTask task = new PropertyData.SagaTask(sagaTask.code(), sagaTask.description(),
                            sagaTask.sagaCode(), sagaTask.seq(), sagaTask.maxRetryCount());
                    task.setConcurrentLimitNum(sagaTask.concurrentLimitNum());
                    task.setConcurrentLimitPolicy(sagaTask.concurrentLimitPolicy().name());
                    task.setTimeoutPolicy(sagaTask.timeoutPolicy().name());
                    task.setTimeoutSeconds(sagaTask.timeoutSeconds());
                    addOutputSchema(sagaTask, method, task);
                    propertyData.addSagaTask(task);
                }
            }
        }
        return bean;
    }


    private void addOutputSchema(final SagaTask sagaTask, final Method method, final PropertyData.SagaTask data) {
        if (!StringUtils.isEmpty(sagaTask.outputSchema())) {
            data.setOutputSchema(sagaTask.outputSchema());
            data.setOutputSchemaSource(SagaDefinition.SagaTaskOutputSchemaSource.OUTPUT_SCHEMA.name());
        }else if (!sagaTask.outputSchemaClass().equals(Object.class)) {
            data.setOutputSchema(GenerateJsonExampleUtil.generate(sagaTask.outputSchemaClass(), mapper, true));
            data.setOutputSchemaSource(SagaDefinition.SagaTaskOutputSchemaSource.OUTPUT_SCHEMA_CLASS.name());
        }else {
            data.setOutputSchema(GenerateJsonExampleUtil.generate(method.getReturnType(), mapper, true));
            data.setOutputSchemaSource(SagaDefinition.SagaTaskOutputSchemaSource.METHOD_RETURN_TYPE.name());
        }
    }

    private void addInputSchema(final Saga saga, final PropertyData.Saga data) {
        if (!StringUtils.isEmpty(saga.inputSchema())) {
            data.setInputSchema(saga.inputSchema());
            data.setInputSchemaSource(SagaDefinition.SagaInputSchemaSource.INPUT_SCHEMA.name());
        } else if (!saga.inputSchemaClass().equals(Object.class)) {
            data.setInputSchema(GenerateJsonExampleUtil.generate(saga.inputSchemaClass(), mapper, false));
            data.setInputSchemaSource(SagaDefinition.SagaInputSchemaSource.INPUT_SCHEMA_CLASS.name());
        } else {
            data.setInputSchema("");
            data.setInputSchemaSource(SagaDefinition.SagaInputSchemaSource.NONE.name());
        }
    }

}
