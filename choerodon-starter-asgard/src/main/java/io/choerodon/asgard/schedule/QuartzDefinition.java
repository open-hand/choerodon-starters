package io.choerodon.asgard.schedule;

public class QuartzDefinition {

    public enum InstanceStatus {
        RUNNING,
        FAILED,
        COMPLETED,
    }

    public enum TaskStatus {
        ENABLE,
        DISABLE,
        FINISHED,
    }

    public enum SimpleRepeatIntervalUnit {
        SECONDS,
        MINUTES,
        HOURS,
        DAYS,
    }
}
