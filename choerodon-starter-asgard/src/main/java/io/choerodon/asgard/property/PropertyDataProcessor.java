package io.choerodon.asgard.property;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.choerodon.asgard.saga.SagaDefinition;
import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.asgard.saga.consumer.GenerateJsonSchemaUtil;
import io.choerodon.asgard.schedule.annotation.JobTask;
import io.choerodon.asgard.schedule.annotation.TimedTask;

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
            PropertySaga data = new PropertySaga(typeSaga.code(), typeSaga.description());
            addInputSchema(typeSaga, data);
            propertyData.addSaga(data);
        }
        Method[] methods = ReflectionUtils.getAllDeclaredMethods(bean.getClass());
        if (methods != null) {
            for (Method method : methods) {
                addMethodSaga(method);
                addMethodSagaTask(method);
                addMethodJobTask(bean, method);
            }
        }
        return bean;
    }

    private void addMethodSaga(final Method method) {
        Saga saga = AnnotationUtils.findAnnotation(method, Saga.class);
        if (saga != null) {
            PropertySaga data = new PropertySaga(saga.code(), saga.description());
            addInputSchema(saga, data);
            propertyData.addSaga(data);
        }
    }

    private void addMethodSagaTask(final Method method) {
        SagaTask sagaTask = AnnotationUtils.findAnnotation(method, SagaTask.class);
        if (sagaTask != null) {
            PropertySagaTask task = new PropertySagaTask(sagaTask.code(), sagaTask.description(),
                    sagaTask.sagaCode(), sagaTask.seq(), sagaTask.maxRetryCount());
            task.setConcurrentLimitNum(sagaTask.concurrentLimitNum());
            task.setConcurrentLimitPolicy(sagaTask.concurrentLimitPolicy().name());
            task.setTimeoutPolicy(sagaTask.timeoutPolicy().name());
            task.setTimeoutSeconds(sagaTask.timeoutSeconds());
            addOutputSchema(sagaTask, method, task);
            propertyData.addSagaTask(task);
        }
    }

    private void addMethodJobTask(final Object bean, final Method method) {
        JobTask jobTask = AnnotationUtils.findAnnotation(method, JobTask.class);
        if (jobTask != null) {
            String methodName = bean.getClass().getName() + "." + method.getName();
            propertyData.addJobTask(new PropertyJobTask(methodName, jobTask.maxRetryCount(), jobTask.code(), jobTask.description(), jobTask.level(), jobTask.params()));
            TimedTask timedTask = AnnotationUtils.findAnnotation(method, TimedTask.class);
            if (timedTask != null) {
                propertyData.addTimedTasks(new PropertyTimedTask(timedTask, jobTask));
            }
        }
    }

    private void addOutputSchema(final SagaTask sagaTask, final Method method, final PropertySagaTask data) {
        if (!StringUtils.isEmpty(sagaTask.outputSchema())) {
            data.setOutputSchema(sagaTask.outputSchema());
            data.setOutputSchemaSource(SagaDefinition.SagaTaskOutputSchemaSource.OUTPUT_SCHEMA.name());
        } else if (!sagaTask.outputSchemaClass().equals(Object.class)) {
            data.setOutputSchema(GenerateJsonSchemaUtil.generate(sagaTask.outputSchemaClass(), mapper, true));
            data.setOutputSchemaSource(SagaDefinition.SagaTaskOutputSchemaSource.OUTPUT_SCHEMA_CLASS.name());
        } else {
            data.setOutputSchema(GenerateJsonSchemaUtil.generate(method.getReturnType(), mapper, true));
            data.setOutputSchemaSource(SagaDefinition.SagaTaskOutputSchemaSource.METHOD_RETURN_TYPE.name());
        }
    }

    private void addInputSchema(final Saga saga, final PropertySaga data) {
        if (!StringUtils.isEmpty(saga.inputSchema())) {
            data.setInputSchema(saga.inputSchema());
            data.setInputSchemaSource(SagaDefinition.SagaInputSchemaSource.INPUT_SCHEMA.name());
        } else if (!saga.inputSchemaClass().equals(Object.class)) {
            data.setInputSchema(GenerateJsonSchemaUtil.generate(saga.inputSchemaClass(), mapper, false));
            data.setInputSchemaSource(SagaDefinition.SagaInputSchemaSource.INPUT_SCHEMA_CLASS.name());
        } else {
            data.setInputSchema("");
            data.setInputSchemaSource(SagaDefinition.SagaInputSchemaSource.NONE.name());
        }
    }

}
