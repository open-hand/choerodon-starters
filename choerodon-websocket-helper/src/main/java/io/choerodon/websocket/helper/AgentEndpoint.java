package io.choerodon.websocket.helper;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;

/**
 * @author crcokitwood
 */

@Endpoint(id = "endpoints.clusters")
public class AgentEndpoint {

    private static final String AGENT_SESSION = "cluster-sessions";
    private RedisTemplate<Object, Object> redisTemplate;


    public AgentEndpoint(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @ReadOperation
    public Map<Object, Object> invoke() {
        return redisTemplate.opsForHash().entries(AGENT_SESSION);
    }
}
