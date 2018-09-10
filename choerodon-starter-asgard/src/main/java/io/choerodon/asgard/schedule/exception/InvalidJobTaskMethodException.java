package io.choerodon.asgard.schedule.exception;

import java.lang.reflect.Method;

public class InvalidJobTaskMethodException extends RuntimeException {

    public InvalidJobTaskMethodException(final Method method) {
        super("@JobTask method's parameter must be Map<String, Object> and returnType must be Map<String, Object> or void. jobTask method: " + method.getName());
    }

}
