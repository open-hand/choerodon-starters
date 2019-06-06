package io.choerodon.actuator.dataset;

import io.choerodon.actuator.dataset.domain.DatabasePageAction;

public interface DatabaseActionExecutor {
    void process(DatabasePageAction action);
}
