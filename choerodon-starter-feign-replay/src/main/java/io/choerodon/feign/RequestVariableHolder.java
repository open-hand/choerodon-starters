package io.choerodon.feign;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariableDefault;

/**
 * 在整个请求生命周期存储请求附带的label，在灰度发布中使用.
 * @author jiatong.li
 * 18-3-7
 */
public class RequestVariableHolder {
    public static final String HEADER_LABEL = "X-Eureka-Label";
    public static final String HEADER_TOKEN = "Authorization";
    public static final String HEADER_JWT = "Jwt_Token";

    public static final HystrixRequestVariableDefault<String> LABEL = new HystrixRequestVariableDefault<>();

    private RequestVariableHolder(){}
}
