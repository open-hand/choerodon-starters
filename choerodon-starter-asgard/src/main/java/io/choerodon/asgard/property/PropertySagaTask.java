package io.choerodon.asgard.property;

public class PropertySagaTask {

    private String code;

    private String description;

    private String sagaCode;

    private Integer seq;

    private Integer maxRetryCount;

    private Integer timeoutSeconds;

    private String timeoutPolicy;

    private Integer concurrentLimitNum;

    private String concurrentLimitPolicy;

    private String outputSchema;

    private String outputSchemaSource;


    public PropertySagaTask(String code, String description, String sagaCode, Integer seq, Integer maxRetryCount) {
        this.code = code;
        this.description = description;
        this.sagaCode = sagaCode;
        this.seq = seq;
        this.maxRetryCount = maxRetryCount;
    }

    public PropertySagaTask() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSagaCode() {
        return sagaCode;
    }

    public void setSagaCode(String sagaCode) {
        this.sagaCode = sagaCode;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public Integer getMaxRetryCount() {
        return maxRetryCount;
    }

    public void setMaxRetryCount(Integer maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    public Integer getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(Integer timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    public String getTimeoutPolicy() {
        return timeoutPolicy;
    }

    public void setTimeoutPolicy(String timeoutPolicy) {
        this.timeoutPolicy = timeoutPolicy;
    }

    public Integer getConcurrentLimitNum() {
        return concurrentLimitNum;
    }

    public void setConcurrentLimitNum(Integer concurrentLimitNum) {
        this.concurrentLimitNum = concurrentLimitNum;
    }

    public String getConcurrentLimitPolicy() {
        return concurrentLimitPolicy;
    }

    public void setConcurrentLimitPolicy(String concurrentLimitPolicy) {
        this.concurrentLimitPolicy = concurrentLimitPolicy;
    }

    public String getOutputSchema() {
        return outputSchema;
    }

    public void setOutputSchema(String outputSchema) {
        this.outputSchema = outputSchema;
    }

    public String getOutputSchemaSource() {
        return outputSchemaSource;
    }

    public void setOutputSchemaSource(String outputSchemaSource) {
        this.outputSchemaSource = outputSchemaSource;
    }

    @Override
    public String toString() {
        return "PropertySagaTask{" +
                "code='" + code + '\'' +
                ", description='" + description + '\'' +
                ", sagaCode='" + sagaCode + '\'' +
                ", seq=" + seq +
                ", maxRetryCount=" + maxRetryCount +
                ", timeoutSeconds=" + timeoutSeconds +
                ", timeoutPolicy='" + timeoutPolicy + '\'' +
                ", concurrentLimitNum=" + concurrentLimitNum +
                ", concurrentLimitPolicy='" + concurrentLimitPolicy + '\'' +
                ", outputSchema='" + outputSchema + '\'' +
                ", outputSchemaSource='" + outputSchemaSource + '\'' +
                '}';
    }
}