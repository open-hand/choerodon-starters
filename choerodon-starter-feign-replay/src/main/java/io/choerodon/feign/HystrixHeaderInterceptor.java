package io.choerodon.feign;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static io.choerodon.core.variable.RequestVariableHolder.HEADER_LABEL;
import static io.choerodon.core.variable.RequestVariableHolder.HEADER_ROUTE_RULE;


/**
 * 初始化HystrixRequestContext，存储请求的routeRuleCode
 * 与RequestVariableHolder结合使用
 *
 * @author zongw.lee@gmail.com
 */
public class HystrixHeaderInterceptor extends HandlerInterceptorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(HystrixHeaderInterceptor.class);


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!HystrixRequestContext.isCurrentThreadInitialized()) {
            HystrixRequestContext.initializeContext();
        }
        String routeRule = request.getHeader(HEADER_ROUTE_RULE);
        String label = request.getHeader(HEADER_LABEL);
        logger.debug("Route-Rule:{} X-Eureka-Label: {}", routeRule, label);
        RequestVariableHolder.ROUTE_RULE.set(routeRule);
        RequestVariableHolder.LABEL.set(label);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (HystrixRequestContext.isCurrentThreadInitialized()) {
            HystrixRequestContext.getContextForCurrentThread().shutdown();
        }
    }
}
