package io.choerodon.core.exception;

public class FeignException extends CommonException {

    public FeignException(String code, Object... parameters) {
        super(code, parameters);
    }

    public FeignException(String code, Throwable cause, Object... parameters) {
        super(code, cause, parameters);
    }

    public FeignException(String code, Throwable cause) {
        super(code, cause);
    }

}
