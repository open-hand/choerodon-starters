package io.choerodon.swagger.property;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PropertyData {

    private List<Saga> sagas = new ArrayList<>();

    private List<SagaTask> sagaTasks = new ArrayList<>();

    public List<Saga> getSagas() {
        return sagas;
    }

    public void addSaga(Saga saga) {
        this.sagas.add(saga);
    }

    public List<SagaTask> getSagaTasks() {
        return sagaTasks;
    }


    public void addSagaTask(SagaTask sagaTask) {
        this.sagaTasks.add(sagaTask);
    }

    static class Saga {

        private String code;

        private String description;

        private List<String> inputKeys;

        private List<String> outputKeys;

        public Saga() {
        }

        public Saga(String code, String description, String[] inputKeys, String[] outputKeys) {
            this.code = code;
            this.description = description;
            this.inputKeys = Arrays.asList(inputKeys);
            this.outputKeys = Arrays.asList(outputKeys);
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

        public List<String> getInputKeys() {
            return inputKeys;
        }

        public void setInputKeys(List<String> inputKeys) {
            this.inputKeys = inputKeys;
        }

        public List<String> getOutputKeys() {
            return outputKeys;
        }

        public void setOutputKeys(List<String> outputKeys) {
            this.outputKeys = outputKeys;
        }
    }

    static class SagaTask {

        private String code;

        private String description;

        private String sagaCode;

        private Integer seq;

        private Integer concurrentExecLimit;

        private Integer maxRetryCount;

        public SagaTask() {
        }

        public SagaTask(String code, String description, String sagaCode, Integer seq,
                        Integer concurrentExecLimit, Integer maxRetryCount) {
            this.code = code;
            this.description = description;
            this.sagaCode = sagaCode;
            this.seq = seq;
            this.concurrentExecLimit = concurrentExecLimit;
            this.maxRetryCount = maxRetryCount;
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

        public Integer getConcurrentExecLimit() {
            return concurrentExecLimit;
        }

        public void setConcurrentExecLimit(Integer concurrentExecLimit) {
            this.concurrentExecLimit = concurrentExecLimit;
        }

        public Integer getMaxRetryCount() {
            return maxRetryCount;
        }

        public void setMaxRetryCount(Integer maxRetryCount) {
            this.maxRetryCount = maxRetryCount;
        }
    }

}
