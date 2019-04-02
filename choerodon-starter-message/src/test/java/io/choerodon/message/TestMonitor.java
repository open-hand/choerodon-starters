package io.choerodon.message;

import io.choerodon.message.annotation.QueueMonitor;
import io.choerodon.message.annotation.TopicMonitor;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

@Component
@TopicMonitor(channel = "test.topic")
@QueueMonitor(queue = "test.queue")
public class TestMonitor implements IQueueMessageListener<String>, ITopicMessageListener<String> {
    public int topicCount = 0;
    public int queueCount = 0;

    @Override
    public String getQueue() {
        return null;
    }

    @Override
    public String[] getTopic() {
        return new String[0];
    }

    @Override
    public RedisSerializer<String> getRedisSerializer() {
        return null;
    }

    @Override
    public void onTopicMessage(String message, String pattern) {
        topicCount++;
    }

    @Override
    public void onQueueMessage(String message, String queue) {
        queueCount++;
    }

}
