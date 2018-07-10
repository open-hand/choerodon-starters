package io.choerodon.saga;

import java.util.concurrent.ScheduledExecutorService;

public class SagaMonitor {

    private ChoerodonSagaProperties choerodonSagaProperties;

    private ScheduledExecutorService ses;

    private SagaClient sagaClient;

    public SagaMonitor(ChoerodonSagaProperties choerodonSagaProperties, SagaClient sagaClient) {

    }
}
