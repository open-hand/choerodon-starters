package io.choerodon.websocket.exception;

import io.choerodon.websocket.receive.MessageHandler;

public class MsgHandlerDuplicateMathTypeException extends RuntimeException {

    public MsgHandlerDuplicateMathTypeException(MessageHandler msgHandler) {
        super("duplicate matchType, matchType must be unique, matchType: " + msgHandler.matchType());
    }

}
