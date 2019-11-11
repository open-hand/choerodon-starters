package io.choerodon.feign;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariableDefault;

/**
 * 如果开启了Hystrix，会单起一个线程，导致灰度发布时获取不到request
 * 在整个请求生命周期存储请求附带的route_rule，在灰度发布中使用.
 *
 * @author zongw.lee@gmail.com
 */
public class RequestVariableHolder {

    public static final HystrixRequestVariableDefault<String> ROUTE_RULE = new HystrixRequestVariableDefault<>();

    private RequestVariableHolder() {
    }
}
