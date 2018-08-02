package io.choerodon.core.exception;

/**
 * 异常信息对象
 *
 * @author wuguokai
 */
public class ExceptionResponse {

    private Boolean failed;
    private String code;
    private String message;


    public ExceptionResponse() {

    }

    /**
     * 创建MessageDto对象
     *
     * @param message 提示消息
     */
    public ExceptionResponse(Boolean failed, String code, String message) {
        this.failed = failed;
        this.code = code;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
