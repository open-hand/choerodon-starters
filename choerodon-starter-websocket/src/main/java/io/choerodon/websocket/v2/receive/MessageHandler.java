package io.choerodon.websocket.v2.receive;

/**
 * Created by hailuo.liu@choerodon.io on 2019/7/2.
 */
public interface MessageHandler {

    public boolean handle(String messageKey);
}
