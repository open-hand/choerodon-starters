package io.choerodon.asgard.schedule;

import io.choerodon.asgard.schedule.annotation.JobTask;

import java.lang.reflect.Method;

public class JobTaskInvokeBean {

    final Method method;
    final Object object;
    final JobTask jobTask;
    final String key;

    public JobTaskInvokeBean(Method method, Object object, JobTask jobTask, String key) {
        this.method = method;
        this.object = object;
        this.jobTask = jobTask;
        this.key = key;
    }
}
