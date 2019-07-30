package io.choerodon.websocket.websocket.health;

import io.choerodon.websocket.session.Session;

/**
 * Elimination strategy. Invoked when io.choerodon.websocket.websocket.health checks fail.
 * @version 0.1 2019-07-26 13:57
 * @author dongbin
 * @since 1.8
 */
public interface HealthCheckEliminationStrategy {

    /**
     * Eliminate the target.
     * @param session target.
     */
    void eliminate(Session session);
}
