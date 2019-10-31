package io.choerodon.feign;

import io.choerodon.feign.encoder.PageRequestQueryConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 配置ribbon client rule
 *
 * @author xausky
 */
@EnableWebMvc
@Configuration
@ComponentScan
@RibbonClients(defaultConfiguration = CustomRibbonConfiguration.class)
@EnableConfigurationProperties({CommonProperties.class})
@Import(PageRequestQueryConfig.class)
public class HystrixInterceptorAutoConfiguration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HystrixHeaderInterceptor());
    }

}
