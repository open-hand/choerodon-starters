package io.choerodon.mybatis.autoconfigure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.PageableArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@EnableWebMvc
@Configuration
public class PageConfiguration extends WebMvcConfigurerAdapter {
    @Override
    public void addArgumentResolvers(
            final List<HandlerMethodArgumentResolver> argumentResolvers) {
        PageableArgumentResolver pageableArgumentResolver = pageable();
        argumentResolvers.add(pageableArgumentResolver);
        super.addArgumentResolvers(argumentResolvers);
    }

    @Bean
    public PageableArgumentResolver pageable() {
        return new CustomPageableResolver();
    }
}
