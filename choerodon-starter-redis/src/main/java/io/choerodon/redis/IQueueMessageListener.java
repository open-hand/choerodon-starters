/*
 * #{copyright}#
 */

package io.choerodon.redis;

import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @param <T>
 *            数据类型
 * @author shengyang.zhou@hand-china.com
 */
public interface IQueueMessageListener<T> {

    String DEFAULT_METHOD_NAME = "onQueueMessage";

    /**
     * @return 队列名称
     */
    String getQueue();

    /**
     *
     * @return 用于反 序列化 从队列中取出的数据
     */
    RedisSerializer<T> getRedisSerializer();

    /**
     *
     * @param message
     *            经过反序列化的数据
     * @param queue
     *            queue name
     */
    void onQueueMessage(T message, String queue);
}
