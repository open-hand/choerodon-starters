package io.choerodon.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.message.impl.rabbit.ListenerContainerFactory;
import io.choerodon.message.impl.rabbit.MessagePublisherImpl;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
@ConditionalOnProperty(havingValue = "rabbitmq", prefix = "message", name = "provider")
public class MessageRabbitAutoConfiguration {

    @Value("${rabbitmq.port:5672}")
    private int port;
    @Value("${rabbitmq.host:localhost}")
    private String host;
    @Value("${rabbitmq.username:guest}")
    private String username;
    @Value("${rabbitmq.password:guest}")
    private String password;
    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public ListenerContainerFactory listenerContainerFactory(){
        return new ListenerContainerFactory();
    }

    @Bean
    public MessagePublisherImpl messagePublisher(){
        return new MessagePublisherImpl();
    }

    @Bean
    public ConnectionFactory defaultConnectionFactory(){
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        return connectionFactory;
    }

    @Bean
    public RabbitTemplate defaultRabbitTemplate(){
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(defaultConnectionFactory());
        rabbitTemplate.setRetryTemplate(defaultRetryTemplate());
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        rabbitTemplate.setExchange("direct-exchange");
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter(){
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RetryTemplate defaultRetryTemplate(){
        RetryTemplate retryTemplate = new RetryTemplate();
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(500);
        backOffPolicy.setMaxInterval(10000);
        backOffPolicy.setMultiplier(10.0);
        retryTemplate.setBackOffPolicy(backOffPolicy);
        return retryTemplate;
    }

    @Bean
    public RabbitAdmin defaultRabbitAdmin(){
        return new RabbitAdmin(defaultConnectionFactory());
    }

    @Bean
    public DirectExchange defaultDirectExchange(){
        return new DirectExchange("defaultDirectExchange");
    }

    @Bean
    public TopicExchange defaultTopicExchange(){
        return new TopicExchange("defaultTopicExchange");
    }

}
