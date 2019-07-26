package io.choerodon.websocket.receive;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.websocket.helper.SocketHandlerRegistration;
import io.choerodon.websocket.relationship.RelationshipDefining;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import org.springframework.web.socket.handler.ExceptionWebSocketHandlerDecorator;

import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketMessageHandler extends AbstractWebSocketHandler {
    static final String MATCH_ALL_STRING = "*";
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketMessageHandler.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final Map<String, Set<HandlerInfo>> pathHandlersMap = new ConcurrentHashMap<>();
    private final Map<String, Set<HandlerInfo>> typeHandlersMap = new ConcurrentHashMap<>();
    private final Map<String, SocketHandlerRegistration> registrationMap = new ConcurrentHashMap<>();

    private RelationshipDefining relationshipDefining;

    public WebSocketMessageHandler(Optional<List<MessageHandler>> msgHandlers, RelationshipDefining relationshipDefining) {
        msgHandlers.orElseGet(Collections::emptyList).forEach(this::addMessageHandler);
        this.relationshipDefining = relationshipDefining;
    }

    public synchronized void addMessageHandler(MessageHandler handler){
        pathHandlersMap.computeIfAbsent(handler.matchPath(), key -> new HashSet<>()).add(new HandlerInfo(handler.payloadClass(), handler));
        typeHandlersMap.computeIfAbsent(handler.matchType(), key -> new HashSet<>()).add(new HandlerInfo(handler.payloadClass(), handler));
    }

    public void addSocketHandlerRegistration(SocketHandlerRegistration registration){
        if (registrationMap.putIfAbsent(registration.path(), registration) != null && LOGGER.isWarnEnabled()){
            LOGGER.warn("path {} connect processor duplicate.", registration.path());
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        if (session.getUri() != null){
            SocketHandlerRegistration registration = registrationMap.get(session.getUri().getPath());
            if (registration != null){
                registration.afterConnectionEstablished(session);
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        if (session.getUri() != null){
            SocketHandlerRegistration registration = registrationMap.get(session.getUri().getPath());
            if (registration != null){
                registration.afterConnectionClosed(session, status);
            }
        }
        this.relationshipDefining.removeWebSocketSessionContact(session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
        LOGGER.error("error.webSocketMessageHandler.handleTransportError", exception);
        this.relationshipDefining.removeWebSocketSessionContact(session);
        throw new Exception(exception); // 抛出异常 Spring 框架负责断开连接
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String receiveMsg = message.getPayload();
        try {
            String path = Optional.ofNullable(session.getUri()).map(URI::getPath).orElse(null);
            String type = OBJECT_MAPPER.readTree(receiveMsg).get("type").asText();
            if (type != null) {
                Set<HandlerInfo> matchHandlers = new HashSet<>(Optional.ofNullable(pathHandlersMap.get(path)).orElse(Collections.emptySet()));
                matchHandlers.addAll(Optional.ofNullable(pathHandlersMap.get(MATCH_ALL_STRING)).orElse(Collections.emptySet()));
                Set<HandlerInfo> matchTypeHandlers = new HashSet<>(Optional.ofNullable(typeHandlersMap.get(type)).orElse(Collections.emptySet()));
                matchTypeHandlers.addAll(Optional.ofNullable(typeHandlersMap.get(MATCH_ALL_STRING)).orElse(Collections.emptySet()));
                matchHandlers.retainAll(matchTypeHandlers);
                if (!matchHandlers.isEmpty()) {
                    for (HandlerInfo handlerInfo : matchHandlers){
                        WebSocketReceivePayload<?> payload = OBJECT_MAPPER.readValue(receiveMsg, handlerInfo.javaType);
                        handlerInfo.msgHandler.handle(session, payload.getType(), payload.getKey(), payload.getData());
                    }
                } else {
                    LOGGER.warn("abandon message that can not find msgHandler, message {}", receiveMsg);
                }
            } else {
                LOGGER.warn("abandon message that does't have 'type' field, message {}", receiveMsg);
            }
        } catch (Exception e) {
            LOGGER.warn("abandon message received from client, msgHandler error, message: {}", receiveMsg, e);
        }
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        String path = Optional.ofNullable(session.getUri()).map(URI::getPath).orElse(null);
        Set<HandlerInfo> matchHandlers = new HashSet<>(Optional.ofNullable(pathHandlersMap.get(path)).orElse(Collections.emptySet()));
        matchHandlers.addAll(Optional.ofNullable(pathHandlersMap.get(MATCH_ALL_STRING)).orElse(Collections.emptySet()));
        matchHandlers.retainAll(Optional.ofNullable(typeHandlersMap.get(MATCH_ALL_STRING)).orElse(Collections.emptySet()));
        if (!matchHandlers.isEmpty()) {
            for (HandlerInfo handlerInfo : matchHandlers){
                handlerInfo.msgHandler.handle(session, message);
            }
        } else {
            LOGGER.warn("abandon message that can not find msgHandler, message {}", message);
        }
    }

    final class HandlerInfo {
        final JavaType javaType;
        final MessageHandler msgHandler;

        HandlerInfo(Class<?> payloadType, MessageHandler msgHandler) {
            this.javaType = OBJECT_MAPPER.getTypeFactory().constructParametricType(WebSocketReceivePayload.class, payloadType);
            this.msgHandler = msgHandler;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            HandlerInfo that = (HandlerInfo) o;
            return Objects.equals(javaType, that.javaType) &&
                    Objects.equals(msgHandler, that.msgHandler);
        }

        @Override
        public int hashCode() {
            return Objects.hash(javaType, msgHandler);
        }
    }

}
