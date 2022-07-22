package io.choerodon.core.utils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Arrays;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

/**
 * Copyright (c) 2022. Hand Enterprise Solution Company. All right reserved.
 *
 * @author zongqi.hao@zknow.com
 * @since 2022/7/13
 */
public class FeignFallbackUtil {

    private FeignFallbackUtil() {
    }

    public static <T> T get(Throwable cause, Class<T> targetClass) {
        Assert.notNull(targetClass, "feign target class could not be null!");
        Logger logger = LoggerFactory.getLogger(targetClass);
        InvocationHandler invocationHandler = (proxy, method, args) -> {
            logger.error("error when call {}.{} by params{}", method.getDeclaringClass().getSimpleName(),
                    method.getName(), JSON.toJSONString(args));
            if (cause != null) {
                logger.error(cause.getMessage(), cause);
            }
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("请联系运维人员");
        };
        return (T) Proxy.newProxyInstance(targetClass.getClassLoader(), new Class[]{targetClass}, invocationHandler);
    }

}
