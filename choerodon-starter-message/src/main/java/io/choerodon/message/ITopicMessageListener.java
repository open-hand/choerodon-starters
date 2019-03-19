/*
 * #{copyright}#
 */

package io.choerodon.message;

import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @param <T>
 *            消息类型
 * @author shengyang.zhou@hand-china.com
 */
public interface ITopicMessageListener<T> {

    String DEFAULT_METHOD_NAME = "onTopicMessage";

    /**
     * 订阅消息类型.
     * <p>
     * 可以订阅多个频道,并且可以使用 * 通配符
     * 
     * @return topics
     */
    String[] getTopic();

    /**
     * 消息反序列化.
     * <p>
     *
     * @return 可以返回 null,表示使用默认的 StringRedisSerializer
     */
    RedisSerializer<T> getRedisSerializer();

    void onTopicMessage(T message, String pattern);
}
