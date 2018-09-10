package io.choerodon.asgard.schedule;

import io.choerodon.asgard.schedule.annotation.JobTask;
import io.choerodon.asgard.schedule.exception.InvalidJobTaskMethodException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

public class JobTaskProcessor implements BeanPostProcessor {

    private final String service;

    public JobTaskProcessor(String service) {
        this.service = service;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String s) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String s) throws BeansException {
        Method[] methods = ReflectionUtils.getAllDeclaredMethods(bean.getClass());
        if (methods != null) {
            for (Method method : methods) {
                JobTask jobTask = AnnotationUtils.findAnnotation(method, JobTask.class);
                if (jobTask != null) {
                    boolean validParam = false;
                    Type param = method.getGenericParameterTypes()[0];
                    if (param instanceof ParameterizedType) {
                        ParameterizedType parameterizedType = (ParameterizedType)param;
                        if (parameterizedType.getRawType().equals(Map.class) ) {
                            Type[] args = parameterizedType.getActualTypeArguments();
                            Class<?> returnType = method.getReturnType();
                            if (args[0].equals(String.class) && args[1].equals(Object.class)
                                    && (returnType.equals(void.class) || returnType.equals(Map.class))) {
                                validParam = true;
                            }
                        }
                    }
                    if (!validParam) {
                        throw new InvalidJobTaskMethodException(method);
                    }
                    String key = service + "." + bean.getClass().getName() + "." + method.getName() + "()";
                    ScheduleMonitor.addInvokeBean(key, new JobTaskInvokeBean(method, bean, jobTask, key));
                }
            }
        }
        return bean;
    }
}
