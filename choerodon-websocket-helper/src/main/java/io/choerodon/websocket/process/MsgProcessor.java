package io.choerodon.websocket.process;

import io.choerodon.websocket.Msg;

/**
 * @author jiatong.li
 */
public interface MsgProcessor {
    boolean shouldProcess(Msg msg);
    void process(Msg msg);
    int getOrder();
}
