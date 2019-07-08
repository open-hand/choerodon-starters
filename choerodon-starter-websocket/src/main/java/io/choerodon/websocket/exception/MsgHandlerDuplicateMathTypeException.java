package io.choerodon.websocket.exception;

import io.choerodon.websocket.receive.ReceiveMsgHandler;

public class MsgHandlerDuplicateMathTypeException extends RuntimeException {

    public MsgHandlerDuplicateMathTypeException(ReceiveMsgHandler msgHandler) {
        super("duplicate matchType, matchType must be unique, matchType: " + msgHandler.matchType());
    }

}
