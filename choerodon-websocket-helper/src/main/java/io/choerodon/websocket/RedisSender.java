package io.choerodon.websocket;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.Set;

/**
 * @author jiatong.li
 */
public class RedisSender {
    private RedisTemplate<Object,Object> redisTemplate;

    public RedisSender(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void sendToChannel(Set<String> channels, Msg msg){
        String realChannel;
        for (String channel : channels){
            realChannel  = msg.getMsgType() == Msg.PIPE? "log"+channel:channel;
            redisTemplate.convertAndSend(realChannel,msg);

        }
    }
}
