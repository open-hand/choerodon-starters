package io.choerodon.websocket.helper;




import org.springframework.boot.actuate.endpoint.AbstractEndpoint;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;

/**
 * @author crcokitwood
 */

@ConfigurationProperties(prefix = "endpoints.clusters")
public class AgentEndpoint  extends AbstractEndpoint<Map<Object, Object>> {

    private static final String AGENT_SESSION = "cluster-sessions";
    private RedisTemplate<Object,Object> redisTemplate;


    public AgentEndpoint(RedisTemplate<Object,Object> redisTemplate) {
        super("clusters", false);
        this.redisTemplate = redisTemplate;
    }


    @Override
    public Map<Object, Object> invoke() {
        return redisTemplate.opsForHash().entries(AGENT_SESSION);
    }
}
