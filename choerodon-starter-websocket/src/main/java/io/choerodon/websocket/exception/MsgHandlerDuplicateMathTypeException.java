package io.choerodon.websocket.exception;

import io.choerodon.websocket.receive.TextMessageHandler;

public class MsgHandlerDuplicateMathTypeException extends RuntimeException {

    public MsgHandlerDuplicateMathTypeException(TextMessageHandler msgHandler) {
        super("duplicate matchType, matchType must be unique, matchType: " + msgHandler.matchType());
    }

}
