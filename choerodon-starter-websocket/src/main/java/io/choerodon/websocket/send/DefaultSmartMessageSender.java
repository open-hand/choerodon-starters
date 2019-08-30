package io.choerodon.websocket.send;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import io.choerodon.websocket.send.relationship.BrokerKeySessionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Set;

public class DefaultSmartMessageSender implements MessageSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageSender.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private StringRedisTemplate redisTemplate;
    private BrokerKeySessionMapper brokerKeySessionMapper;

    public DefaultSmartMessageSender(StringRedisTemplate redisTemplate, BrokerKeySessionMapper brokerKeySessionMapper) {
        this.redisTemplate = redisTemplate;
        this.brokerKeySessionMapper = brokerKeySessionMapper;
    }


    @Override
    public void sendByKey(String key, SendMessagePayload<?> payload) {
        if (!StringUtils.isEmpty(key) && payload != null) {
            payload.setKey(key);
            // 从本地找出messageKey对应的session
            Set<WebSocketSession> sessions = brokerKeySessionMapper.getSessionsByKey(key);
            sessions.forEach(session -> {
                sendBySession(session, payload);
            });
            // 从redis存储中找出messageKey对应的broker
            Set<String> brokerChannels = brokerKeySessionMapper.getBrokerChannelsByKey(key);
            if (!brokerChannels.isEmpty()) {
                String payloadJson = payloadToJson(payload);
                brokerChannels.forEach(channel -> {
                    sendToChannel(channel, payloadJson);
                });
            }
        }
    }

    @Override
    public void sendBySession(WebSocketSession session, SendMessagePayload<?> payload) {
        if (payload != null) {
            if (payload instanceof SendBinaryMessagePayload) {
                SendBinaryMessagePayload binaryMessagePayload = (SendBinaryMessagePayload) payload;
                BinaryMessage binaryMessage = new BinaryMessage(binaryMessagePayload.getData());
                this.sendToSession(session, binaryMessage);
            } else if(payload instanceof SendPlaintextMessagePayload) {
                this.sendToSession(session, new TextMessage(((SendPlaintextMessagePayload) payload).getData()));
            } else {
                try {
                    this.sendToSession(session, new TextMessage(OBJECT_MAPPER.writeValueAsString(payload)));
                } catch (JsonProcessingException e) {
                    LOGGER.warn("payload data json processing exception {}", payload);
                }
            }
        }
    }

    @Override
    public void closeSessionByKey(String messageKey) {
        if (!StringUtils.isEmpty(messageKey)) {
            closeLocalSessionByKey(messageKey);
            // 从redis存储中找出messageKey对应的broker
            Set<String> brokerChannels = brokerKeySessionMapper.getBrokerChannelsByKey(messageKey);
            if (!brokerChannels.isEmpty()) {
                String payloadJson = makeClosePayload(messageKey);
                brokerChannels.forEach(channel -> {
                    this.sendToChannel(channel, payloadJson);
                });
            }
        }
    }

    @Override
    public void closeLocalSessionByKey(String messageKey) {
        if (!StringUtils.isEmpty(messageKey)) {
            Set<WebSocketSession> sessions = brokerKeySessionMapper.getSessionsByKey(messageKey);
            sessions.forEach(session -> {
                if (session.isOpen()){
                    try {
                        session.close();
                    } catch (IOException e) {
                        LOGGER.warn("close session {} exception.", session.getId(), e);
                    }
                }
            });
        }
    }

    @Override
    public void sendToLocalSessionByKey(String messageKey, SendMessagePayload<?> payload) {
        if (!StringUtils.isEmpty(messageKey) && payload != null) {
            Set<WebSocketSession> sessions = brokerKeySessionMapper.getSessionsByKey(messageKey);
            sessions.forEach(session -> {
                sendBySession(session, payload);
            });
        }
    }


    private void sendToChannel(String brokerChannel, String payloadJson) {
        redisTemplate.convertAndSend(brokerChannel, payloadJson);
    }


    private void sendToSession(final WebSocketSession session, WebSocketMessage webSocketMessage) {
        try {
            if (!session.isOpen()) {
                brokerKeySessionMapper.unsubscribeAll(session);
                LOGGER.warn("websocket session is close, json: {},message not send {}", session,webSocketMessage);
                return;
            }
            if (webSocketMessage != null) {
                synchronized (session){
                    session.sendMessage(webSocketMessage);
                }
            }
        } catch (IOException e) {
            LOGGER.error("error.messageOperator.sendWebSocket.IOException, json: {}", webSocketMessage, e);
        }
    }

    private String makeClosePayload(String key){
        ObjectNode root = OBJECT_MAPPER.createObjectNode();
        root.set("key", new TextNode(key));
        root.set("type", new TextNode(""));
        root.set("control", new TextNode(BrokerChannelMessageListener.CONTROL_FLAG_CLOSE));
        return root.toString();
    }

    private String payloadToJson(SendMessagePayload<?> payload) {
        ObjectNode root = OBJECT_MAPPER.createObjectNode();
        root.set("key", new TextNode(payload.getKey()));
        root.set("type", new TextNode(payload.getType()));
        if (payload instanceof SendBinaryMessagePayload) {
            root.set("control", new TextNode(BrokerChannelMessageListener.CONTROL_FLAG_BINARY));
            SendBinaryMessagePayload binaryMessagePayload = (SendBinaryMessagePayload) payload;
            root.set("data", new BinaryNode(binaryMessagePayload.getData()));
        } else if(payload instanceof SendPlaintextMessagePayload){
            root.set("control", new TextNode(BrokerChannelMessageListener.CONTROL_FLAG_PLAINTEXT));
            SendPlaintextMessagePayload binaryMessagePayload = (SendPlaintextMessagePayload) payload;
            root.set("data", new TextNode(binaryMessagePayload.getData()));
        } else {
            root.set("control", new TextNode(BrokerChannelMessageListener.CONTROL_FLAG_TEXT));
            JsonNode data = OBJECT_MAPPER.convertValue(payload.getData(), JsonNode.class);
            root.set("data", data);
        }
        return root.toString();

    }

}

