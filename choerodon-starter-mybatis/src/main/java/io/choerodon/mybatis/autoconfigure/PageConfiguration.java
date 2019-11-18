package io.choerodon.mybatis.autoconfigure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.PageableArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class PageConfiguration implements WebMvcConfigurer {
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        PageableArgumentResolver pageableArgumentResolver = pageable();
        resolvers.add(pageableArgumentResolver);
    }

    @Bean
    public PageableArgumentResolver pageable() {
        return new CustomPageableResolver();
    }
}
