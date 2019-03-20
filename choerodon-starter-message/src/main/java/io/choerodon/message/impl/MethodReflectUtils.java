/*
 * #{copyright}#
 */

package io.choerodon.message.impl;

import io.choerodon.message.IMessageConsumer;
import io.choerodon.message.IQueueMessageListener;
import io.choerodon.message.ITopicMessageListener;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author shengyang.zhou@hand-china.com
 */
public class MethodReflectUtils {

    public static final class FindDesc {
        /**
         * default null,no check.
         */
        private String name;
        /**
         * default 1,public.
         */
        private int modifier = Modifier.PUBLIC;
        /**
         * true,false,null(no check).
         * <p>
         * default false
         */
        private Boolean bridged = Boolean.FALSE;

        /**
         * throws exception if no suitable method found,default false.
         */
        private boolean failOnNotFound = false;

        /**
         * parameter count limit,default -1(no check).
         */
        private int parameterCount = -1;

        /**
         * with name.
         * 
         * @param name
         *            method name
         */
        public FindDesc(String name) {
            this.name = name;
        }

        /**
         * with name and parameter count.
         * 
         * @param name
         *            method name
         * @param pc
         *            parameter count
         */
        public FindDesc(String name, int pc) {
            this.name = name;
            this.parameterCount = pc;
        }

        public FindDesc withModifier(int mod) {
            this.modifier = mod;
            return this;
        }

        public FindDesc bridged(Boolean b) {
            this.bridged = b;
            return this;
        }

        public FindDesc paramsCount(int pc) {
            this.parameterCount = pc;
            return this;
        }

        public FindDesc failOnNotFound(boolean fonf) {
            this.failOnNotFound = fonf;
            return this;
        }

    }

    public static List<Method> findMethod(Class clazz, final FindDesc desc) {
        List<Method> methods = new ArrayList<>();
        ReflectionUtils.doWithMethods(clazz, methods::add, m -> {
            if (desc.name != null && !desc.name.equals(m.getName())) {
                return false;
            }
            if (desc.modifier != 0 && (desc.modifier & m.getModifiers()) == 0) {
                return false;
            }
            if (desc.bridged != null) {
                if (m.isBridge() != desc.bridged) {
                    return false;
                }
            }
            if (desc.parameterCount >= 0) {
                if (desc.parameterCount != m.getParameterCount()) {
                    return false;
                }
            }

            methods.add(m);

            return true;
        });
        if (desc.failOnNotFound && methods.isEmpty()) {
            throw new RuntimeException("can not found suitable method on class:" + clazz);
        }

        return methods;
    }

    private static final Map<Class, RedisSerializer> CLASS_SERIALIZER = new HashMap<>();

    /**
     * StringRedisSerializer for String.<br>
     * Jackson2JsonRedisSerializer for others.
     * 
     * @param clazz
     *            serializer type
     * @return RedisSerializer
     */
    public static RedisSerializer getProperRedisSerializer(Class<?> clazz) {
        RedisSerializer rs = CLASS_SERIALIZER.get(clazz);
        if (rs == null) {
            if (clazz == String.class) {
                rs = new StringRedisSerializer();
            } else {
                rs = new Jackson2JsonRedisSerializer<>(clazz);
            }
            CLASS_SERIALIZER.put(clazz, rs);
        }
        return rs;
    }

    /**
     * get proper method name .
     * <p>
     * if mn is blank, try use default method name depends on type of target
     * 
     * @param mn
     *            method name on annotation
     * @param target
     *            target
     * @return proper method name
     */
    public static String getTopicMethodName(String mn, Object target) {
        if (StringUtils.isEmpty(mn)) {
            if (target instanceof ITopicMessageListener) {
                mn = ITopicMessageListener.DEFAULT_METHOD_NAME;
            } else {
                mn = IMessageConsumer.DEFAULT_METHOD_NAME;
            }
        }
        return mn;
    }

    public static String getQueueMethodName(String mn, Object target) {
        if (StringUtils.isEmpty(mn)) {
            if (target instanceof IQueueMessageListener) {
                mn = IQueueMessageListener.DEFAULT_METHOD_NAME;
            } else {
                mn = IMessageConsumer.DEFAULT_METHOD_NAME;
            }
        }
        return mn;
    }

}
