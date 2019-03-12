/*
 * #{copyright}#
 */

package io.choerodon.redis.impl;

import io.choerodon.redis.Cache;
import io.choerodon.redis.CacheManager;
import io.choerodon.redis.IMessagePublisher;
import io.choerodon.redis.IQueueMessageListener;
import io.choerodon.redis.ITopicMessageListener;
import io.choerodon.redis.annotation.QueueMonitor;
import io.choerodon.redis.annotation.TopicMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

/**
 * 用来接收<b>队列</b>消息, reload一个Cache.
 * <p>
 * 在集群环境中,只会有一个节点收到消息,避免重复的reload.<br/>
 * 当reload完毕后,发出一个广播消息,频道[topic:cache.reload] ,消息 [cacheName].<br>
 * 同时订阅这个消息,并对相应的cache 执行onCacheReload()(这个操作会在集群中的每个节点上执行)
 * 
 * @author shengyang.zhou@hand-china.com
 */
@Component
@QueueMonitor(queue = "queue:cache:reload")
@TopicMonitor(channel = "topic:cache:reloaded")
public class CacheReloadProcessor implements IQueueMessageListener<String>, ITopicMessageListener<String> {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private IMessagePublisher messagePublisher;

    private StringRedisSerializer redisSerializer = new StringRedisSerializer();

    private Logger logger = LoggerFactory.getLogger(CacheReloadProcessor.class);

    private String name = "queue:cache:reload";
    private String[] publishMessageTo = { "topic:cache:reloaded" };

    @Override
    public String getQueue() {
        return name;
    }

    public void setQueue(String name) {
        this.name = name;
    }

    @Override
    public String[] getTopic() {
        return publishMessageTo;
    }

    @Override
    public RedisSerializer<String> getRedisSerializer() {
        return redisSerializer;
    }

    @Override
    public void onTopicMessage(String message, String pattern) {
        Cache cache = cacheManager.getCache(message);
        if (cache instanceof RedisCache) {
            ((RedisCache) cache).onCacheReload();
        }
    }

    @Override
    public void onQueueMessage(String cacheName, String queue) {
        Cache<Object> cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("begin reload cache:" + cacheName);
            }
            cache.reload();
            for (String top : publishMessageTo) {
                messagePublisher.publish(top, cacheName);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("reload cache:{} success.", cacheName);
            }
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("{} is not a valid cache.", cacheName);
            }
        }
    }

    public String[] getPublishMessageTo() {
        return publishMessageTo;
    }

    /**
     * when cache reload complete,publish a message(cacheName) to specified
     * topic channels.
     * 
     * @param publishMessageTo
     *            destination
     */
    public void setPublishMessageTo(String[] publishMessageTo) {
        this.publishMessageTo = publishMessageTo;
    }

}
