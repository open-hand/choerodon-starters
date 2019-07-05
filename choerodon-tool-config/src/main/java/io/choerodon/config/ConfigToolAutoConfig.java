package io.choerodon.config;

import io.choerodon.base.provider.CustomProvider;
import io.choerodon.config.execute.ConfigServerExecutor;
import io.choerodon.config.execute.Executor;
import io.choerodon.config.execute.RegisterServerExecutor;
import io.choerodon.config.mapper.ServiceConfigMapper;
import io.choerodon.config.mapper.ServiceMapper;
import io.choerodon.config.mapper.ZuulRouteMapper;
import io.choerodon.config.utils.InitConfigProperties;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.ChoerodonMybatisAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableConfigurationProperties(InitConfigProperties.class)
public class ConfigToolAutoConfig {

    @ConditionalOnProperty(name = "config.type", havingValue = InitConfigProperties.TYPE_CONFIG_SERVER, matchIfMissing = true)
    @Import(ChoerodonMybatisAutoConfiguration.class)
    static class ConfigServer {

        @Bean
        public Executor configServerExecutor(ServiceMapper serviceMapper,
                                             ServiceConfigMapper serviceConfigMapper,
                                             ZuulRouteMapper zuulRouteMapper) {
            return new ConfigServerExecutor(serviceMapper, serviceConfigMapper, zuulRouteMapper);
        }

    }

    @ConditionalOnProperty(name = "config.type", havingValue = InitConfigProperties.TYPE_REGISTER_SERVER)
    @Configuration
    static class RegisterServer {

        @Bean
        public Executor registerServerExecutor() {
            return new RegisterServerExecutor();
        }
    }

    @Bean
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
