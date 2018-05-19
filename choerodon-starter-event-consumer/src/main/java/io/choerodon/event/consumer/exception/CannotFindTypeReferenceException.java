package io.choerodon.event.consumer.exception;

/**
 * @author flyleft
 * 2018/4/10
 */
public class CannotFindTypeReferenceException extends Exception {

    public CannotFindTypeReferenceException() {
    }

    public CannotFindTypeReferenceException(String message) {
        super("不能找到该payload的TypeReference，无法序列化消息. type: " +message);
    }
}
