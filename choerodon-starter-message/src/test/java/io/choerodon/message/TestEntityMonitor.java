package io.choerodon.message;

import io.choerodon.base.entity.BaseEntity;
import io.choerodon.message.annotation.QueueMonitor;
import io.choerodon.message.annotation.TopicMonitor;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@TopicMonitor(channel = "test:topicEntity")
@QueueMonitor(queue = "test:queueEntity")
public class TestEntityMonitor implements IQueueMessageListener<BaseEntity>, ITopicMessageListener<BaseEntity> {
    public int topicCount = 0;
    public int queueCount = 0;

    @Override
    public void onTopicMessage(BaseEntity message, String pattern) {
        Assert.notNull(message, "message not should to null.");
        topicCount++;
    }

    @Override
    public RedisSerializer<BaseEntity> getRedisSerializer() {
        return null;
    }

    @Override
    public void onQueueMessage(BaseEntity message, String queue) {
        Assert.notNull(message, "message not should to null.");
        queueCount++;
    }
}
