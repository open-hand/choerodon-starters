package io.choerodon.websocket.listener;

import io.choerodon.websocket.Msg;
import io.choerodon.websocket.process.ProcessManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleMsgListener implements MsgListener{
    public static final Logger logger = LoggerFactory.getLogger(SimpleMsgListener.class);
    private ProcessManager processManager;

    public SimpleMsgListener(ProcessManager processManager) {
        this.processManager = processManager;
    }

    @Override
    public void onMsg( Msg msg) {
        processManager.process(msg);
    }
}
