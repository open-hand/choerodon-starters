package io.choerodon.asgard.property;

import java.util.ArrayList;
import java.util.List;

public class PropertyData {

    private String service;

    private List<PropertySaga> sagas = new ArrayList<>();

    private List<PropertySagaTask> sagaTasks = new ArrayList<>();

    private List<PropertyJobTask> jobTasks = new ArrayList<>();

    public List<PropertySaga> getSagas() {
        return sagas;
    }

    public void addSaga(PropertySaga saga) {
        this.sagas.add(saga);
    }

    public List<PropertySagaTask> getSagaTasks() {
        return sagaTasks;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public void addSagaTask(PropertySagaTask sagaTask) {
        this.sagaTasks.add(sagaTask);
    }

    public List<PropertyJobTask> getJobTasks() {
        return jobTasks;
    }

    public void addJobTask(PropertyJobTask jobTask) {
        this.jobTasks.add(jobTask);
    }

    @Override
    public String toString() {
        return "PropertyData{" +
                "sagas=" + sagas +
                ", sagaTasks=" + sagaTasks +
                '}';
    }

}
