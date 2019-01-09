package io.choerodon.asgard;

import io.choerodon.asgard.common.ApplicationContextHelper;
import io.choerodon.asgard.property.PropertyData;
import io.choerodon.asgard.property.PropertyDataProcessor;
import io.choerodon.asgard.property.PropertyEndpoint;
import io.choerodon.asgard.saga.SagaProperties;
import io.choerodon.asgard.saga.consumer.SagaConsumer;
import io.choerodon.asgard.saga.consumer.SagaTaskProcessor;
import io.choerodon.asgard.saga.feign.SagaClient;
import io.choerodon.asgard.saga.feign.SagaClientCallback;
import io.choerodon.asgard.saga.feign.SagaConsumerClient;
import io.choerodon.asgard.saga.producer.ProducerBackCheckEndpoint;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.asgard.saga.producer.TransactionalProducerImpl;
import io.choerodon.asgard.saga.producer.consistency.SagaProducerConsistencyHandler;
import io.choerodon.asgard.saga.producer.consistency.SagaProducerDbConsistencyHandler;
import io.choerodon.asgard.saga.producer.consistency.SagaProducerMemoryConsistencyHandler;
import io.choerodon.asgard.schedule.JobTaskProcessor;
import io.choerodon.asgard.schedule.ScheduleConsumer;
import io.choerodon.asgard.schedule.ScheduleProperties;
import io.choerodon.asgard.schedule.feign.ScheduleConsumerClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.*;

@Configuration
@EnableConfigurationProperties({SagaProperties.class, ScheduleProperties.class})
public class AsgardAutoConfiguration {

    @Value("${spring.application.name}")
    private String service;

    @Bean
    public ApplicationContextHelper sagaApplicationContextHelper() {
        return new ApplicationContextHelper();
    }

    @Bean
    public PropertyData propertyData() {
        PropertyData propertyData = new PropertyData();
        propertyData.setService(service);
        return propertyData;
    }

    @Bean(name = "instance")
    public String instance(Environment environment) throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress() + ":" + service + ":" + environment.getProperty("server.port");
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
    static class ScheduleConsumerConfig {

        private ScheduleProperties scheduleProperties;

        public ScheduleConsumerConfig(ScheduleProperties scheduleProperties) {
            this.scheduleProperties = scheduleProperties;
        }

        @Value("${spring.application.name}")
        private String service;

        @Bean(name = "quartzScheduledExecutorService")
        public ScheduledExecutorService scheduledExecutorService() {
            return Executors.newScheduledThreadPool(1);
        }

        @Bean
        public JobTaskProcessor jobTaskProcessor() {
            return new JobTaskProcessor();
        }

        @Bean(name = "scheduleExecutor")
        public Executor scheduleExecutor() {
            int coreSize = scheduleProperties.getCoreThreadNum();
            if (coreSize <= 0) {
                return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
                        new SynchronousQueue<>());
            }
            return new ThreadPoolExecutor(coreSize, scheduleProperties.getMaxThreadNum(), 60L, TimeUnit.SECONDS,
                    new LinkedBlockingDeque<>(99999), new ThreadPoolExecutor.AbortPolicy());
        }

        @Bean
        public ScheduleConsumer scheduleMonitor(ScheduleConsumerClient consumerClient,
                                                DataSourceTransactionManager transactionManager,
                                                ApplicationContextHelper contextHelper,
                                                @Qualifier("instance") String instance,
                                                ScheduleProperties properties) {
            ScheduleConsumer scheduleConsumer = new ScheduleConsumer(service, instance, transactionManager,
                    scheduleExecutor(), scheduledExecutorService(), contextHelper, properties.getPollIntervalMs());
            scheduleConsumer.setScheduleConsumerClient(consumerClient);
            return scheduleConsumer;
        }
    }

    @ConditionalOnProperty(prefix = "choerodon.saga.consumer", name = "enabled")
    static class SagaConsumerConfig {

        @Value("${spring.application.name}")
        private String service;

        private SagaProperties sagaProperties;

        public SagaConsumerConfig(SagaProperties sagaProperties) {
            this.sagaProperties = sagaProperties;
        }

        @Bean(name = "sagaScheduledExecutorService")
        public ScheduledExecutorService sagaScheduledExecutorService() {
            return Executors.newScheduledThreadPool(1);
        }

        @Bean(name = "sagaExecutor")
        public Executor sagaExecutor() {
            int coreSize = sagaProperties.getConsumer().getCoreThreadNum();
            if (coreSize <= 0) {
                return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
                        new SynchronousQueue<>());
            }
            return new ThreadPoolExecutor(coreSize, sagaProperties.getConsumer().getMaxThreadNum(), 60L, TimeUnit.SECONDS,
                    new LinkedBlockingDeque<>(99999), new ThreadPoolExecutor.AbortPolicy());
        }

        @Bean
        public SagaTaskProcessor sagaTaskProcessor() {
            return new SagaTaskProcessor();
        }


        @Bean
        public SagaConsumer sagaMonitor(SagaConsumerClient sagaConsumerClient,
                                        @Qualifier("instance") String instance,
                                        DataSourceTransactionManager transactionManager,
                                        ApplicationContextHelper contextHelper) {
            SagaConsumer sagaConsumer = new SagaConsumer(service, instance, transactionManager, sagaExecutor(),
                    sagaScheduledExecutorService(), contextHelper, sagaProperties.getConsumer().getPollIntervalMs());
            sagaConsumer.setConsumerClient(sagaConsumerClient);
            sagaConsumer.setProperties(sagaProperties);
            return sagaConsumer;
        }

    }

    static class SagaProducer {

        @Value("${spring.application.name}")
        private String service;

        @Bean(name = "clearCacheScheduledService")
        public ScheduledExecutorService sagaScheduledExecutorService() {
            return Executors.newScheduledThreadPool(1);
        }


        @ConditionalOnMissingBean
        @ConditionalOnProperty(prefix = "choerodon.saga.producer", name = "consistencyType", havingValue = "memory", matchIfMissing = true)
        @Bean
        public SagaProducerMemoryConsistencyHandler memoryConsistencyHandler() {
            return new SagaProducerMemoryConsistencyHandler(sagaScheduledExecutorService());
        }

        @ConditionalOnMissingBean
        @ConditionalOnProperty(prefix = "choerodon.saga.producer", name = "consistencyType", havingValue = "db", matchIfMissing = false)
        @Bean
        public SagaProducerDbConsistencyHandler dbConsistencyHandler(DataSource dataSource) {
            return new SagaProducerDbConsistencyHandler(sagaScheduledExecutorService(), dataSource);
        }

        @Bean
        @ConditionalOnMissingBean
        public ProducerBackCheckEndpoint sagaProducerBackCheckEndpoint(SagaProducerConsistencyHandler handler) {
            return new ProducerBackCheckEndpoint(handler);
        }

        @Bean
        public TransactionalProducer transactionalProducer(PlatformTransactionManager transactionManager,
                                                           SagaProducerConsistencyHandler consistencyHandler,
                                                           SagaClient sagaClient) {
            return new TransactionalProducerImpl(transactionManager, consistencyHandler, sagaClient, service);
        }
    }

}
