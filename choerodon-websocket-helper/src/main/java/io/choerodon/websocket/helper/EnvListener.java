package io.choerodon.websocket.helper;

import io.choerodon.websocket.security.AgentSecurityInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

/**
 * @author crockitwood
 */
public class EnvListener {
    public static final String AGENT_SESSION = "cluster-sessions";
    private RedisTemplate<Object, Object> redisTemplate;

    public EnvListener(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    public Map<String, EnvSession> connectedEnv(){
      return  (Map<String,EnvSession>)(Map)redisTemplate.opsForHash().entries(AGENT_SESSION);
    }

}
