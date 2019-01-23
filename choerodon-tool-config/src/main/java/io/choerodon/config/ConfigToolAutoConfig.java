package io.choerodon.config;

import io.choerodon.config.execute.ConfigServerExecutor;
import io.choerodon.config.execute.Executor;
import io.choerodon.config.execute.RegisterServerExecutor;
import io.choerodon.config.mapper.ServiceConfigMapper;
import io.choerodon.config.mapper.ServiceMapper;
import io.choerodon.config.mapper.ZuulRouteMapper;
import io.choerodon.config.utils.InitConfigProperties;
import io.choerodon.mybatis.MybatisMapperAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties(InitConfigProperties.class)
public class ConfigToolAutoConfig {

    @ConditionalOnProperty(name = "config.type", havingValue = InitConfigProperties.TYPE_CONFIG_SERVER, matchIfMissing = true)
    @Import(MybatisMapperAutoConfiguration.class)
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

}
