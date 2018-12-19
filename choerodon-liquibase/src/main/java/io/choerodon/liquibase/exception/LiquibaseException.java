package io.choerodon.liquibase.exception;

public class LiquibaseException extends RuntimeException {

    public LiquibaseException(String message) {
        super(message);
    }

    public LiquibaseException(Throwable cause) {
        super(cause);
    }

    public LiquibaseException(String message, Throwable cause) {
        super(message, cause);
    }

}
