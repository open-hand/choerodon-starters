package io.choerodon.websocket.receive;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.websocket.connect.SocketHandlerRegistration;
import io.choerodon.websocket.send.relationship.BrokerKeySessionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
//处理websocket消息
//处理连接成功，断开连接
//
public class MessageHandlerAdapter extends AbstractWebSocketHandler {
    static final String MATCH_ALL_STRING = "*";
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageHandlerAdapter.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final Map<String, Set<HandlerInfo>> pathHandlersMap = new ConcurrentHashMap<>();
    private final Map<String, Set<HandlerInfo>> typeHandlersMap = new ConcurrentHashMap<>();
    private final Map<String, Set<BinaryMessageHandler>> binaryHandlersMap = new ConcurrentHashMap<>();
    private final Map<String, Set<PlaintextMessageHandler>> plaintextHandlersMap = new ConcurrentHashMap<>();
    private final Map<String, SocketHandlerRegistration> registrationMap = new ConcurrentHashMap<>();

    private BrokerKeySessionMapper brokerKeySessionMapper;

    public MessageHandlerAdapter(Collection<MessageHandler> messageHandlers, BrokerKeySessionMapper brokerKeySessionMapper) {
        messageHandlers.forEach(this::addMessageHandler);
        this.brokerKeySessionMapper = brokerKeySessionMapper;
    }
    // 添加接收消息处理器
    public synchronized void addMessageHandler(MessageHandler handler){
        if(handler instanceof TextMessageHandler) {
            TextMessageHandler textMessageHandler = (TextMessageHandler)handler;
            pathHandlersMap.computeIfAbsent(textMessageHandler.matchPath(), key -> new HashSet<>()).add(new HandlerInfo(textMessageHandler.payloadClass(), textMessageHandler));
            typeHandlersMap.computeIfAbsent(textMessageHandler.matchType(), key -> new HashSet<>()).add(new HandlerInfo(textMessageHandler.payloadClass(), textMessageHandler));
        }else if(handler instanceof BinaryMessageHandler){
            binaryHandlersMap.computeIfAbsent(handler.matchPath(), key -> new HashSet<>()).add((BinaryMessageHandler)handler);
        } else if (handler instanceof PlaintextMessageHandler){
            plaintextHandlersMap.computeIfAbsent(handler.matchPath(), key -> new HashSet<>()).add((PlaintextMessageHandler) handler);
        } else {
            LOGGER.warn("Message handler type unsupported {}.", handler.getClass());
        }
    }
    // 添加websocket入口
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
        this.brokerKeySessionMapper.unsubscribeAll(session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
        LOGGER.error("error.webSocketMessageHandler.handleTransportError", exception);
        this.brokerKeySessionMapper.unsubscribeAll(session);
        // 抛出异常 Spring 框架负责断开连接
        throw new Exception(exception);
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
                        TextMessagePayload<?> payload = OBJECT_MAPPER.readValue(receiveMsg, handlerInfo.javaType);
                        handlerInfo.msgHandler.handle(session, payload.getType(), payload.getKey(), payload.getData());
                    }
                } else {
                    LOGGER.warn("abandon message that can not find msgHandler, message {}", receiveMsg);
                }
            } else {
                LOGGER.warn("abandon message that does't have 'type' field, message {}", receiveMsg);
            }
        } catch (JsonParseException e){
            String path = Optional.ofNullable(session.getUri()).map(URI::getPath).orElse(null);
            Set<PlaintextMessageHandler> matchHandlers = new HashSet<>(Optional.ofNullable(plaintextHandlersMap.get(path)).orElse(Collections.emptySet()));
            if (!matchHandlers.isEmpty()) {
                for (PlaintextMessageHandler binaryMessageHandler : matchHandlers){
                    binaryMessageHandler.handle(session, message.getPayload());
                }
            } else {
                LOGGER.warn("abandon message that can not find msgHandler, message {}", message);
            }
        } catch (Exception e) {
            LOGGER.warn("abandon message received from client, msgHandler error, message: {}", receiveMsg, e);
        }
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        String path = Optional.ofNullable(session.getUri()).map(URI::getPath).orElse(null);
        Set<BinaryMessageHandler> matchHandlers = new HashSet<>(Optional.ofNullable(binaryHandlersMap.get(path)).orElse(Collections.emptySet()));
        if (!matchHandlers.isEmpty()) {
            for (BinaryMessageHandler binaryMessageHandler : matchHandlers){
                binaryMessageHandler.handle(session, message);
            }
        } else {
            LOGGER.warn("abandon message that can not find msgHandler, message {}", message);
        }
    }

    final class HandlerInfo {
        final JavaType javaType;
        final TextMessageHandler msgHandler;

        HandlerInfo(Class<?> payloadType, TextMessageHandler msgHandler) {
            this.javaType = OBJECT_MAPPER.getTypeFactory().constructParametricType(TextMessagePayload.class, payloadType);
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
