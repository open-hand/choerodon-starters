package io.choerodon.asgard.saga.exception;

import io.choerodon.core.exception.CommonException;

public class SagaProducerException extends CommonException {

    public SagaProducerException(String message) {
        super(message);
    }

    public SagaProducerException(String message, Throwable cause) {
        super(message, cause);
    }
}
