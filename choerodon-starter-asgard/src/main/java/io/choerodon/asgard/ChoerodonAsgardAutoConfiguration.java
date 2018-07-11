package io.choerodon.asgard;

import io.choerodon.asgard.saga.*;
import io.choerodon.core.ChoerodonCoreAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaRegistration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@ConditionalOnProperty(prefix = "choerodon.saga", name = "enabled")
@EnableConfigurationProperties(ChoerodonSagaProperties.class)
@Import(ChoerodonCoreAutoConfiguration.class)
public class ChoerodonAsgardAutoConfiguration {

    @Autowired
    private ChoerodonSagaProperties choerodonSagaProperties;

    @Bean
    public SagaApplicationContextHelper sagaApplicationContextHelper() {
        return new SagaApplicationContextHelper();
    }

    @Bean
    public SagaClientCallback sagaClientCallback() {
        return new SagaClientCallback();
    }

    @Bean
    public SagaTaskProcessor sagaTaskProcessor() {
        return new SagaTaskProcessor(sagaApplicationContextHelper());
    }


    @Bean
    public Executor asyncServiceExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(choerodonSagaProperties.getMaxExecuteThread());
        executor.setMaxPoolSize(choerodonSagaProperties.getMaxExecuteThread());
        executor.setQueueCapacity(99999);
        executor.setThreadNamePrefix("saga-service-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    @Bean
    public SagaMonitor sagaMonitor(SagaClient sagaClient,
                                   DataSourceTransactionManager transactionManager,
                                   Optional<EurekaRegistration> eurekaRegistration) {
        return new SagaMonitor(choerodonSagaProperties, sagaClient, asyncServiceExecutor(),transactionManager, eurekaRegistration);
    }

}
