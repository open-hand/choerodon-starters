package io.choerodon.asgard.saga;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "choerodon.saga")
public class SagaProperties {

    private Consumer consumer;

    private Producer producer;


    public Consumer getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer consumer) {
        this.consumer = consumer;
    }

    public Producer getProducer() {
        return producer;
    }

    public void setProducer(Producer producer) {
        this.producer = producer;
    }

    public static class Consumer {
        private Long pollIntervalMs = 1000L;

        private Integer maxPollSize = 200;

        private Integer coreThreadNum = 5;

        private Integer maxThreadNum = 10;

        private Boolean enabled = false;

        public Long getPollIntervalMs() {
            return pollIntervalMs;
        }

        public void setPollIntervalMs(Long pollIntervalMs) {
            this.pollIntervalMs = pollIntervalMs;
        }

        public Integer getMaxPollSize() {
            return maxPollSize;
        }

        public void setMaxPollSize(Integer maxPollSize) {
            this.maxPollSize = maxPollSize;
        }

        public Integer getCoreThreadNum() {
            return coreThreadNum;
        }

        public void setCoreThreadNum(Integer coreThreadNum) {
            this.coreThreadNum = coreThreadNum;
        }

        public Integer getMaxThreadNum() {
            return maxThreadNum;
        }

        public void setMaxThreadNum(Integer maxThreadNum) {
            this.maxThreadNum = maxThreadNum;
        }

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }
    }

    public static class Producer {
        private String consistencyType = "memory";

        public String getConsistencyType() {
            return consistencyType;
        }

        public void setConsistencyType(String consistencyType) {
            this.consistencyType = consistencyType;
        }
    }

}
