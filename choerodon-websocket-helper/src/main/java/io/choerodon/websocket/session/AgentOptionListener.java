package io.choerodon.websocket.session;

import io.choerodon.websocket.helper.EnvSession;
import io.choerodon.websocket.listener.OptionalListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import java.io.IOException;

/**
 * @author crcokitwood
 */
public class AgentOptionListener implements OptionalListener {
    private static final Logger logger = LoggerFactory.getLogger(AgentOptionListener.class);
    private static final String AGENT_SESSION = "cluster-sessions";

    private RedisTemplate<Object,Object> redisTemplate;
    private SessionRepository sessionRepository;

    public AgentOptionListener(RedisTemplate<Object, Object> redisTemplate, SessionRepository sessionRepository) {
        this.redisTemplate = redisTemplate;
        this.sessionRepository = sessionRepository;
    }

    @Override
    public void onConn(EnvSession envSession) {
        redisTemplate.opsForHash().put(AGENT_SESSION,envSession.getRegisterKey(),envSession);
    }

    @Override
    public void onClose(String key, boolean isClean) {
        redisTemplate.opsForHash().delete(AGENT_SESSION,key);
        if (isClean) {
            for (Session session : sessionRepository.allExecutors()) {
                if (session.getRegisterKey().equals(key)) {
                    try {
                        session.getWebSocketSession().close();
                    } catch (IOException e) {
                        logger.warn("close clean timeout session failed {}",e.getMessage());
                    }
                }
            }
        }

    }
}
