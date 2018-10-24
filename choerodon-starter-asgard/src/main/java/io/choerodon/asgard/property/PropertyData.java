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
    private String code;
    private String description;

    private List<PropertySaga> sagas = new ArrayList<>();

    private List<PropertySagaTask> sagaTasks = new ArrayList<>();

    private List<PropertyJobTask> jobTasks = new ArrayList<>();

    private List<PropertyTimedTask> timedTasks = new ArrayList<>();

    public void addSaga(PropertySaga saga) {
        this.sagas.add(saga);
    }

    public void addSagaTask(PropertySagaTask sagaTask) {
        this.sagaTasks.add(sagaTask);
    }

    public void addJobTask(PropertyJobTask jobTask) {
        this.jobTasks.add(jobTask);
    }

    public void addTimedTasks(PropertyTimedTask timedTask) {
        this.timedTasks.add(timedTask);
    }

}
