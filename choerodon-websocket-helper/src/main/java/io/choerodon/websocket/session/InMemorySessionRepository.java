package io.choerodon.websocket.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jiatong.li
 */
public class InMemorySessionRepository implements SessionRepository {

    private static final Logger logger = LoggerFactory.getLogger(InMemorySessionRepository.class);
    private static final ConcurrentHashMap<String,Session> SOCKET_SESSION_MAP = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String,Session> executorSessions = new ConcurrentHashMap<>();
    private Map<String,Session> sessionMap = SOCKET_SESSION_MAP;

    @Override
    public void add(String id, Session session) {
        sessionMap.put(id,session);
        if(logger.isDebugEnabled()){
            logger.debug("new session :"+ session+" \n the count is "+SOCKET_SESSION_MAP.size());
        }
        if(session.getType() == Session.AGENT){
            executorSessions.put(id, session);
        }
    }

    @Override
    public Session removeById(String id) {
        executorSessions.remove(id);
        Session session = sessionMap.remove(id);
        logger.debug(" session: "+session+"\n close and the count is "+SOCKET_SESSION_MAP.size());
        return session;
    }

    @Override
    public List<Session> allExecutors() {
        return new ArrayList<>(executorSessions.values());
    }

    @Override
    public List<Session> allSessions() {
        return new ArrayList<>(sessionMap.values());
    }

    @Override
    public Session getById(String id) {
        Session session = sessionMap.get(id);
        if(session == null){
            throw new IllegalArgumentException("session not found for session id: "+id);
        }
        return session;
    }


}
