package io.choerodon.asgard.schedule;

import io.choerodon.asgard.schedule.annotation.JobTask;
import io.choerodon.asgard.schedule.exception.InvalidJobTaskMethodException;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

public class JobTaskProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String s) {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String s) {
        Method[] methods = ReflectionUtils.getAllDeclaredMethods(bean.getClass());
        if (methods != null) {
            for (Method method : methods) {
                JobTask jobTask = AnnotationUtils.findAnnotation(method, JobTask.class);
                if (jobTask != null) {
                    if (!validParam(method, method.getGenericParameterTypes()[0])) {
                        throw new InvalidJobTaskMethodException(method);
                    }
                    ScheduleConsumer.addInvokeBean(jobTask.code(), new JobTaskInvokeBean(method, bean, jobTask));
                }
            }
        }
        return bean;
    }

    private boolean validParam(final Method method, final Type param) {
        if (param instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) param;
            if (parameterizedType.getRawType().equals(Map.class)) {
                Type[] args = parameterizedType.getActualTypeArguments();
                Class<?> returnType = method.getReturnType();
                return args[0].equals(String.class) && args[1].equals(Object.class)
                        && (returnType.equals(void.class) || returnType.equals(Map.class));
            }
        }
        return false;
    }
}
