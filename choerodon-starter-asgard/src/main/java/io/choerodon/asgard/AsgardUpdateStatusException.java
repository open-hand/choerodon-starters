package io.choerodon.asgard;

public class AsgardUpdateStatusException extends RuntimeException {

    public AsgardUpdateStatusException(Long id, String status) {
        super("error.saga.updateStatus, id: " + id + " status: " + status);
    }
}
