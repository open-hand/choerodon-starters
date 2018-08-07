package io.choerodon.asgard.saga;

import io.choerodon.asgard.saga.annotation.SagaTask;

import java.lang.reflect.Method;

class SagaTaskInvokeBean {

    final Method method;
    final Object object;
    final SagaTask sagaTask;
    final String key;

    SagaTaskInvokeBean(Method method, Object object, SagaTask sagaTask, String key) {
        this.method = method;
        this.object = object;
        this.sagaTask = sagaTask;
        this.key = key;
    }
}
