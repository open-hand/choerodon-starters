package io.choerodon.asgard.saga;

public class SagaDefinition {

    public enum TimeoutPolicy {
        RETRY,
        TIME_OUT_WF,
        ALERT_ONLY
    }

    public enum ConsistencyPolicy {
        ROLL_BACK,
        CONTINUE,
    }

    public enum ConcurrentLimitPolicy {
        NONE,
        TYPE,
        TYPE_AND_ID
    }

    public enum InstanceStatus {
        UN_CONFIRMED,
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
        QUEUE,
        WAIT_TO_BE_PULLED,
    }

    public enum SagaInputSchemaSource {
        INPUT_SCHEMA,
        INPUT_SCHEMA_CLASS,
        NONE
    }

    public enum SagaTaskOutputSchemaSource {
        OUTPUT_SCHEMA,
        OUTPUT_SCHEMA_CLASS,
        METHOD_RETURN_TYPE,
        NONE
    }

}
