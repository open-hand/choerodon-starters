package io.choerodon.limiter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.IOException;


public class PermitsRedisTemplate extends RedisTemplate<String, RedisPermits> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PermitsRedisTemplate.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    public PermitsRedisTemplate() {
        super();
        this.setKeySerializer(new StringRedisSerializer());
        this.setValueSerializer(new RedisSerializer<RedisPermits>() {

            @Override
            public byte[] serialize(RedisPermits redisPermits) throws SerializationException {
                try {
                    return objectMapper.writeValueAsBytes(redisPermits);
                } catch (JsonProcessingException e) {
                    LOGGER.error("fail to serialize redisPermits. ", e);
                    return null;
                }
            }

            @Override
            public RedisPermits deserialize(byte[] bytes) throws SerializationException {
                if (bytes != null) {
                    try {
                        return objectMapper.readValue(bytes, RedisPermits.class);
                    } catch (IOException e) {
                        LOGGER.error("fail to deSerialize to RedisPermits. ", e);
                    }
                }
                return null;
            }
        });
    }
}
