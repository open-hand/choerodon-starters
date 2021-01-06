package io.choerodon.core.utils;

import org.springframework.http.ResponseEntity;

@FunctionalInterface
public interface FeignClient {
    /**
     * 执行feign请求
     *
     * @return
     */
    ResponseEntity<String> doRequest();
}
