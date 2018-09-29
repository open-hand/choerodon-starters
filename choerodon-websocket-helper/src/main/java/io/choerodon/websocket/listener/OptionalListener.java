package io.choerodon.websocket.listener;

import io.choerodon.websocket.helper.EnvSession;

/**
 * @author crcokitwood
 */
public interface OptionalListener {
    void onConn(EnvSession envSession);
    void onClose(String key, boolean isClean);
}
