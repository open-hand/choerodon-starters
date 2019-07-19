package io.choerodon.websocket;

import io.choerodon.websocket.v2.helper.BrokerHelper;
import io.choerodon.websocket.v2.receive.WebSocketMessageHandler;
import io.choerodon.websocket.v2.receive.ReceiveRedisMessageListener;
import io.choerodon.websocket.v2.helper.HandshakeCheckerHandler;
import io.choerodon.websocket.v2.helper.SocketHandlerRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@ComponentScan
@Configuration
@EnableWebSocket
public class ChoerodonWebSocketConfigure implements WebSocketConfigurer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChoerodonWebSocketConfigure.class);

    @Autowired
    private WebSocketMessageHandler webSocketHandler;

    @Autowired
    private Optional<List<SocketHandlerRegistration>> socketHandlerRegistrations;


    @Bean
    MessageListenerAdapter defaultListenerAdapter(ReceiveRedisMessageListener receiveRedisMessageListener) {
        return new MessageListenerAdapter(receiveRedisMessageListener, "receiveMessage");
    }

    @Bean
    RedisMessageListenerContainer defaultContainer(RedisConnectionFactory connectionFactory,
                                                   MessageListenerAdapter messageListenerAdapter,
                                                   BrokerHelper brokerHelper) {
        PatternTopic topic = new PatternTopic(brokerHelper.brokerName());
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(messageListenerAdapter, topic);
        LOGGER.info("Begin listen redis channel: {}", topic);
        return container;
    }


    @Bean(name = "registerHeartBeat")
    public ScheduledExecutorService registerHeartBeat() {
        return Executors.newScheduledThreadPool(1);
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        List<SocketHandlerRegistration> registrations = socketHandlerRegistrations.orElseGet(Collections::emptyList);
        registrations.forEach(registration -> {
            webSocketHandler.addSocketHandlerRegistration(registration);
            registry.addHandler(webSocketHandler, registration.path()).setHandshakeHandler(new HandshakeCheckerHandler(registration));
        });
    }

}
