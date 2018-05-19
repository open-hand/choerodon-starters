package io.choerodon.event.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import io.choerodon.core.ChoerodonCoreAutoConfiguration;
import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.event.consumer.annotation.EventListener;
import io.choerodon.event.consumer.domain.EventConsumer;
import io.choerodon.event.consumer.exception.CannotFindTypeReferenceException;
import io.choerodon.event.consumer.exception.RepeatBusinessTypeException;
import io.choerodon.event.consumer.factory.*;
import io.choerodon.event.consumer.handler.DefaultMsgHandlerImpl;
import io.choerodon.event.consumer.handler.MsgHandler;
import io.choerodon.event.consumer.mapper.EventConsumerRecordMapper;
import io.choerodon.event.consumer.retry.RetryFactory;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.security.jwt.crypto.sign.Signer;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * event helper的bean装配AutoConfiguration类
 *
 * @author flyleft
 */
@Configuration
@ConditionalOnProperty(prefix = "choerodon.event.consumer", name = "enabled", matchIfMissing = true)
@EnableAsync
@EnableScheduling
@EnableConfigurationProperties(EventConsumerProperties.class)
@Import(ChoerodonCoreAutoConfiguration.class)
public class EventConsumerAutoConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventConsumerAutoConfiguration.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static final String EVENT_BEAN_PREFIX = "event_consumer_helper_";

    private static final String DEFAULT_KAFKA_CONSUME_GROUP = "default-group";

    private static final CustomUserDetails DEFAULT_USER;

    private static final Signer SIGNER = new MacSigner("hand");

    static {
        DEFAULT_USER = new CustomUserDetails("default", "unknown", Collections.emptyList());
        DEFAULT_USER.setLanguage("zh_CN");
        DEFAULT_USER.setTimeZone("CCT");
        DEFAULT_USER.setUserId(0L);
        DEFAULT_USER.setOrganizationId(1L);
    }

    class OAuthAuthorizationInterceptor implements ClientHttpRequestInterceptor {
        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
            try {
                String jwtToken = "Bearer " + JwtHelper.encode(OBJECT_MAPPER.writeValueAsString(DEFAULT_USER),
                        SIGNER).getEncoded();
                LOGGER.info("token {}", jwtToken);
                request.getHeaders().add("Authorization", jwtToken);
            } catch (IOException e) {
                LOGGER.warn("IOException happen when add RestTemplate JWT token, {}", e.getCause());
            }
            return execution.execute(request, body);
        }
    }

    /**
     * spring工厂加入用于发送HTTP请求的RestTemplate
     *
     * @return 自定义的RestTemplate
     */
    @ConditionalOnMissingBean
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(new OAuthAuthorizationInterceptor()));
        return restTemplate;
    }

    @Bean
    @Autowired
    public DuplicateRemoveListener duplicateRemoveListener(EventConsumerRecordMapper eventConsumerRecordMapper) {
        return new DefaultDuplicateRemoveListener(eventConsumerRecordMapper);
    }

    @ConditionalOnProperty(prefix = "choerodon.event.consumer", name = "retry.enabled", havingValue = "true")
    static class Retry {
        @Bean
        public Scheduler retryScheduler() {
            Scheduler scheduler = null;
            try {
                scheduler = new StdSchedulerFactory().getScheduler();
                scheduler.start();
            } catch (SchedulerException e) {
                LOGGER.warn("error happen when start scheduler, {}", e.getCause());
            }
            return scheduler;
        }

        @Bean
        public RetryFactory retryFactory() {
            return new RetryFactory(retryScheduler());
        }

    }

    @Bean
    public MsgHandler msgHandler(DataSourceTransactionManager transactionManager,
                                 DuplicateRemoveListener listener,
                                 Optional<RetryFactory> retryFactory,
                                 RestTemplate restTemplate) {
        return new DefaultMsgHandlerImpl(transactionManager, listener, retryFactory, restTemplate);
    }

    @ConditionalOnClass(org.apache.kafka.clients.consumer.KafkaConsumer.class)
    @ConditionalOnProperty(prefix = "choerodon.event.consumer", name = "queue-type", havingValue = "kafka", matchIfMissing = true)
    static class Kafka {

        @Value("${spring.application.name:default}")
        private String applicationName;

        @Bean("kafkaPropertiesMap")
        @Autowired
        public Properties kafkaPropertiesMap(EventConsumerProperties consumerProperties) {
            Properties properties = new Properties();
            properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                    consumerProperties.getKafka().getBootstrapServers());
            properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG,
                    consumerProperties.getKafka().getSessionTimeoutMs());
            properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
            properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                    consumerProperties.getKafka().getAutoOffsetReset());
            properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG,
                    consumerProperties.getKafka().getMaxPollRecords());
            properties.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG,
                    consumerProperties.getKafka().getMaxPollIntervalMs());
            properties.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG,
                    consumerProperties.getKafka().getFetchMaxBytes());
            properties.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG,
                    consumerProperties.getKafka().getFetchMaxWaitMs());
            properties.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG,
                    consumerProperties.getKafka().getHeartbeatIntervalMs());
            properties.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG,
                    consumerProperties.getKafka().getFetchMaxBytes());
            properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                    org.apache.kafka.common.serialization.StringDeserializer.class);
            properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                    org.apache.kafka.common.serialization.StringDeserializer.class);
            properties.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG,
                    consumerProperties.getKafka().getPartitionAssignmentStrategy());
            properties.put(ConsumerConfig.SEND_BUFFER_CONFIG,
                    consumerProperties.getKafka().getSendBufferBytes());
            properties.put(ConsumerConfig.RECEIVE_BUFFER_CONFIG,
                    consumerProperties.getKafka().getReceiveBufferBytes());
            properties.put(ConsumerConfig.CLIENT_ID_CONFIG,
                    consumerProperties.getKafka().getClientId());
            properties.put(ConsumerConfig.RECONNECT_BACKOFF_MS_CONFIG,
                    consumerProperties.getKafka().getReconnectBackoffMs());
            properties.put(ConsumerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG,
                    consumerProperties.getKafka().getReconnectBackoffMaxMs());
            properties.put(ConsumerConfig.RETRY_BACKOFF_MS_CONFIG,
                    consumerProperties.getKafka().getRetryBackoffMs());
            properties.put(ConsumerConfig.METRICS_SAMPLE_WINDOW_MS_CONFIG,
                    consumerProperties.getKafka().getMetricsSampleWindowMs());
            properties.put(ConsumerConfig.METRICS_NUM_SAMPLES_CONFIG,
                    consumerProperties.getKafka().getMetricsNumSample());
            properties.put(ConsumerConfig.METRICS_RECORDING_LEVEL_CONFIG,
                    consumerProperties.getKafka().getMetricsRecordingLevel());
            properties.put(ConsumerConfig.METRIC_REPORTER_CLASSES_CONFIG,
                    consumerProperties.getKafka().getMetricReporters());
            properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG,
                    consumerProperties.getKafka().getSecurityProtocol());
            properties.put(ConsumerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG,
                    consumerProperties.getKafka().getConnectionsMaxIdleMs());
            properties.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG,
                    consumerProperties.getKafka().getRequestTimeoutMs());
            properties.put(ConsumerConfig.CHECK_CRCS_CONFIG,
                    consumerProperties.getKafka().isCheckCrcs());
            properties.put(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG,
                    consumerProperties.getKafka().getInterceptorClasses());
            properties.put(ConsumerConfig.EXCLUDE_INTERNAL_TOPICS_CONFIG,
                    consumerProperties.getKafka().isExcludeInternalTopics());
            properties.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG,
                    consumerProperties.getKafka().getIsolationLevel());
            properties.put(ConsumerConfig.GROUP_ID_CONFIG, applicationName);
            if (DEFAULT_KAFKA_CONSUME_GROUP.equals(applicationName)) {
                LOGGER.warn("Please set spring.application.name，otherwise 'default' is used as the default group");
            } else {
                LOGGER.info("kafka consumer group name is {}", applicationName);
            }
            return properties;
        }

        @Bean
        public ExecutorService queueReceiveExecutor() {
            return Executors.newCachedThreadPool();
        }

        @Bean
        public MessageConsumerFactory kafkaMessageConsumeFactory(MsgHandler msgHandler,
                                                                 @Qualifier("kafkaPropertiesMap") Properties kafkaProperties,
                                                                 EventConsumerProperties consumerProperties) {
            return new KafkaMessageConsumerFactory(kafkaProperties,
                    msgHandler,
                    consumerProperties,
                    queueReceiveExecutor());
        }
    }

    @ConditionalOnClass(org.springframework.amqp.rabbit.connection.ConnectionFactory.class)
    @ConditionalOnProperty(prefix = "choerodon.event.consumer", name = "queue-type", havingValue = "rabbitmq")
    static class Rabbitmq {
        @Bean
        public MessageConsumerFactory rabbitMessageConsumeFactory(ConnectionFactory connectionFactory,
                                                                  DataSourceTransactionManager transactionManager,
                                                                  DuplicateRemoveListener listener,
                                                                  Optional<RetryFactory> retryFactory) {
            return new RabbitMessageConsumerFactory(transactionManager, connectionFactory, listener, retryFactory);
        }
    }

    @ConditionalOnClass(org.springframework.data.redis.connection.RedisConnectionFactory.class)
    @ConditionalOnProperty(prefix = "choerodon.event.consumer", name = "queue-type", havingValue = "redis")
    static class Redis {
        @Bean
        public MessageConsumerFactory redisMessageConsumeFactory(DataSourceTransactionManager transactionManager,
                                                                 RedisConnectionFactory connectionFactory,
                                                                 DuplicateRemoveListener listener,
                                                                 Optional<RetryFactory> retryFactory) {
            return new RedisMessageConsumerFactory(transactionManager, connectionFactory, listener, retryFactory);
        }
    }

    @ConditionalOnClass(org.apache.rocketmq.client.consumer.DefaultMQPushConsumer.class)
    @ConditionalOnProperty(prefix = "choerodon.event.consumer", name = "queue-type", havingValue = "rocketmq")
    static class Rocketmq {
        @Bean
        public MessageConsumerFactory rocketMessageConsumeFactory(DataSourceTransactionManager transactionManager,
                                                                  EventConsumerProperties consumerProperties,
                                                                  DuplicateRemoveListener listener,
                                                                  Optional<RetryFactory> retryFactory) {
            return new RocketMessageConsumerFactory(transactionManager, consumerProperties, listener, retryFactory);
        }
    }

    /**
     * 扫描@EventListener注解，并生成生成consumer
     *
     * @param consumeFactory 创建consumer的工厂
     * @return 扫描到的@EventListener集合
     * @throws RepeatBusinessTypeException RepeatBusinessTypeException
     * @throws CannotFindTypeReferenceException CannotFindTypeReferenceException
     */
    @Bean(name = "runAndGetMethods")
    @Autowired
    public Boolean runAndGetTopics(MessageConsumerFactory consumeFactory) throws RepeatBusinessTypeException, CannotFindTypeReferenceException {
        final Set<String> keys = new HashSet<>();
        final StopWatch sw = new StopWatch();
        sw.start();
        final Reflections reflections = new Reflections("", new MethodAnnotationsScanner());
        Set<Method> methods = reflections.getMethodsAnnotatedWith(EventListener.class);
        List<EventConsumer> eventConsumers = new ArrayList<>(methods.size());
        if (methods.isEmpty()) {
            sw.stop();
            return true;
        }
        for (Method method : methods) {
            EventListener listener = AnnotationUtils.findAnnotation(method, EventListener.class);
            for (String type : listener.businessType()) {
                String key = listener.topic() + type;
                if (keys.contains(key)) {
                    throw new RepeatBusinessTypeException(listener.topic(), type);
                }
                keys.add(key);
            }
            Class<?> claz = method.getDeclaringClass();
            Object object = ApplicationContextHelper.getSpringFactory().getBean(claz);
            TypeReference typeReference = CommonUtils.getTypeReference(method);
            eventConsumers.add(new EventConsumer(method, object, listener, typeReference));
        }
        try {
            consumeFactory.createConsumers(eventConsumers);
        } catch (Exception e) {
            LOGGER.warn("error happen when create consumer, {}", e.getCause());
        }
        sw.stop();
        return true;
    }

}
