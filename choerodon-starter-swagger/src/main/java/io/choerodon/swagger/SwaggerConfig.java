package io.choerodon.swagger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import io.choerodon.swagger.custom.extra.ExtraDataProcessor;
import io.choerodon.swagger.custom.swagger.CustomSwagger2Controller;
import io.choerodon.swagger.exclude.EnableHandSwagger2;

/**
 * swagger的config类，配置Docket和自定以CustomSwaggerOperationPlugin插件
 *
 * @author xausky
 */
@Configuration
@EnableHandSwagger2
public class SwaggerConfig {

    @Value("${swagger.oauthUrl:http://localhost:8080/oauth/oauth/authorize}")
    private String oauthUrl;

    /**
     * 配置swagger-ui
     *
     * @return swagger-ui Docket
     */
    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2).select().build();
    }

    @Bean
    public CustomSwaggerOperationPlugin customSwaggerOperationPlugin() {
        return new CustomSwaggerOperationPlugin();
    }

    @Bean
    public OperationCustomPageRequestReader operationCustomPageRequestReader() {
        return new OperationCustomPageRequestReader();
    }

    @Bean("extraDataProcessor")
    public ExtraDataProcessor extraDataProcessor() {
        return new ExtraDataProcessor();
    }

    @Bean
    @DependsOn("extraDataProcessor")
    public CustomSwagger2Controller customSwagger2Controller() {
        return new CustomSwagger2Controller();
    }
}
