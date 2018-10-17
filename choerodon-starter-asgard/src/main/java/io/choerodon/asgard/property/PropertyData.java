package io.choerodon.asgard.property;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class PropertyData {

    private String service;

    private List<PropertySaga> sagas = new ArrayList<>();

    private List<PropertySagaTask> sagaTasks = new ArrayList<>();

    private List<PropertyJobTask> jobTasks = new ArrayList<>();

    public void addSaga(PropertySaga saga) {
        this.sagas.add(saga);
    }

    public void addSagaTask(PropertySagaTask sagaTask) {
        this.sagaTasks.add(sagaTask);
    }

    public void addJobTask(PropertyJobTask jobTask) {
        this.jobTasks.add(jobTask);
    }

}
