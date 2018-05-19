package io.choerodon.event.consumer.exception;

/**
 * 回传到event store时异常
 * @author flyleft
 * 2018/3/8
 */
public class SendEventStoreException extends RuntimeException {

    public SendEventStoreException() {
    }

    public SendEventStoreException(String message) {
        super(message);
    }
}
