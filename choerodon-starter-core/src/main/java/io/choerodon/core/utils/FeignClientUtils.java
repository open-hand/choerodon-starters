package io.choerodon.core.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.netflix.client.ClientException;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import org.hzero.core.message.MessageAccessor;
import org.hzero.core.util.ResponseUtils;
import org.springframework.http.ResponseEntity;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.ServiceUnavailableException;

/**
 * @author lihao
 * feign请求和响应通用处理类
 */
public class FeignClientUtils {
    static {
        MessageAccessor.addBasenames("classpath:messages/message_choerodon_starter_core");
    }

    public static <E> E doRequest(FeignClient feignClient, Class<E> elementType) {
        try {
            ResponseEntity<String> stringResponseEntity = feignClient.doRequest();
            return ResponseUtils.getResponse(stringResponseEntity, elementType);
        } catch (HystrixRuntimeException hystrixRuntimeException) {
            if (hystrixRuntimeException.getCause().getCause() instanceof ClientException) {
                String serviceName = extractServiceName(hystrixRuntimeException.getCause().getCause().getMessage());
                throw new ServiceUnavailableException("error.service.unavailable", serviceName);
            } else {
                throw new CommonException(hystrixRuntimeException.getMessage());
            }
        }
    }

    public static <E> E doRequest(FeignClient feignClient, Class<E> elementType, String exceptionCode, Object... param) {
        try {
            ResponseEntity<String> stringResponseEntity = feignClient.doRequest();
            return ResponseUtils.getResponse(stringResponseEntity, elementType);
        } catch (HystrixRuntimeException hystrixRuntimeException) {
            if (hystrixRuntimeException.getCause().getCause() instanceof ClientException) {
                String serviceName = extractServiceName(hystrixRuntimeException.getCause().getCause().getMessage());
                throw new ServiceUnavailableException("error.service.unavailable", serviceName);
            } else {
                throw new CommonException(exceptionCode, param);
            }
        }
    }

    public static <E> E doRequest(FeignClient feignClient, TypeReference<E> elementType) {
        try {
            ResponseEntity<String> stringResponseEntity = feignClient.doRequest();
            return ResponseUtils.getResponse(stringResponseEntity, elementType);
        } catch (HystrixRuntimeException hystrixRuntimeException) {
            if (hystrixRuntimeException.getCause().getCause() instanceof ClientException) {
                String serviceName = extractServiceName(hystrixRuntimeException.getCause().getCause().getMessage());
                throw new ServiceUnavailableException("error.service.unavailable", serviceName);
            } else {
                throw new CommonException(hystrixRuntimeException.getMessage());
            }
        }
    }

    public static <E> E doRequest(FeignClient feignClient, TypeReference<E> elementType, String exceptionCode, Object... param) {
        try {
            ResponseEntity<String> stringResponseEntity = feignClient.doRequest();
            return ResponseUtils.getResponse(stringResponseEntity, elementType);
        } catch (HystrixRuntimeException hystrixRuntimeException) {
            if (hystrixRuntimeException.getCause().getCause() instanceof ClientException) {
                String serviceName = extractServiceName(hystrixRuntimeException.getCause().getCause().getMessage());
                throw new ServiceUnavailableException("error.service.unavailable", serviceName);
            } else {
                throw new CommonException(exceptionCode, param);
            }
        }
    }

    private static String extractServiceName(String message) {
        return message.split("client:")[1].trim();
    }
}
