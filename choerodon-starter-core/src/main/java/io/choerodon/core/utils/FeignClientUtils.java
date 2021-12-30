package io.choerodon.core.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import org.hzero.core.message.MessageAccessor;
import org.hzero.core.util.ResponseUtils;
import org.springframework.http.ResponseEntity;

import io.choerodon.core.exception.CommonException;

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
        } catch (Exception hystrixRuntimeException) {
            throw new CommonException(hystrixRuntimeException.getMessage());
        }
    }

    public static <E> E doRequest(FeignClient feignClient, Class<E> elementType, String exceptionCode, Object... param) {
        try {
            ResponseEntity<String> stringResponseEntity = feignClient.doRequest();
            return ResponseUtils.getResponse(stringResponseEntity, elementType);
        } catch (Exception hystrixRuntimeException) {
            throw new CommonException(exceptionCode, param);
        }
    }

    public static <E> E doRequest(FeignClient feignClient, TypeReference<E> elementType) {
        try {
            ResponseEntity<String> stringResponseEntity = feignClient.doRequest();
            return ResponseUtils.getResponse(stringResponseEntity, elementType);
        } catch (Exception hystrixRuntimeException) {

            throw new CommonException(hystrixRuntimeException.getMessage());
        }
    }

    public static <E> E doRequest(FeignClient feignClient, TypeReference<E> elementType, String exceptionCode, Object... param) {
        try {
            ResponseEntity<String> stringResponseEntity = feignClient.doRequest();
            return ResponseUtils.getResponse(stringResponseEntity, elementType);
        } catch (Exception hystrixRuntimeException) {
            throw new CommonException(exceptionCode, param);
        }
    }

    private static String extractServiceName(String message) {
        String[] splitMessage = message.split("client:");
        if (splitMessage.length != 2) {
            return "unknown";
        } else {
            return splitMessage[1].trim();
        }
    }
}
