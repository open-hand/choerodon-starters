package io.choerodon.websocket.session;

import java.util.List;

/**
 * 缓存Session
 * @author jiatong.li
 */
public interface SessionRepository {

    /**
     * 存入session
     * @param id session uuid
     * @param session session
     */
    void add(String id,Session session);

    /**
     * 通过id移除session
     * @param id session uuid
     */
    Session removeById(String id);

    /**
     * 获取所有Agent session
     * @return 所有Agent session
     */
    List<Session> allExecutors();

    /**
     * 获取所有session
     * @return 所有session
     */
    List<Session> allSessions();
    /**
     * 获取Session通过id
     * @param Id session uuid
     * @return 通过uuid 获取session
     * @throws IllegalArgumentException session找不存在
     */
    Session getById(String Id) throws IllegalArgumentException;


}
