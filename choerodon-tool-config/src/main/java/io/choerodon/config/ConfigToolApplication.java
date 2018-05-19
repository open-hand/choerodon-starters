package io.choerodon.config;

import io.choerodon.config.execute.ExecutorFactory;
import io.choerodon.config.utils.GatewayProperties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * {@inheritDoc}
 *
 * @author wuguokai
 */
@SpringBootApplication
@EnableConfigurationProperties(GatewayProperties.class)
public class ConfigToolApplication {
    /**
     * 主函数
     *
     * @param args 参数
     */
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ConfigToolApplication.class);
        app.setWebEnvironment(false);
        app.run(args);
    }

    @Bean
    ExecutorFactory executorFactory(ApplicationContext applicationContext) {
        ExecutorFactory factory = new ExecutorFactory();
        factory.setApplicationContext(applicationContext);
        return factory;
    }
}
