package io.choerodon.core.saga;

public class SagaDefinition {

    public enum TimeoutPolicy {
        RETRY,
        TIME_OUT_WF,
        ALERT_ONLY
    }

    public enum ConcurrentLimitPolicy {
        NONE,
        TYPE,
        TYPE_AND_ID
    }

    public enum InstanceStatus {
        RUNNING,
        ROLLBACK,
        FAILED,
        COMPLETED,
        NON_CONSUMER
    }

    public enum TaskInstanceStatus {
        RUNNING,
        ROLLBACK,
        FAILED,
        COMPLETED,
        QUEUE
    }

}
