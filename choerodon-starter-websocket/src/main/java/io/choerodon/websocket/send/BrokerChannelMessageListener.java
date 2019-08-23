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
@Service
public class BrokerChannelMessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrokerChannelMessageListener.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static final String BINARY_FLAG_YES = "YES";
    public static final String BINARY_FLAG_NO = "NO";
    private MessageSender messageSender;

    public BrokerChannelMessageListener(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    public void receiveMessage(Object message) {
        LOGGER.debug("receive message from redis channels, message {}", message);
        if (message instanceof String) {
            try {
                JsonNode node = OBJECT_MAPPER.readTree((String) message);
                String binaryFlag = node.get("binary").asText();
                String key = node.get("key").asText();
                String type = node.get("type").asText();
                if(BINARY_FLAG_YES.equals(binaryFlag)){
                    SendBinaryMessagePayload sendBinaryMessagePayload = new SendBinaryMessagePayload();
                    sendBinaryMessagePayload.setKey(key);
                    sendBinaryMessagePayload.setType(type);
                    byte[] data = node.get("data").binaryValue();
                    sendBinaryMessagePayload.setData(data);
                    messageSender.sendToLocalSessionByKey(key,sendBinaryMessagePayload);
                }else{
                    SendMessagePayload<JsonNode> sendMessagePayload = new SendMessagePayload<JsonNode>();
                    sendMessagePayload.setKey(key);
                    sendMessagePayload.setType(type);
                    sendMessagePayload.setData(node.get("data"));
                    messageSender.sendToLocalSessionByKey(key,sendMessagePayload);
                }

            } catch (IOException e) {
                LOGGER.warn("error.receiveRedisMessageListener.receiveMessage.send", e);
            }
        } else {
            LOGGER.warn("receive message from redis channels that type is not String, message: {}", message);
        }
    }

}
