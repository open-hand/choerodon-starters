package io.choerodon.asgard;

import io.choerodon.asgard.saga.*;
import io.choerodon.asgard.saga.feign.SagaClientCallback;
import io.choerodon.asgard.saga.feign.SagaMonitorClient;
import io.choerodon.asgard.saga.feign.SagaMonitorClientCallback;
import io.choerodon.asgard.property.PropertyData;
import io.choerodon.asgard.property.PropertyDataProcessor;
import io.choerodon.asgard.property.PropertyEndpoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.sql.DataSource;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableConfigurationProperties(ChoerodonSagaProperties.class)
public class ChoerodonAsgardAutoConfiguration {

    @Value("${spring.application.name}")
    private String service;

    @Bean
    public PropertyData propertyData() {
        PropertyData propertyData = new PropertyData();
        propertyData.setService(service);
        return propertyData;
    }

    @Bean
    public PropertyDataProcessor propertyDataProcessor() {
        return new PropertyDataProcessor(propertyData());
    }

    @Bean
    public PropertyEndpoint propertyEndpoint() {
        return new PropertyEndpoint(propertyData());
    }

    @Bean
    public SagaClientCallback sagaClientCallback() {
        return new SagaClientCallback();
    }

    @ConditionalOnProperty(prefix = "choerodon.saga.consumer", name = "enabled", matchIfMissing = true)
    static class Consumer {

        private ChoerodonSagaProperties choerodonSagaProperties;

        public Consumer(ChoerodonSagaProperties choerodonSagaProperties) {
            this.choerodonSagaProperties = choerodonSagaProperties;
        }

        @Bean
        public SagaTaskInstanceStore taskInstanceStore(DataSource dataSource) {
            return new SagaTaskInstanceStore(dataSource);
        }

        @Bean
        public SagaApplicationContextHelper sagaApplicationContextHelper() {
            return new SagaApplicationContextHelper();
        }

        @Bean
        public SagaProcessor sagaTaskProcessor(SagaTaskInstanceStore sagaTaskInstanceStore,
                                               SagaApplicationContextHelper applicationContextHelper) {
            return new SagaProcessor(applicationContextHelper, sagaTaskInstanceStore);
        }

        @Bean
        public Executor asyncServiceExecutor() {
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setCorePoolSize(choerodonSagaProperties.getThreadNum());
            executor.setMaxPoolSize(choerodonSagaProperties.getThreadNum());
            executor.setQueueCapacity(99999);
            executor.setThreadNamePrefix("Asgard-saga-consumer-");
            executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
            executor.initialize();
            return executor;
        }

        @Bean
        public SagaMonitorClientCallback sagaMonitorClientCallback() {
            return new SagaMonitorClientCallback();
        }

        @Bean
        public SagaMonitor sagaMonitor(SagaMonitorClient sagaMonitorClient,
                                       DataSourceTransactionManager transactionManager,
                                       Environment environment,
                                       SagaTaskInstanceStore taskInstanceStore) {
            return new SagaMonitor(choerodonSagaProperties, sagaMonitorClient,
                    asyncServiceExecutor(), transactionManager,
                    environment, sagaApplicationContextHelper(),
                    taskInstanceStore);
        }

    }

}
