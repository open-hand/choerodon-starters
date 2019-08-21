package io.choerodon.webmvc.paginate;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;


/**
 * 请求映射pageRequest配置类
 *
 * @author NaccOll
 * 2018/1/30
 **/
@Configuration
public class MethodArgParamResolverConfig implements WebMvcConfigurer {
    @Bean
    public PageRequestHandlerMethodArgumentResolver pageRequestResolver() {
        return new PageRequestHandlerMethodArgumentResolver(sortHandlerMethodArgumentResolver());
    }

    @Bean
    public SortHandlerMethodArgumentResolver sortHandlerMethodArgumentResolver() {
        return new SortHandlerMethodArgumentResolver();
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(pageRequestResolver());
        argumentResolvers.add(sortHandlerMethodArgumentResolver());
    }

}

