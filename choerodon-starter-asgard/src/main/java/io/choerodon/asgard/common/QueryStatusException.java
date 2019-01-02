package io.choerodon.asgard.common;

public class QueryStatusException extends RuntimeException {

    public QueryStatusException(Long id) {
        super("error.saga.queryStatus, id: " + id);
    }

}
