package io.choerodon.core.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hzero.boot.message.constant.WebSocketConstant;
import org.hzero.boot.message.entity.Msg;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;

/**
 * <p>
 * 消息客户端
 * 提供消息生成，接收人获取，消息发送等功能
 * </p>
 *
 * @author qingsheng.chen 2018/8/6 星期一 20:09
 */
public class MessageClientC7n {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.application.name}")
    private String serviceName;

    public MessageClientC7n(
                         RedisTemplate<String, String> redisTemplate,
                         ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * 指定用户发送webSocket消息
     * <p>
     * 该方法仅支持websocket使用redis的广播模式，无法使用stream模式。建议依赖hzero-starter-websocket，调用SocketSendHelper.sendByUserId方法
     *
     * @param userId  用户Id
     * @param key     自定义的key
     * @param message 消息内容
     * @deprecated 该方法仅支持websocket使用redis的广播模式，无法使用stream模式。建议依赖hzero-starter-websocket，调用SocketSendHelper.sendByUserId方法
     */
    public void sendByUserId(Long userId, String key, String message) {
        Msg msg = new Msg().setUserId(userId).setKey(key).setMessage(message).setType(WebSocketConstant.SendType.USER).setService(serviceName);
        try {
            redisTemplate.convertAndSend(WebSocketConstant.CHANNEL, objectMapper.writeValueAsString(msg));
        } catch (JsonProcessingException e) {
            throw new CommonException(e);
        }
    }

}
