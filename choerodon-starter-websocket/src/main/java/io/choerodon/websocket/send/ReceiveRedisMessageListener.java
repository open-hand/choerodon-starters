package io.choerodon.websocket.send;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * 监听本实例的channel，接收消息
 */
@Service
public class ReceiveRedisMessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReceiveRedisMessageListener.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private MessageSender messageSender;

    public ReceiveRedisMessageListener(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    public void receiveMessage(Object message) {
        LOGGER.debug("receive message from redis channels, message {}", message);
        if (message instanceof String) {
            try {
                JsonNode node = OBJECT_MAPPER.readTree((String) message);
                String key = node.get("key").asText();
                if (!StringUtils.isEmpty(key)) {
                    messageSender.sendWebSocketByKey(key, (String) message);
                }
            } catch (IOException e) {
                LOGGER.warn("error.receiveRedisMessageListener.receiveMessage.send", e);
            }
        } else {
            LOGGER.warn("receive message from redis channels that type is not String, message: {}", message);
        }
    }

}
