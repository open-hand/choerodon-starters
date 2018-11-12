package io.choerodon.swagger;

import io.choerodon.swagger.exclude.EnableHandSwagger2;
import io.choerodon.swagger.notify.NotifyTemplateProcessor;
import io.choerodon.swagger.swagger.CustomSwaggerOperationPlugin;
import io.choerodon.swagger.swagger.OperationCustomPageRequestReader;
import io.choerodon.swagger.swagger.extra.ExtraDataProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.RequestHandler;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.json.JsonSerializer;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2Mapper;

import static com.google.common.base.Predicates.not;

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

    @Value("${spring.application.name}")
    private String service;

    /**
     * 配置swagger-ui
     * 因为包依赖没有BasicErrorController，所以没有使用instanceOf
     *
     * @return swagger-ui Docket
     */
    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2).select().apis(not((RequestHandler requestHandler) ->
                requestHandler.declaringClass().getName().equals("org.springframework.boot.autoconfigure.web.BasicErrorController")
        )).build();
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

    @Bean("notifyTemplateProcessor")
    public NotifyTemplateProcessor notifyTemplateProcessor() {
        return new NotifyTemplateProcessor();
    }

    @Bean
    public CustomController customSwagger2Controller(JsonSerializer jsonSerializer,
                                                     DocumentationCache documentationCache,
                                                     ServiceModelToSwagger2Mapper mapper) {
        return new CustomController(jsonSerializer, extraDataProcessor(),
                notifyTemplateProcessor(), documentationCache, mapper);
    }


}
