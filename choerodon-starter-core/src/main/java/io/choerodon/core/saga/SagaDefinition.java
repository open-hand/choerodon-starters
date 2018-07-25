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
        STATUS_RUNNING,
        STATUS_ROLLBACK,
        STATUS_FAILED,
        STATUS_COMPLETED,
        STATUS_NON_CONSUMER
    }

    public enum TaskInstanceStatus {
        STATUS_RUNNING,
        STATUS_ROLLBACK,
        STATUS_FAILED,
        STATUS_COMPLETED,
        QUEUE
    }

}
