package io.choerodon.event.consumer;

import java.util.Collections;
import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerInterceptor;
import org.apache.kafka.common.metrics.MetricsReporter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * event consume的配置property bean
 * @author zhipeng.zuo
 * 17-11-7
 */

@ConfigurationProperties(prefix = "choerodon.event.consumer")
public class EventConsumerProperties {

    public static final String FAILED_STRATEGY_NOTHING = "nothing";

    public static final String FAILED_STRATEGY_EVENT_STORE = "send_back_event_store";

    private boolean enabled = true;

    private String queueType = "kafka";

    private boolean enableDuplicateRemove = true;

    private String failedStrategy = FAILED_STRATEGY_EVENT_STORE;

    @NestedConfigurationProperty
    private Kafka kafka = new Kafka();

    @NestedConfigurationProperty
    private Rocketmq rocketmq = new Rocketmq();

    @NestedConfigurationProperty
    private Retry retry = new Retry();

    public Kafka getKafka() {
        return kafka;
    }

    public void setKafka(Kafka kafka) {
        this.kafka = kafka;
    }

    public Rocketmq getRocketmq() {
        return rocketmq;
    }

    public void setRocketmq(Rocketmq rocketmq) {
        this.rocketmq = rocketmq;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getQueueType() {
        return queueType;
    }

    public void setQueueType(String queueType) {
        this.queueType = queueType;
    }

    public boolean isEnableDuplicateRemove() {
        return enableDuplicateRemove;
    }

    public void setEnableDuplicateRemove(boolean enableDuplicateRemove) {
        this.enableDuplicateRemove = enableDuplicateRemove;
    }

    public String getFailedStrategy() {
        return failedStrategy;
    }

    public void setFailedStrategy(String failedStrategy) {
        this.failedStrategy = failedStrategy;
    }

    public Retry getRetry() {
        return retry;
    }

    public void setRetry(Retry retry) {
        this.retry = retry;
    }

    @Override
    public String toString() {
        return "EventConsumerProperties{"
                + "enabled=" + enabled
                + ", queueType='" + queueType + '\''
                + ", enableDuplicateRemove=" + enableDuplicateRemove
                + ", failedStrategy='" + failedStrategy + '\''
                + ", kafka=" + kafka
                + ", rocketmq=" + rocketmq
                + ", retry=" + retry
                + '}';
    }

    public static class Kafka {
        private String bootstrapServers = "localhost:9092";
        private int sessionTimeoutMs = 30000;
        private int maxPollRecords = 500;
        private int heartbeatIntervalMs = 3000;
        private int fetchMaxBytes = 52428800;
        private int fetchMaxWaitMs = 500;
        private int maxPollIntervalMs = 300000;
        private String autoOffsetReset = "earliest";
        private int fetchMinBytes = 1;
        private int sendBufferBytes = 128 * 1024;
        private int receiveBufferBytes = 64 * 1024;
        private String clientId = "";
        private long reconnectBackoffMs = 50L;
        private long reconnectBackoffMaxMs = 1000L;
        private long retryBackoffMs = 100L;
        private long metricsSampleWindowMs = 30000L;
        private int metricsNumSample = 2;
        private String metricsRecordingLevel = "INFO";
        private List<MetricsReporter> metricReporters = Collections.emptyList();
        private String securityProtocol = "PLAINTEXT";
        private long connectionsMaxIdleMs = 54000;
        private int requestTimeoutMs = 305000;
        private boolean checkCrcs = true;
        private List<ConsumerInterceptor> interceptorClasses = Collections.emptyList();
        private boolean excludeInternalTopics = true;
        private String isolationLevel = "read_uncommitted";
        private String partitionAssignmentStrategy = "org.apache.kafka.clients.consumer.RangeAssignor";

        public String getBootstrapServers() {
            return bootstrapServers;
        }

        public void setBootstrapServers(String bootstrapServers) {
            this.bootstrapServers = bootstrapServers;
        }

        public int getSessionTimeoutMs() {
            return sessionTimeoutMs;
        }

        public void setSessionTimeoutMs(int sessionTimeoutMs) {
            this.sessionTimeoutMs = sessionTimeoutMs;
        }

        public int getMaxPollRecords() {
            return maxPollRecords;
        }

        public void setMaxPollRecords(int maxPollRecords) {
            this.maxPollRecords = maxPollRecords;
        }

        public int getHeartbeatIntervalMs() {
            return heartbeatIntervalMs;
        }

        public void setHeartbeatIntervalMs(int heartbeatIntervalMs) {
            this.heartbeatIntervalMs = heartbeatIntervalMs;
        }

        public int getFetchMaxBytes() {
            return fetchMaxBytes;
        }

        public void setFetchMaxBytes(int fetchMaxBytes) {
            this.fetchMaxBytes = fetchMaxBytes;
        }

        public int getFetchMaxWaitMs() {
            return fetchMaxWaitMs;
        }

        public void setFetchMaxWaitMs(int fetchMaxWaitMs) {
            this.fetchMaxWaitMs = fetchMaxWaitMs;
        }

        public int getMaxPollIntervalMs() {
            return maxPollIntervalMs;
        }

        public void setMaxPollIntervalMs(int maxPollIntervalMs) {
            this.maxPollIntervalMs = maxPollIntervalMs;
        }

        public String getPartitionAssignmentStrategy() {
            return partitionAssignmentStrategy;
        }

        public void setPartitionAssignmentStrategy(String partitionAssignmentStrategy) {
            this.partitionAssignmentStrategy = partitionAssignmentStrategy;
        }

        public String getAutoOffsetReset() {
            return autoOffsetReset;
        }

        public void setAutoOffsetReset(String autoOffsetReset) {
            this.autoOffsetReset = autoOffsetReset;
        }

        public int getFetchMinBytes() {
            return fetchMinBytes;
        }

        public void setFetchMinBytes(int fetchMinBytes) {
            this.fetchMinBytes = fetchMinBytes;
        }

        public int getSendBufferBytes() {
            return sendBufferBytes;
        }

        public void setSendBufferBytes(int sendBufferBytes) {
            this.sendBufferBytes = sendBufferBytes;
        }

        public int getReceiveBufferBytes() {
            return receiveBufferBytes;
        }

        public void setReceiveBufferBytes(int receiveBufferBytes) {
            this.receiveBufferBytes = receiveBufferBytes;
        }

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public long getReconnectBackoffMs() {
            return reconnectBackoffMs;
        }

        public void setReconnectBackoffMs(long reconnectBackoffMs) {
            this.reconnectBackoffMs = reconnectBackoffMs;
        }

        public long getReconnectBackoffMaxMs() {
            return reconnectBackoffMaxMs;
        }

        public void setReconnectBackoffMaxMs(long reconnectBackoffMaxMs) {
            this.reconnectBackoffMaxMs = reconnectBackoffMaxMs;
        }

        public long getRetryBackoffMs() {
            return retryBackoffMs;
        }

        public void setRetryBackoffMs(long retryBackoffMs) {
            this.retryBackoffMs = retryBackoffMs;
        }

        public long getMetricsSampleWindowMs() {
            return metricsSampleWindowMs;
        }

        public void setMetricsSampleWindowMs(long metricsSampleWindowMs) {
            this.metricsSampleWindowMs = metricsSampleWindowMs;
        }

        public int getMetricsNumSample() {
            return metricsNumSample;
        }

        public void setMetricsNumSample(int metricsNumSample) {
            this.metricsNumSample = metricsNumSample;
        }

        public String getMetricsRecordingLevel() {
            return metricsRecordingLevel;
        }

        public void setMetricsRecordingLevel(String metricsRecordingLevel) {
            this.metricsRecordingLevel = metricsRecordingLevel;
        }

        public List<MetricsReporter> getMetricReporters() {
            return metricReporters;
        }

        public void setMetricReporters(List<MetricsReporter> metricReporters) {
            this.metricReporters = metricReporters;
        }

        public String getSecurityProtocol() {
            return securityProtocol;
        }

        public void setSecurityProtocol(String securityProtocol) {
            this.securityProtocol = securityProtocol;
        }

        public long getConnectionsMaxIdleMs() {
            return connectionsMaxIdleMs;
        }

        public void setConnectionsMaxIdleMs(long connectionsMaxIdleMs) {
            this.connectionsMaxIdleMs = connectionsMaxIdleMs;
        }

        public int getRequestTimeoutMs() {
            return requestTimeoutMs;
        }

        public void setRequestTimeoutMs(int requestTimeoutMs) {
            this.requestTimeoutMs = requestTimeoutMs;
        }

        public boolean isCheckCrcs() {
            return checkCrcs;
        }

        public void setCheckCrcs(boolean checkCrcs) {
            this.checkCrcs = checkCrcs;
        }

        public List<ConsumerInterceptor> getInterceptorClasses() {
            return interceptorClasses;
        }

        public void setInterceptorClasses(List<ConsumerInterceptor> interceptorClasses) {
            this.interceptorClasses = interceptorClasses;
        }

        public boolean isExcludeInternalTopics() {
            return excludeInternalTopics;
        }

        public void setExcludeInternalTopics(boolean excludeInternalTopics) {
            this.excludeInternalTopics = excludeInternalTopics;
        }

        public String getIsolationLevel() {
            return isolationLevel;
        }

        public void setIsolationLevel(String isolationLevel) {
            this.isolationLevel = isolationLevel;
        }

    }

    public static class Rocketmq {

        private String namesrvAddr = "127.0.0.1:9876";
        private int consumeThreadMin = 1;
        private int consumeThreadMax = 2;

        public String getNamesrvAddr() {
            return namesrvAddr;
        }

        public void setNamesrvAddr(String namesrvAddr) {
            this.namesrvAddr = namesrvAddr;
        }

        public int getConsumeThreadMin() {
            return consumeThreadMin;
        }

        public void setConsumeThreadMin(int consumeThreadMin) {
            this.consumeThreadMin = consumeThreadMin;
        }

        public int getConsumeThreadMax() {
            return consumeThreadMax;
        }

        public void setConsumeThreadMax(int consumeThreadMax) {
            this.consumeThreadMax = consumeThreadMax;
        }

        @Override
        public String toString() {
            return "Rocketmq{"
                    + "namesrvAddr='" + namesrvAddr + '\''
                    + ", consumeThreadMin=" + consumeThreadMin
                    + ", consumeThreadMax=" + consumeThreadMax
                    + '}';
        }
    }

    public static class Retry {

        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        @Override
        public String toString() {
            return "Retry{"
                    + "enabled=" + enabled
                    + '}';
        }
    }
}
