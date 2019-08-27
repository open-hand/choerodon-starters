package io.choerodon.websocket.send;

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
public class BrokerChannelMessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrokerChannelMessageListener.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static final String CONTROL_FLAG_BINARY = "BINARY";
    public static final String CONTROL_FLAG_TEXT = "TEXT";
    public static final String CONTROL_FLAG_CLOSE = "CLOSE";
    private MessageSender messageSender;

    public BrokerChannelMessageListener(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    public void receiveMessage(Object message) {
        LOGGER.debug("receive message from redis channels, message {}", message);
        if (message instanceof String) {
            try {
                JsonNode node = OBJECT_MAPPER.readTree((String) message);
                String control = node.get("control").asText();
                String key = node.get("key").asText();
                String type = node.get("type").asText();
                switch (control){
                    case CONTROL_FLAG_BINARY:
                        SendBinaryMessagePayload sendBinaryMessagePayload = new SendBinaryMessagePayload();
                        sendBinaryMessagePayload.setKey(key);
                        sendBinaryMessagePayload.setType(type);
                        byte[] data = node.get("data").binaryValue();
                        sendBinaryMessagePayload.setData(data);
                        messageSender.sendToLocalSessionByKey(key,sendBinaryMessagePayload);
                        break;
                    case CONTROL_FLAG_TEXT:
                        SendMessagePayload<JsonNode> sendMessagePayload = new SendMessagePayload<JsonNode>();
                        sendMessagePayload.setKey(key);
                        sendMessagePayload.setType(type);
                        sendMessagePayload.setData(node.get("data"));
                        messageSender.sendToLocalSessionByKey(key,sendMessagePayload);
                        break;
                    case CONTROL_FLAG_CLOSE:
                        messageSender.closeLocalSessionByKey(key);
                        break;
                }
            } catch (IOException e) {
                LOGGER.warn("error.receiveRedisMessageListener.receiveMessage.send", e);
            }
        } else {
            LOGGER.warn("receive message from redis channels that type is not String, message: {}", message);
        }
    }

}
