package io.choerodon.asgard.saga.exception;

public class SagaUpdateStatusException extends RuntimeException {

    public SagaUpdateStatusException(Long id, String status) {
        super("error.saga.updateStatus, id: " + id + " status: " + status);
    }
}
