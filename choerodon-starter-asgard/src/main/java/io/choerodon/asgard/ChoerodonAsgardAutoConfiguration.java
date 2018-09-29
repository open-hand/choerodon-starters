package io.choerodon.asgard;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import io.choerodon.asgard.property.PropertyData;
import io.choerodon.asgard.property.PropertyDataProcessor;
import io.choerodon.asgard.property.PropertyEndpoint;
import io.choerodon.asgard.saga.ChoerodonSagaProperties;
import io.choerodon.asgard.saga.SagaMonitor;
import io.choerodon.asgard.saga.SagaTaskInstanceStore;
import io.choerodon.asgard.saga.SagaTaskProcessor;
import io.choerodon.asgard.saga.feign.SagaClientCallback;
import io.choerodon.asgard.saga.feign.SagaMonitorClient;
import io.choerodon.asgard.saga.feign.SagaMonitorClientCallback;
import io.choerodon.asgard.schedule.ChoerodonScheduleProperties;
import io.choerodon.asgard.schedule.JobTaskProcessor;
import io.choerodon.asgard.schedule.ScheduleMonitor;
import io.choerodon.asgard.schedule.feign.ScheduleMonitorClient;
import io.choerodon.asgard.schedule.feign.ScheduleMonitorClientCallback;

@Configuration
@EnableConfigurationProperties({ChoerodonSagaProperties.class, ChoerodonScheduleProperties.class})
public class ChoerodonAsgardAutoConfiguration {

    @Value("${spring.application.name}")
    private String service;

    @Bean
    public AsgardApplicationContextHelper sagaApplicationContextHelper() {
        return new AsgardApplicationContextHelper();
    }

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

    @ConditionalOnProperty(prefix = "choerodon.schedule.consumer", name = "enabled")
    static class ScheduleConsumer {

        private ChoerodonScheduleProperties scheduleProperties;

        public ScheduleConsumer(ChoerodonScheduleProperties scheduleProperties) {
            this.scheduleProperties = scheduleProperties;
        }

        @Bean
        public ScheduleMonitorClientCallback scheduleMonitorClientCallback() {
            return new ScheduleMonitorClientCallback();
        }

        @Value("${spring.application.name}")
        private String service;

        @Bean(name = "quartzScheduledExecutorService")
        public ScheduledExecutorService quartzScheduledExecutorService() {
            return Executors.newScheduledThreadPool(1);
        }

        @Bean
        public JobTaskProcessor jobTaskProcessor() {
            return new JobTaskProcessor();
        }

        @Bean(name = "scheduleExecutor")
        public Executor scheduleExecutor() {
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setCorePoolSize(scheduleProperties.getThreadNum());
            executor.setMaxPoolSize(scheduleProperties.getThreadNum());
            executor.setQueueCapacity(99999);
            executor.setThreadNamePrefix("Asgard-schedule-consumer-");
            executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
            executor.initialize();
            return executor;
        }

        @Bean
        public ScheduleMonitor scheduleMonitor(ScheduleMonitorClient scheduleMonitorClient,
                                               DataSourceTransactionManager transactionManager,
                                               Environment environment,
                                               AsgardApplicationContextHelper applicationContextHelper) {
            return new ScheduleMonitor(transactionManager, environment, scheduleExecutor(), scheduleMonitorClient,
                    applicationContextHelper, quartzScheduledExecutorService(), scheduleProperties.getPollIntervalMs());

        }
    }

    @ConditionalOnProperty(prefix = "choerodon.saga.consumer", name = "enabled")
    static class SagaConsumer {

        private ChoerodonSagaProperties choerodonSagaProperties;

        public SagaConsumer(ChoerodonSagaProperties choerodonSagaProperties) {
            this.choerodonSagaProperties = choerodonSagaProperties;
        }

        @Bean
        public SagaTaskInstanceStore taskInstanceStore(DataSource dataSource) {
            return new SagaTaskInstanceStore(dataSource);
        }

        @Bean
        public SagaTaskProcessor sagaTaskProcessor(SagaTaskInstanceStore sagaTaskInstanceStore) {
            return new SagaTaskProcessor(sagaTaskInstanceStore);
        }

        @Bean(name = "sagaScheduledExecutorService")
        public ScheduledExecutorService sagaScheduledExecutorService() {
            return Executors.newScheduledThreadPool(1);
        }

        @Bean(name = "sagaExecutor")
        public Executor sagaExecutor() {
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setCorePoolSize(1);
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
                                       SagaTaskInstanceStore taskInstanceStore,
                                       AsgardApplicationContextHelper asgardApplicationContextHelper) {
            SagaMonitor sagaMonitor = new SagaMonitor(choerodonSagaProperties, sagaMonitorClient,
                    sagaExecutor(), transactionManager,
                    environment,
                    taskInstanceStore,
                    asgardApplicationContextHelper);
            sagaMonitor.setScheduledExecutorService(sagaScheduledExecutorService());
            return sagaMonitor;
        }

    }

}
