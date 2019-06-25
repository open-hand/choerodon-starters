package io.choerodon.websocket;

import io.choerodon.websocket.connect.WebSocketMessageHandler;
import io.choerodon.websocket.handshake.AuthHandshakeInterceptor;
import io.choerodon.websocket.handshake.WebSocketHandshakeInterceptor;
import io.choerodon.websocket.notify.ReceiveRedisMessageListener;
import io.choerodon.websocket.register.RedisChannelRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
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

@Configuration
@EnableWebSocket
public class ChoerodonWebSocketConfigure implements WebSocketConfigurer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChoerodonWebSocketConfigure.class);

    @Autowired
    private WebSocketMessageHandler webSocketHandler;

    @Autowired
    private ChoerodonWebSocketProperties choerodonWebSocketProperties;

    @Autowired
    private Optional<List<WebSocketHandshakeInterceptor>> handshakeInterceptors;


    @Bean
    MessageListenerAdapter defaultListenerAdapter(ReceiveRedisMessageListener receiveRedisMessageListener) {
        return new MessageListenerAdapter(receiveRedisMessageListener, "receiveMessage");
    }

    @Bean
    RedisMessageListenerContainer defaultContainer(RedisConnectionFactory connectionFactory,
                                                   MessageListenerAdapter messageListenerAdapter,
                                                   RedisChannelRegister redisChannelRegister) {
        PatternTopic topic = new PatternTopic(redisChannelRegister.channelName());
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(messageListenerAdapter, topic);
        LOGGER.info("Begin listen redis channel: {}", topic);
        return container;
    }

    @Bean
    @ConditionalOnProperty(prefix = "choerodon.ws", name = "oauth")
    AuthHandshakeInterceptor authHandshakeInterceptor() {
        return new AuthHandshakeInterceptor(choerodonWebSocketProperties);
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        List<WebSocketHandshakeInterceptor> interceptors = handshakeInterceptors.orElseGet(Collections::emptyList);
        WebSocketHandshakeInterceptor[] webSocketHandshakeInterceptors = new WebSocketHandshakeInterceptor[interceptors.size()];
        interceptors.toArray(webSocketHandshakeInterceptors);
        registry.addHandler(webSocketHandler, choerodonWebSocketProperties.getPaths())
                .setAllowedOrigins("*")
                .addInterceptors(webSocketHandshakeInterceptors);

    }

}
