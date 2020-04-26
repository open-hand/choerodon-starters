package io.choerodon.asgard.saga.exception;

import java.lang.reflect.Method;

public class SagaTaskMethodParameterException extends RuntimeException {

    public SagaTaskMethodParameterException(Method method) {
        super("@SagaTask method's parameter can only be one string, method: " + method.toString());
    }

}
