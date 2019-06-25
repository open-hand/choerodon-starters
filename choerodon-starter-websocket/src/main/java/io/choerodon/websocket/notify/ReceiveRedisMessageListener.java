package io.choerodon.websocket.notify;

import io.choerodon.websocket.send.MessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 监听本实例的channel，接收消息
 */
@Service
public class ReceiveRedisMessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReceiveRedisMessageListener.class);

    private MessageSender messageSender;

    public ReceiveRedisMessageListener(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    public void receiveMessage(Object message) {
        LOGGER.debug("receive message from redis channels, message {}", message);
        if (message instanceof String) {
            try {
                String json = (String) message;
                JSONObject jsonObject = new JSONObject(json);
                String key = jsonObject.getString("key");
                if (!StringUtils.isEmpty(key)) {
                    messageSender.sendWebSocketByKey(key, json);
                }
            } catch (JSONException e) {
                LOGGER.warn("error.receiveRedisMessageListener.receiveMessage.send", e);
            }
        } else {
            LOGGER.warn("receive message from redis channels that type is not String, message: {}", message);
        }
    }

}
