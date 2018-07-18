package io.choerodon.asgard.saga;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.Objects;
import java.util.Set;

public class DataObject {

    private DataObject() {
    }

    public static class SagaTaskInstanceDTO {

        private Long id;

        private Long sagaInstanceId;

        private String sagaCode;

        private String taskCode;

        private String status;

        private String inputData;

        private String outputData;

        private String instanceLock;

        private Integer seq;

        private Integer maxRetryCount;

        private Integer retriedCount;

        public String getSagaCode() {
            return sagaCode;
        }

        public void setSagaCode(String sagaCode) {
            this.sagaCode = sagaCode;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getSagaInstanceId() {
            return sagaInstanceId;
        }

        public void setSagaInstanceId(Long sagaInstanceId) {
            this.sagaInstanceId = sagaInstanceId;
        }

        public String getTaskCode() {
            return taskCode;
        }

        public void setTaskCode(String taskCode) {
            this.taskCode = taskCode;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getInputData() {
            return inputData;
        }

        public void setInputData(String inputData) {
            this.inputData = inputData;
        }

        public String getOutputData() {
            return outputData;
        }

        public void setOutputData(String outputData) {
            this.outputData = outputData;
        }

        public String getInstanceLock() {
            return instanceLock;
        }

        public void setInstanceLock(String instanceLock) {
            this.instanceLock = instanceLock;
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

        public Integer getRetriedCount() {
            return retriedCount;
        }

        public void setRetriedCount(Integer retriedCount) {
            this.retriedCount = retriedCount;
        }

        public SagaTaskInstanceDTO() {
        }

        public SagaTaskInstanceDTO(Long id) {
            this.id = id;
        }

        public SagaTaskInstanceDTO(Long id, String sagaCode, String taskCode) {
            this.id = id;
            this.sagaCode = sagaCode;
            this.taskCode = taskCode;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SagaTaskInstanceDTO that = (SagaTaskInstanceDTO) o;
            return Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public String toString() {
            return "SagaTaskInstanceDTO{" +
                    "id=" + id +
                    ", sagaInstanceId=" + sagaInstanceId +
                    ", sagaCode='" + sagaCode + '\'' +
                    ", taskCode='" + taskCode + '\'' +
                    ", status='" + status + '\'' +
                    ", inputData='" + inputData + '\'' +
                    ", outputData='" + outputData + '\'' +
                    ", instanceLock='" + instanceLock + '\'' +
                    ", seq=" + seq +
                    ", maxRetryCount=" + maxRetryCount +
                    ", retriedCount=" + retriedCount +
                    '}';
        }
    }

    public static class SagaTaskInstanceStatusDTO {

        private Long id;

        private String status;

        private String outputData;

        private String exceptionMessage;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getOutputData() {
            return outputData;
        }

        public void setOutputData(String outputData) {
            this.outputData = outputData;
        }

        public String getExceptionMessage() {
            return exceptionMessage;
        }

        public void setExceptionMessage(String exceptionMessage) {
            this.exceptionMessage = exceptionMessage;
        }

        public SagaTaskInstanceStatusDTO() {
        }

        public SagaTaskInstanceStatusDTO(Long id, String status, String outputData) {
            this.id = id;
            this.status = status;
            this.outputData = outputData;
        }

        @Override
        public String toString() {
            return "SagaTaskInstanceStatusDTO{" +
                    "id=" + id +
                    ", status='" + status + '\'' +
                    '}';
        }
    }

    public static class StartInstanceDTO {
        private String sagaCode;
        private String input;
        private String refType;
        private Long refId;

        public String getSagaCode() {
            return sagaCode;
        }

        public void setSagaCode(String sagaCode) {
            this.sagaCode = sagaCode;
        }

        public String getInput() {
            return input;
        }

        public void setInput(String input) {
            this.input = input;
        }

        public String getRefType() {
            return refType;
        }

        public void setRefType(String refType) {
            this.refType = refType;
        }

        public Long getRefId() {
            return refId;
        }

        public void setRefId(Long refId) {
            this.refId = refId;
        }

        public StartInstanceDTO() {
        }

        public StartInstanceDTO(String input, String refType, Long refId) {
            this.input = input;
            this.refType = refType;
            this.refId = refId;
        }

        @Override
        public String toString() {
            return "StartInstanceDTO{" +
                    "sagaCode='" + sagaCode + '\'' +
                    ", refType='" + refType + '\'' +
                    ", refId=" + refId +
                    '}';
        }
    }

    public static class SagaInstance {
        private Long id;

        private String sagaCode;

        private String status;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private Date startTime;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private Date endTime;

        private Long inputDataId;

        private Long outputDataId;

        private String refType;

        private Long refId;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private Date creationDate;

        private Long createdBy;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private Date lastUpdateDate;

        private Long lastUpdatedBy;

        private Long objectVersionNumber;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getSagaCode() {
            return sagaCode;
        }

        public void setSagaCode(String sagaCode) {
            this.sagaCode = sagaCode;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Date getStartTime() {
            return startTime;
        }

        public void setStartTime(Date startTime) {
            this.startTime = startTime;
        }

        public Date getEndTime() {
            return endTime;
        }

        public void setEndTime(Date endTime) {
            this.endTime = endTime;
        }

        public Long getInputDataId() {
            return inputDataId;
        }

        public void setInputDataId(Long inputDataId) {
            this.inputDataId = inputDataId;
        }

        public Long getOutputDataId() {
            return outputDataId;
        }

        public void setOutputDataId(Long outputDataId) {
            this.outputDataId = outputDataId;
        }

        public String getRefType() {
            return refType;
        }

        public void setRefType(String refType) {
            this.refType = refType;
        }

        public Long getRefId() {
            return refId;
        }

        public void setRefId(Long refId) {
            this.refId = refId;
        }

        public Date getCreationDate() {
            return creationDate;
        }

        public void setCreationDate(Date creationDate) {
            this.creationDate = creationDate;
        }

        public Long getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(Long createdBy) {
            this.createdBy = createdBy;
        }

        public Date getLastUpdateDate() {
            return lastUpdateDate;
        }

        public void setLastUpdateDate(Date lastUpdateDate) {
            this.lastUpdateDate = lastUpdateDate;
        }

        public Long getLastUpdatedBy() {
            return lastUpdatedBy;
        }

        public void setLastUpdatedBy(Long lastUpdatedBy) {
            this.lastUpdatedBy = lastUpdatedBy;
        }

        public Long getObjectVersionNumber() {
            return objectVersionNumber;
        }

        public void setObjectVersionNumber(Long objectVersionNumber) {
            this.objectVersionNumber = objectVersionNumber;
        }
    }

    public static class PollBatchDTO {

        private String instance;

        private Set<String> codes;

        public String getInstance() {
            return instance;
        }

        public void setInstance(String instance) {
            this.instance = instance;
        }

        public Set<String> getCodes() {
            return codes;
        }

        public void setCodes(Set<String> codes) {
            this.codes = codes;
        }

        public PollBatchDTO() {
        }

        public PollBatchDTO(String instance, Set<String> codes) {
            this.instance = instance;
            this.codes = codes;
        }

        @Override
        public String toString() {
            return "PollBatchDTO{" +
                    "instance='" + instance + '\'' +
                    ", codes=" + codes +
                    '}';
        }
    }

}
