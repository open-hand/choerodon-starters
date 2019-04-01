package io.choerodon.dataset.dto;

import io.choerodon.dataset.service.IDatasetService;

public class DatasetService {
    private IDatasetService service;

    public IDatasetService getService() {
        return service;
    }

    public void setService(IDatasetService service) {
        this.service = service;
    }

}
