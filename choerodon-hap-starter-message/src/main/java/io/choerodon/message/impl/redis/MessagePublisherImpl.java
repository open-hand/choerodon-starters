/*
 * #{copyright}#
 */

package io.choerodon.message.impl.redis;

import io.choerodon.message.IMessagePublisher;
import io.choerodon.message.impl.ChannelAndQueuePrefix;
import io.choerodon.message.impl.MethodReflectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author shengyang.zhou@hand-china.com
 * @author njq.niu@hand-china.com
 */
public class MessagePublisherImpl implements IMessagePublisher {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private Logger logger = LoggerFactory.getLogger(MessagePublisherImpl.class);

    @Override
    @SuppressWarnings("unchecked")
    public void publish(String channel, Object message) {
        //添加前缀
        channel = ChannelAndQueuePrefix.addPrefix(channel);
        if (message == null) {
            redisTemplate.convertAndSend(channel, "null");
        } else if (message instanceof String || message instanceof Number) {
            redisTemplate.convertAndSend(channel, message.toString());
        } else {
            redisTemplate.convertAndSend(channel, new String(MethodReflectUtils.getProperRedisSerializer(message.getClass()).serialize(message)));

        }
    }

    @Override
    public void rPush(String list, Object message) {
        message(list, message);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void message(String name, Object message) {
        if (message == null) {
            redisTemplate.opsForList().rightPush(name, "null");
        } else if (message instanceof String || message instanceof Number) {
            redisTemplate.opsForList().rightPush(name, message.toString());
        } else {
            redisTemplate.opsForList().rightPush(name, new String(MethodReflectUtils.getProperRedisSerializer(message.getClass()).serialize(message)));
        }
    }

}
