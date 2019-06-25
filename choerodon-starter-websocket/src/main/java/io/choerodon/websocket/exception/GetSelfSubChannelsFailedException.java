package io.choerodon.websocket.exception;

public class GetSelfSubChannelsFailedException extends RuntimeException {

    public GetSelfSubChannelsFailedException(Throwable throwable) {
        super("cannot get sub redis channel names", throwable);
    }
}
