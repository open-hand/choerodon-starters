package io.choerodon.asgard.saga.exception;

import java.lang.reflect.Method;

public class SagaTaskMethodParameterError extends RuntimeException {

    public SagaTaskMethodParameterError(Method method) {
        super("@SagaTask method's parameter can only be one string, method: " + method.toString());
    }

}
