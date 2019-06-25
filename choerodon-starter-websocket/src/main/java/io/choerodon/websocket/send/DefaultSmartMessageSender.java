package io.choerodon.websocket.send;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import io.choerodon.websocket.relationship.RelationshipDefining;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Component
public class DefaultSmartMessageSender implements MessageSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageSender.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    private StringRedisTemplate redisTemplate;

    private RelationshipDefining relationshipDefining;

    public DefaultSmartMessageSender(StringRedisTemplate redisTemplate,
                                     RelationshipDefining relationshipDefining) {
        this.redisTemplate = redisTemplate;
        this.relationshipDefining = relationshipDefining;
    }

    @Override
    public void sendWebSocket(WebSocketSession session, WebSocketSendPayload<?> payload) {
        if (payload == null) {
            LOGGER.warn("error.messageOperator.sendWebSocket.payloadIsNull");
            return;
        }
        if (session == null) {
            return;
        }
        if (!session.isOpen()) {
            relationshipDefining.removeWebSocketSessionContact(session);
            return;
        }
        try {
            TextMessage textMessage = new TextMessage(objectMapper.writeValueAsBytes(payload));
            session.sendMessage(textMessage);
        } catch (IOException e) {
            LOGGER.warn("error.messageOperator.sendWebSocket.IOException, payload: {}", payload, e);
        }
    }

    @Override
    public void sendRedis(String channel, WebSocketSendPayload<?> payload) {
        if (payload == null) {
            LOGGER.warn("error.messageOperator.sendRedis.payloadIsNull");
            return;
        }
        try {
            redisTemplate.convertAndSend(channel, objectMapper.writeValueAsString(payload));
        } catch (JsonProcessingException e) {
            LOGGER.warn("error.messageOperator.sendRedisDefaultChannel.JsonProcessingException, payload: {}", payload, e);
        }
    }

    @Override
    public void sendRedis(String channel, String json) {
        if (json == null) {
            LOGGER.warn("error.messageOperator.sendRedis.payloadIsNull");
            return;
        }
        redisTemplate.convertAndSend(channel, json);
    }


    @Override
    public void sendWebSocket(WebSocketSession session, String json) {
        if (json == null) {
            LOGGER.warn("error.messageOperator.sendWebSocket.jsonIsNull");
            return;
        }
        if (session == null) {
            return;
        }
        if (!session.isOpen()) {
            relationshipDefining.removeWebSocketSessionContact(session);
            return;
        }
        try {
            session.sendMessage(new TextMessage(json));
        } catch (IOException e) {
            LOGGER.warn("error.messageOperator.sendWebSocket.IOException, json: {}", json, e);
        }
    }

    @Override
    public void sendWebSocketByKey(String key, String json) {
        relationshipDefining.getWebSocketSessionsByKey(key).forEach(session -> this.sendWebSocket(session, json));
    }


    @Override
    public void sendByKey(String key, WebSocketSendPayload<?> payload) {
        if (!StringUtils.isEmpty(key) && payload != null) {
            relationshipDefining.getWebSocketSessionsByKey(key).forEach(session -> this.sendWebSocket(session, payload));
            relationshipDefining.getRedisChannelsByKey(key, true).forEach(redis -> this.sendRedis(redis, payload));
        }
    }

    @Override
    public void sendByKey(String key, String type, String data) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(type)) {
            return;
        }
        try {
            JsonNode jsonNode = objectMapper.readTree(data);
            ObjectNode root = objectMapper.createObjectNode();
            root.set("key", new TextNode(key));
            root.set("type", new TextNode(type));
            if (data == null) {
                root.set("data", NullNode.instance);
            } else {
                if (jsonNode instanceof ObjectNode || jsonNode instanceof ValueNode) {
                    root.set("data", jsonNode);
                } else if (jsonNode instanceof ArrayNode) {
                    root.putArray("data").addAll((ArrayNode) jsonNode);
                }
            }
            String json = root.toString();
            relationshipDefining.getWebSocketSessionsByKey(key).forEach(session -> this.sendWebSocket(session, json));
            relationshipDefining.getRedisChannelsByKey(key, true).forEach(redis -> this.sendRedis(redis, json));
        } catch (IOException e) {
            this.sendByKey(key, new WebSocketSendPayload<>(type, key, data));
        }
    }

}

