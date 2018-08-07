package io.choerodon.asgard.saga.exception;

import io.choerodon.asgard.saga.annotation.SagaTask;

public class SagaTaskInstanceTableNotExistError extends RuntimeException {

    public SagaTaskInstanceTableNotExistError(SagaTask sagaTask) {
        super("Table saga_task_instance_record must exist when @SagaTask's enabledDbRecord is true, sagaTaskCode: " + sagaTask.code());
    }

}
