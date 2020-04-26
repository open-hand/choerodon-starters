package io.choerodon.asgard.schedule;

import io.choerodon.asgard.schedule.annotation.JobTask;

import java.lang.reflect.Method;

class JobTaskInvokeBean {

    final Method method;
    final Object object;
    final JobTask jobTask;

    JobTaskInvokeBean(Method method, Object object, JobTask jobTask) {
        this.method = method;
        this.object = object;
        this.jobTask = jobTask;
    }
}
