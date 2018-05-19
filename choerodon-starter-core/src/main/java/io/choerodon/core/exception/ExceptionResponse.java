package io.choerodon.core.exception;

/**
 * 异常信息对象
 *
 * @author wuguokai
 */
public class ExceptionResponse {

    private Boolean failed;
    private String message;

    /**
     * 创建MessageDto对象
     *
     * @param message 提示消息
     */
    public ExceptionResponse(Boolean failed, String message) {
        super();
        this.failed = failed;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getFailed() {
        return failed;
    }

    public void setFailed(Boolean failed) {
        this.failed = failed;
    }
}
