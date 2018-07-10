package io.choerodon.core.saga;

public class SagaDef {

    public enum TimeoutPolicy {
        RETRY,
        TIME_OUT_WF,
        ALERT_ONLY
    }

    public enum InstanceStatus {
        STATUS_RUNNING,
        STATUS_ROLLBACK,
        STATUS_FAILED,
        STATUS_COMPLETED,
        STATUS_NON_CONSUMER
    }

    public enum TaskStatus {
        STATUS_RUNNING,
        STATUS_ROLLBACK,
        STATUS_FAILED,
        STATUS_COMPLETED,
        QUEUE
    }

}
