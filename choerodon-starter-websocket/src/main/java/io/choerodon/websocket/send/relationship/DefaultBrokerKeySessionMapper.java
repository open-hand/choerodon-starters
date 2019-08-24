package io.choerodon.websocket.send.relationship;

import io.choerodon.websocket.send.BrokerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultBrokerKeySessionMapper implements BrokerKeySessionMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrokerKeySessionMapper.class);
    private Map<String, Set<WebSocketSession>> keySessionsMap = new ConcurrentHashMap<>();
    private Map<WebSocketSession, Set<String>> sessionKeysMap = new ConcurrentHashMap<>();
    private StringRedisTemplate redisTemplate;
    private BrokerManager brokerManager;

    public DefaultBrokerKeySessionMapper(StringRedisTemplate redisTemplate, BrokerManager brokerManager) {
        this.redisTemplate = redisTemplate;
        this.brokerManager = brokerManager;
    }

    @Override
    public void subscribe(String messageKey, WebSocketSession session) {
        if (StringUtils.isEmpty(messageKey)) {
            return;
        }
        if (session != null) {
            Set<WebSocketSession> keySessions = keySessionsMap.computeIfAbsent(messageKey, k -> new HashSet<>());
            keySessions.add(session);
            Set<String> sessionKeys = sessionKeysMap.computeIfAbsent(session, k -> new HashSet<>());
            sessionKeys.add(messageKey);
            LOGGER.debug("webSocket subscribe sessionId is {}, subKeys is {}", session.getId(), sessionKeys);
        }
        redisTemplate.opsForSet().add(brokerManager.getBrokerKeyMapKey(), messageKey);
    }

    @Override
    public void unsubscribeAll(WebSocketSession session) {
        Set<String> sessionKeys = sessionKeysMap.computeIfAbsent(session, k -> new HashSet<>());
        for(String messageKey: sessionKeys){
            Set<WebSocketSession> keySessions = keySessionsMap.computeIfAbsent(messageKey, k -> new HashSet<>());
            keySessions.remove(session);
            if(keySessions.isEmpty()){
                redisTemplate.opsForSet().remove(brokerManager.getBrokerKeyMapKey(), messageKey);
            }
        }
        sessionKeysMap.remove(session);
    }

    @Override
    public void unsubscribe(WebSocketSession session, String messageKey) {
        Set<WebSocketSession> keySessions = keySessionsMap.computeIfAbsent(messageKey, k -> new HashSet<>());
        keySessions.remove(session);
        Set<String> sessionKeys = sessionKeysMap.computeIfAbsent(session, k -> new HashSet<>());
        sessionKeys.remove(messageKey);
        if(keySessions.isEmpty()){
            redisTemplate.opsForSet().remove(brokerManager.getBrokerKeyMapKey(), messageKey);
        }
    }

    @Override
    public Set<WebSocketSession> getSessionsByKey(String messageKey) {
        return keySessionsMap.computeIfAbsent(messageKey, k -> new HashSet<>());
    }

    @Override
    public Set<String> getBrokerChannelsByKey(String messageKey) {
        return getBrokerChannelsByKey(messageKey,true);
    }

    @Override
    public Set<String> getKeysBySession(WebSocketSession session) {
        return sessionKeysMap.getOrDefault(session, Collections.emptySet());
    }

    @Override
    public Set<String> getBrokerChannelsByKey(String messageKey, boolean exceptSelf) {
        Set<String> set = new HashSet<>();
        for(String brokerName:brokerManager.getActiveBrokers()){
            if(exceptSelf&&brokerManager.getBrokerName().equals(brokerName)){
                continue;
            }
            Boolean isMember = redisTemplate.opsForSet().isMember(brokerManager.getBrokerKeyMapKey(brokerName), messageKey);
            if (Boolean.TRUE.equals(isMember)) {
                set.add(brokerName);
            }
        }
        return set;
    }
}
