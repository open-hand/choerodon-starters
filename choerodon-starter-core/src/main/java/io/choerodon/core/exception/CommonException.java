package io.choerodon.core.exception;

/**
 * 封装运行异常为CommonException.
 *
 * @author wuguokai
 */
public class CommonException extends RuntimeException {

    private final transient Object[] parameters;

    /**
     * 构造器
     *
     * @param message    异常信息
     * @param parameters parameters
     */
    public CommonException(String message, Object... parameters) {

        super(message);
        this.parameters = parameters;
    }

    public CommonException(Throwable cause, Object... parameters) {
        super(cause);
        this.parameters = parameters;
    }

    public Object[] getParameters() {
        return parameters;
    }
}
