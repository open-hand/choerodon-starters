package io.choerodon.resource;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.choerodon.base.provider.CustomProvider;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.resource.permission.PublicPermissionOperationPlugin;
import io.choerodon.swagger.SwaggerConfig;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import io.choerodon.core.oauth.resource.DateDeserializer;
import io.choerodon.core.oauth.resource.DateSerializer;
import io.choerodon.resource.handler.ControllerExceptionHandler;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * 配置jwtToken的验证规则
 *
 * @author wuguokai
 */
@Configuration
@AutoConfigureBefore(JacksonAutoConfiguration.class)
@AutoConfigureAfter(SwaggerConfig.class)
public class ResourceServerConfig {

    /**
     * 创建 jackson objectMapper bean
     *
     * @return 返回bean
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper serializingObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(Date.class, new DateSerializer());
        javaTimeModule.addDeserializer(Date.class, new DateDeserializer());
        mapper.registerModule(javaTimeModule);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    @Bean(name = "messageSource")
    @ConditionalOnMissingClass("io.choerodon.fnd.util.service.impl.CacheMessageSource")
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageBundle =
                new ReloadableResourceBundleMessageSource();
        messageBundle.setBasename("classpath:messages/messages");
        messageBundle.setDefaultEncoding("UTF-8");
        return messageBundle;
    }


    /**
     * ControllerExceptionHandler Bean
     *
     * @return Bean
     */
    @Bean
    public ControllerExceptionHandler controllerExceptionHandler() {
        return new ControllerExceptionHandler();
    }

    /**
     * 扫描public接口
     *
     * @return Bean
     */
    @Bean
    public PublicPermissionOperationPlugin permissionSwaggerOperationPlugin() {
        return new PublicPermissionOperationPlugin();
    }


    @Bean
    @ConditionalOnMissingClass("com.hand.hap.core.impl.CustomProviderImpl")
    public CustomProvider choerodonProvider() {
        return new CustomProvider() {
            @Override
            public String currentLanguage() {
                CustomUserDetails details = DetailsHelper.getUserDetails();
                if (details == null) {
                    return "zh_CN";
                }
                return details.getLanguage();
            }

            @Override
            public Long currentPrincipal() {
                CustomUserDetails details = DetailsHelper.getUserDetails();
                if (details == null) {
                    return 0L;
                }
                return details.getUserId();
            }

            @Override
            public Set<String> getSupportedLanguages() {
                String[] languages = {"zh_CN", "en_US"};
                return new HashSet<>(Arrays.asList(languages));
            }
        };
    }

}