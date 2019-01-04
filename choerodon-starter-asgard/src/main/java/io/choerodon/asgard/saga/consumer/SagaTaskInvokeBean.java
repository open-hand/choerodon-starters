package io.choerodon.asgard.saga.consumer;

import io.choerodon.asgard.saga.annotation.SagaTask;

import java.lang.reflect.Method;

public class SagaTaskInvokeBean {

    public final Method method;
    public final Object object;
    public final SagaTask sagaTask;
    public final String key;

    SagaTaskInvokeBean(Method method, Object object, SagaTask sagaTask, String key) {
        this.method = method;
        this.object = object;
        this.sagaTask = sagaTask;
        this.key = key;
    }
}
