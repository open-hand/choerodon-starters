package io.choerodon.websocket.listener;

import io.choerodon.websocket.helper.EnvSession;

public interface OptionalListener {
    void onConn(EnvSession envSession);
    void onClose(String key);
}
