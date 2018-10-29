package io.choerodon.statemachine.dto;


import com.google.common.base.MoreObjects;

/**
 * @author peng.jiang@hand-china.com
 * @author dinghuang123@gmail.com
 * @since 2018/10/23
 */
public class ExecuteResult {

    private Boolean isSuccess;

    private Long resultStateId;

    private String errorMessage;

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    public Long getResultStateId() {
        return resultStateId;
    }

    public void setResultStateId(Long resultStateId) {
        this.resultStateId = resultStateId;
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
                .add("resultStateId", resultStateId)
                .add("errorMessage", errorMessage)
                .toString();
    }
}

