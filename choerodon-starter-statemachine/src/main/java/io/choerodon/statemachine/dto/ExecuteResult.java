package io.choerodon.statemachine.dto;


import com.google.common.base.MoreObjects;

/**
 * @author peng.jiang@hand-china.com
 * @author dinghuang123@gmail.com
 * @since 2018/10/23
 */
public class ExecuteResult {

    private Boolean isSuccess;

    private Long resultStatusId;

    private String errorMessage;

    public ExecuteResult() {
    }

    public ExecuteResult(Boolean isSuccess, Long resultStatusId, String errorMessage) {
        this.isSuccess = isSuccess;
        this.resultStatusId = resultStatusId;
        this.errorMessage = errorMessage;
    }

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    public Long getResultStatusId() {
        return resultStatusId;
    }

    public void setResultStatusId(Long resultStatusId) {
        this.resultStatusId = resultStatusId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("isSuccess", isSuccess)
                .add("resultStateId", resultStatusId)
                .add("errorMessage", errorMessage)
                .toString();
    }
}

