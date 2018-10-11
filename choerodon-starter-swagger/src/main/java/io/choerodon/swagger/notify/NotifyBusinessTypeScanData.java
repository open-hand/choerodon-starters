package io.choerodon.swagger.notify;

public class NotifyBusinessTypeScanData {

    private String code;

    private String name;

    private String description;

    private String level;

    private Integer retryCount;

    private Boolean isSendInstantly;

    private Boolean isManualRetry;

    public NotifyBusinessTypeScanData() {
    }

    public NotifyBusinessTypeScanData(String code, String name, String description,
                                      String level, Integer retryCount,
                                      Boolean isSendInstantly, Boolean isManualRetry) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.level = level;
        this.retryCount = retryCount;
        this.isSendInstantly = isSendInstantly;
        this.isManualRetry = isManualRetry;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public Boolean getSendInstantly() {
        return isSendInstantly;
    }

    public void setSendInstantly(Boolean sendInstantly) {
        isSendInstantly = sendInstantly;
    }

    public Boolean getManualRetry() {
        return isManualRetry;
    }

    public void setManualRetry(Boolean manualRetry) {
        isManualRetry = manualRetry;
    }
}
