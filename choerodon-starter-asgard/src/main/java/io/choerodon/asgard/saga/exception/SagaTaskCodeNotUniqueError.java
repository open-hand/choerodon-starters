package io.choerodon.asgard.saga.exception;

import io.choerodon.asgard.saga.annotation.SagaTask;

public class SagaTaskCodeNotUniqueError extends RuntimeException {

    public SagaTaskCodeNotUniqueError(SagaTask sagaTask) {
        super("@SagaTask's code with sagaCode must be unique, sagaTaskCode: " + sagaTask.code());
    }

}
