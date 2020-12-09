package io.choerodon.asgard.saga.consumer;

import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.asgard.saga.exception.SagaTaskCodeUniqueException;
import io.choerodon.asgard.saga.exception.SagaTaskMethodParameterException;
import io.choerodon.core.exception.CommonException;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.choerodon.asgard.saga.consumer.SagaConsumer.invokeBeanMap;

public class SagaTaskProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        Method[] methods = ReflectionUtils.getAllDeclaredMethods(bean.getClass());
        for (Method method : methods) {
            SagaTask sagaTask = AnnotationUtils.getAnnotation(method, SagaTask.class);
            if (sagaTask != null) {
                String key = sagaTask.sagaCode() + sagaTask.code();
                errorCheck(method, sagaTask, key);

                // 添加失败回调
                Method failureCallbackMethod = null;
                Class<?> clazz = null;
                if (!StringUtils.isEmpty(sagaTask.failureCallbackMethod())) {
                    String str = sagaTask.failureCallbackMethod().replace("()", "");
                    List<String> result = Arrays.asList(str.split("\\."));
                    String strMethod = result.get(result.size() - 1);
                    List<String> list = new ArrayList<>(result);
                    list.remove((list.size() - 1));
                    String strClass = String.join(".", list);
                    System.out.println(strClass);
                    try {
                        clazz = Class.forName(strClass);
                        failureCallbackMethod = clazz.getDeclaredMethod(strMethod, String.class);
                        failureCallbackMethod.setAccessible(true);
//                        failureCallbackObject = clazz.newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new CommonException("error.get.asgard.failure.callback", e.getMessage());
                    }
                }
                invokeBeanMap.put(key, new SagaTaskInvokeBean(method, bean, sagaTask, key, clazz, failureCallbackMethod));
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
