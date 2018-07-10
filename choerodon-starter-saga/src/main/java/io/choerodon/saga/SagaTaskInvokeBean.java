package io.choerodon.saga;

import io.choerodon.core.saga.SagaTask;

import java.lang.reflect.Method;

public class SagaTaskInvokeBean {

    public final Method method;
    public final Object object;
    public final SagaTask sagaTask;
    public final String key;

    public SagaTaskInvokeBean(Method method, Object object, SagaTask sagaTask, String key) {
        this.method = method;
        this.object = object;
        this.sagaTask = sagaTask;
        this.key = key;
    }
}
