package io.choerodon.swagger.notify;

public class NotifyBusinessTypeScanData {

    private String code;

    private String name;

    private String categoryCode;

    private String description;

    private String level;

    private Integer retryCount;

    private Boolean isSendInstantly;

    private Boolean isManualRetry;

    private Boolean isAllowConfig;

    private Boolean emailEnabledFlag;

    private Boolean pmEnabledFlag;

    private Boolean smsEnabledFlag;

    private Boolean webhookEnabledFlag;

    private String[] targetUserType;

    private String notifyType;

    private Boolean proEmailEnabledFlag;

    private Boolean proPmEnabledFlag;

    public NotifyBusinessTypeScanData() {
    }

    public NotifyBusinessTypeScanData(String code, String name, String description,
                                      String level, Integer retryCount,
                                      Boolean isSendInstantly, Boolean isManualRetry, Boolean isAllowConfig,
                                      String categoryCode, Boolean emailEnabledFlag, Boolean pmEnabledFlag, Boolean smsEnabledFlag, Boolean webhookEnabledFlag,
                                      String[] targetUserType, String notifyType,
                                      Boolean proEmailEnabledFlag, Boolean proPmEnabledFlag) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.level = level;
        this.retryCount = retryCount;
        this.isSendInstantly = isSendInstantly;
        this.isManualRetry = isManualRetry;
        this.isAllowConfig = isAllowConfig;
        this.categoryCode = categoryCode;
        this.emailEnabledFlag = emailEnabledFlag;
        this.pmEnabledFlag = pmEnabledFlag;
        this.smsEnabledFlag = smsEnabledFlag;
        this.webhookEnabledFlag = webhookEnabledFlag;
        this.targetUserType = targetUserType;
        this.notifyType = notifyType;
        this.proEmailEnabledFlag = proEmailEnabledFlag;
        this.proPmEnabledFlag = proPmEnabledFlag;
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

    public Boolean getAllowConfig() {
        return isAllowConfig;
    }

    public void setAllowConfig(Boolean allowConfig) {
        isAllowConfig = allowConfig;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public Boolean getEmailEnabledFlag() {
        return emailEnabledFlag;
    }

    public void setEmailEnabledFlag(Boolean emailEnabledFlag) {
        this.emailEnabledFlag = emailEnabledFlag;
    }

    public Boolean getPmEnabledFlag() {
        return pmEnabledFlag;
    }

    public void setPmEnabledFlag(Boolean pmEnabledFlag) {
        this.pmEnabledFlag = pmEnabledFlag;
    }

    public Boolean getSmsEnabledFlag() {
        return smsEnabledFlag;
    }

    public void setSmsEnabledFlag(Boolean smsEnabledFlag) {
        this.smsEnabledFlag = smsEnabledFlag;
    }

    public Boolean getWebhookEnabledFlag() {
        return webhookEnabledFlag;
    }

    public void setWebhookEnabledFlag(Boolean webhookEnabledFlag) {
        this.webhookEnabledFlag = webhookEnabledFlag;
    }

    public String[] getTargetUserType() {
        return targetUserType;
    }

    public void setTargetUserType(String[] targetUserType) {
        this.targetUserType = targetUserType;
    }

    public String getNotifyType() {
        return notifyType;
    }

    public void setNotifyType(String notifyType) {
        this.notifyType = notifyType;
    }

    public Boolean getProEmailEnabledFlag() {
        return proEmailEnabledFlag;
    }

    public void setProEmailEnabledFlag(Boolean proEmailEnabledFlag) {
        this.proEmailEnabledFlag = proEmailEnabledFlag;
    }

    public Boolean getProPmEnabledFlag() {
        return proPmEnabledFlag;
    }

    public void setProPmEnabledFlag(Boolean proPmEnabledFlag) {
        this.proPmEnabledFlag = proPmEnabledFlag;
    }
}
