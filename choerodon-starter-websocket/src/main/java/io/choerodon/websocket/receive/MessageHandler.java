package io.choerodon.websocket.receive;

/**
 * Created by hailuo.liu@choerodon.io on 2019-08-22.
 */
public interface MessageHandler {
    default String matchPath() {
        return MessageHandlerAdapter.MATCH_ALL_STRING;
    }
}
