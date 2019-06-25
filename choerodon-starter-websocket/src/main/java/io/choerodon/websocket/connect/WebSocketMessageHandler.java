package io.choerodon.websocket.connect;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.websocket.exception.MsgHandlerDuplicateMathTypeException;
import io.choerodon.websocket.receive.ReceiveMsgHandler;
import io.choerodon.websocket.receive.WebSocketReceivePayload;
import io.choerodon.websocket.relationship.RelationshipDefining;
import io.choerodon.websocket.send.MessageSender;
import io.choerodon.websocket.send.WebSocketSendPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;

@Component
public class WebSocketMessageHandler extends TextWebSocketHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketMessageHandler.class);

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, HandlerInfo> typeClassMap = new HashMap<>(2 << 4);
    private MessageSender messageSender;
    private RelationshipDefining relationshipDefining;

    public WebSocketMessageHandler(Optional<List<ReceiveMsgHandler>> msgHandlers,
                                   RelationshipDefining relationshipDefining,
                                   MessageSender messageSender) {
        msgHandlers.orElseGet(Collections::emptyList).forEach(t -> {
            if (typeClassMap.get(t.matchType()) == null) {
                typeClassMap.put(t.matchType(), new HandlerInfo(t.payloadClass(), t));
            } else {
                throw new MsgHandlerDuplicateMathTypeException(t);
            }
        });
        this.messageSender = messageSender;
        this.relationshipDefining = relationshipDefining;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        messageSender.sendWebSocket(session, new WebSocketSendPayload<>(WebSocketSendPayload.MSG_TYPE_SESSION, null, session.getId()));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        this.relationshipDefining.removeWebSocketSessionContact(session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
        LOGGER.error("error.webSocketMessageHandler.handleTransportError", exception);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
        String receiveMsg = message.getPayload();
        try {
            JSONObject jsonObject = new JSONObject(receiveMsg);
            String type = jsonObject.getString("type");
            if (type != null) {
                HandlerInfo handlerInfo = typeClassMap.get(type);
                if (handlerInfo != null) {
                    JavaType javaType = objectMapper.getTypeFactory().constructParametricType(WebSocketReceivePayload.class, handlerInfo.payloadType);
                    WebSocketReceivePayload<?> payload = objectMapper.readValue(receiveMsg, javaType);
                    handlerInfo.msgHandler.handle(session, payload.getData());
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

    final class HandlerInfo {
        final Class<?> payloadType;
        final ReceiveMsgHandler msgHandler;

        HandlerInfo(Class<?> payloadType, ReceiveMsgHandler msgHandler) {
            this.payloadType = payloadType;
            this.msgHandler = msgHandler;
        }
    }

}
