package io.choerodon.websocket.config;

import io.choerodon.websocket.helper.WebSocketHelper;
import io.choerodon.websocket.receive.MessageHandler;
import io.choerodon.websocket.send.BrokerManager;
import io.choerodon.websocket.receive.MessageHandlerAdapter;
import io.choerodon.websocket.send.BrokerChannelMessageListener;
import io.choerodon.websocket.connect.HandshakeCheckerHandler;
import io.choerodon.websocket.connect.SocketHandlerRegistration;
import io.choerodon.websocket.send.DefaultSmartMessageSender;
import io.choerodon.websocket.send.MessageSender;
import io.choerodon.websocket.send.relationship.BrokerKeySessionMapper;
import io.choerodon.websocket.send.relationship.DefaultBrokerKeySessionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import reactor.util.annotation.NonNullApi;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
@EnableWebSocket
public class ChoerodonWebSocketConfigure implements WebSocketConfigurer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChoerodonWebSocketConfigure.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MessageHandlerAdapter messageHandlerAdapter;

    //通过SocketHandlerRegistration接口找到所有注册的websocket入口
    @Autowired
    private Optional<List<SocketHandlerRegistration>> socketHandlerRegistrations;

    // Broker监听Redis channal消息,初始化Redis MessageListenerAdapter
    @Bean
    MessageListenerAdapter defaultListenerAdapter(BrokerChannelMessageListener receiveRedisMessageListener) {
        return new MessageListenerAdapter(receiveRedisMessageListener, "receiveMessage");
    }



    @Bean
    RedisMessageListenerContainer defaultContainer(RedisConnectionFactory connectionFactory,
                                                   MessageListenerAdapter messageListenerAdapter,
                                                   BrokerManager brokerManager) {
        PatternTopic topic = new PatternTopic(brokerManager.getBrokerName());
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(messageListenerAdapter, topic);
        LOGGER.info("Begin listen redis channel: {}", topic);
        return container;
    }

    // 定时任务执行线程池ScheduledExecutorService
    // Broker定时刷新活跃状态
    //
    @Bean
    public ScheduledExecutorService defaultHeartbeatScheduledExecutorService() {
        return Executors.newScheduledThreadPool(1);
    }

    @Bean
    BrokerManager defaultBrokerManager(StringRedisTemplate redisTemplate){
        return new BrokerManager(redisTemplate, defaultHeartbeatScheduledExecutorService());
    }

    @Bean
    BrokerKeySessionMapper defaultBrokerKeySessionMapper(BrokerManager brokerManager, StringRedisTemplate redisTemplate){
        return new DefaultBrokerKeySessionMapper(redisTemplate, brokerManager);
    }

    @Bean
    BrokerChannelMessageListener defaultBrokerChannelMessageListener(MessageSender messageSender){
        return new BrokerChannelMessageListener(messageSender);
    }


    // Websocket消息处理Adapter
    @Bean
    MessageHandlerAdapter defaultMessageHandlerAdapter(Optional<List<MessageHandler>> messageHandlers, BrokerKeySessionMapper brokerKeySessionMapper){
        return new MessageHandlerAdapter(messageHandlers.orElse(Collections.emptyList()), brokerKeySessionMapper);
    }

    @Bean
    MessageSender defaultMessageSender(BrokerKeySessionMapper brokerKeySessionMapper, StringRedisTemplate redisTemplate) {
        return new DefaultSmartMessageSender(redisTemplate, brokerKeySessionMapper);
    }

    @Bean
    WebSocketHelper defaultWebSocketHelper(MessageSender messageSender, BrokerKeySessionMapper brokerKeySessionMapper) {
        return new WebSocketHelper(messageSender, brokerKeySessionMapper);
    }

    @Override
    public void registerWebSocketHandlers( WebSocketHandlerRegistry registry) {
        //通过SocketHandlerRegistration接口找到所有注册的websocket入口
        socketHandlerRegistrations.orElse(Collections.emptyList()).forEach(registration -> {
            messageHandlerAdapter.addSocketHandlerRegistration(registration);
            registry.addHandler(messageHandlerAdapter, registration.path()).addInterceptors(new HandshakeCheckerHandler(registration)).setAllowedOrigins("*");
        });
    }

}
