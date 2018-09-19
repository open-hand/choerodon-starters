package io.choerodon.asgard.property;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import io.choerodon.asgard.schedule.annotation.JobParam;

@Getter
@Setter
@NoArgsConstructor
public class PropertyJobTask {

    private String method;

    private int maxRetryCount;

    private String code;

    private String description;

    private List<PropertyJobParam> params;

    public PropertyJobTask(String method, int maxRetryCount, String code, String description, JobParam[] params) {
        this.method = method;
        this.maxRetryCount = maxRetryCount;
        this.code = code;
        this.description = description;
        this.params = new ArrayList<>(params.length);
        for (JobParam jobParam : params) {
            this.params.add(new PropertyJobParam(jobParam));
        }
    }
}
