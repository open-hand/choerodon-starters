package io.choerodon.core.exception;

public class ServiceUnavailableException extends CommonException {

    public ServiceUnavailableException(String code, Object... parameters) {
        super(code, parameters);
    }

    public ServiceUnavailableException(String code, Throwable cause, Object... parameters) {
        super(code, cause, parameters);
    }

    public ServiceUnavailableException(String code, Throwable cause) {
        super(code, cause);
    }

    public ServiceUnavailableException(Throwable cause, Object... parameters) {
        super(null, cause, parameters);
    }
}
