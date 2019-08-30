/*
 * #{copyright}#
 */

package io.choerodon.message.impl;

import io.choerodon.message.IMessageConsumer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * 通过反射直接访问对象方法,传递消息.
 * 
 * @author shengyang.zhou@hand-china.com
 * @param <T>
 *            消息类型
 */
public class MessageConsumerAdaptor<T> implements IMessageConsumer<T>, InitializingBean {

    private Object delegate;

    private String methodName;

    private Method method;

    public Object getDelegate() {
        return delegate;
    }

    public void setDelegate(Object delegate) {
        this.delegate = delegate;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    @Override
    public void onMessage(T message, String pattern) {
        try {
            method.invoke(delegate, message, pattern);
        } catch (Exception e) {
            throw new RuntimeException(new StringBuilder("Failed to invoke target method '").append(methodName)
                    .append("' with argument: ").append(message).toString());
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ReflectionUtils.doWithMethods(delegate.getClass(), method -> {
            ReflectionUtils.makeAccessible(method);
            MessageConsumerAdaptor.this.method = method;
        } , method -> {
            if (method.getName().equals(getMethodName())) {
                Class<?>[] paraTypes = method.getParameterTypes();
                return paraTypes.length == 2 && paraTypes[1] == String.class;
            }
            return false;
        });
        if (method == null) {
            throw new RuntimeException(new StringBuilder("No suitable method named '").append(methodName)
                    .append("' found in ").append(delegate).toString());
        }
    }
}
