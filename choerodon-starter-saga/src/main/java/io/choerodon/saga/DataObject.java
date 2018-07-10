package io.choerodon.saga;

public class DataObject {

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
                    ", outputData='" + outputData + '\'' +
                    ", exceptionMessage='" + exceptionMessage + '\'' +
                    '}';
        }
    }

}
