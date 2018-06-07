package io.choerodon.websocket.session;

import io.choerodon.websocket.helper.EnvSession;
import io.choerodon.websocket.listener.OptionalListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

public class AgentOptionListener implements OptionalListener {
    public static final String AGENT_SESSION = "agent-sessions";

    private RedisTemplate<Object,Object> redisTemplate;

    public AgentOptionListener(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void onConn(EnvSession envSession) {
        redisTemplate.opsForHash().put(AGENT_SESSION,envSession.getRegisterKey(),envSession);
    }

    @Override
    public void onClose(String key) {
        redisTemplate.opsForHash().delete(AGENT_SESSION,key);
    }
}
