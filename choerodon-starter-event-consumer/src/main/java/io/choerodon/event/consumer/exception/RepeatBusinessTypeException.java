package io.choerodon.event.consumer.exception;

/**
 * @author flyleft
 * 2018/4/10
 */
public class RepeatBusinessTypeException extends Exception {

    public RepeatBusinessTypeException() {
    }

    public RepeatBusinessTypeException(String topic, String type) {
        super("同一个topic的同一个businessType只能被一个EventListener消费, topic: "
                + topic + " busineessType : " +type);
    }
}
