package io.choerodon.event.consumer.rocketmq;

/**
 * rocketmq消息处理异常
 * @author flyleft
 * 2017/10/23
 */
public class RocketmqException extends Exception {

    public RocketmqException() {
        super();
    }

    public RocketmqException(String message) {
        super(message);
    }

    public RocketmqException(String message, Throwable cause) {
        super(message, cause);
    }

    public RocketmqException(Throwable cause) {
        super(cause);
    }
}
