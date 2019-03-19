/*
 * #{copyright}#
 */

package io.choerodon.message.impl;

import io.choerodon.message.IMessageConsumer;
import io.choerodon.message.IQueueMessageListener;
import io.choerodon.message.ITopicMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 根据反射直接访问方法传递参数.
 * <p>
 * 适用于 Topic 和 Queue,通过 name 来指定 pattern 或者 queue.<br>
 * 根据反射的方法的参数类型自动构造一个RedisSerializer
 * 
 * @author shengyang.zhou@hand-china.com
 * @param <T>
 *            消息类型
 */
public class MessageListenerAdaptor<T>
        implements ITopicMessageListener<T>, IQueueMessageListener<T>, BeanNameAware, InitializingBean {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private String name;

    private Object delegate;
    private String methodName = IMessageConsumer.DEFAULT_METHOD_NAME;
    private Method method;

    private RedisSerializer<T> redisSerializer;

    @Override
    public String getQueue() {
        return name;
    }

    @Override
    public void onQueueMessage(T message, String queue) {
        onTopicMessage(message, queue);
    }

    @Override
    public String[] getTopic() {
        return new String[] { name };
    }

    @Override
    public RedisSerializer<T> getRedisSerializer() {
        return redisSerializer;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object getDelegate() {
        return delegate;
    }

    public void setDelegate(Object delegate) {
        this.delegate = delegate;
    }

    @Override
    public void onTopicMessage(T message, String pattern) {
        if (logger.isDebugEnabled()) {
            logger.debug("receive message: {}, from: {}", message, name);
        }
        try {
            method.invoke(delegate, message, pattern);
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.error("error while invoke method:" + methodName, e);
            }

        }
    }

    @Override
    public void setBeanName(String name) {
        this.name = name;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(delegate, "delegate can not be null.");
        Assert.notNull(methodName, "methodName can not be null.");
        List<Method> methods = MethodReflectUtils.findMethod(delegate.getClass(), new MethodReflectUtils.FindDesc(methodName, 2));
        if (methods.size() == 0) {
            throw new RuntimeException(new StringBuilder("No suitable method named '").append(methodName)
                    .append("' found in ").append(delegate).toString());
        } else if (methods.size() == 1) {
            method = methods.get(0);
        } else {
            // overload method
            throw new RuntimeException("'" + methodName + "' has OVERLOAD method.");
        }
        Class<T> type = (Class<T>) method.getParameterTypes()[0];
        this.redisSerializer = MethodReflectUtils.getProperRedisSerializer(type);
    }
}
