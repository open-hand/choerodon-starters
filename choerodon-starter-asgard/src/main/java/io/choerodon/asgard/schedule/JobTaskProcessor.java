package io.choerodon.asgard.schedule;

import io.choerodon.asgard.schedule.annotation.JobTask;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

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
                    String key = service + "." + bean.getClass().getName() + "." + method.getName() + "()";
                    ScheduleMonitor.addInvokeBean(key, new JobTaskInvokeBean(method, bean, jobTask, key));
                }
            }
        }
        return bean;
    }
}
