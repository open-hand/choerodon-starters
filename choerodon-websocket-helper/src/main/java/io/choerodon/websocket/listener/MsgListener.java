package io.choerodon.websocket.listener;

import io.choerodon.websocket.Msg;

public interface MsgListener {
    void onMsg(Msg msg);
}
