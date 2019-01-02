package io.choerodon.asgard.common;

public class UpdateStatusException extends RuntimeException {

    public final Long id;

    public UpdateStatusException(Long id, String status) {
        super("error.asgard.updateStatus, id: " + id + " status: " + status);
        this.id = id;
    }
}
