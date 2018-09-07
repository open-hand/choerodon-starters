package io.choerodon.asgard.property;

import io.choerodon.asgard.schedule.annotation.JobParam;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PropertyJobTask {

    private String method;

    private int maxRetryCount;

    private List<PropertyJobParam> params;

    public PropertyJobTask(String method, int maxRetryCount, JobParam[] params) {
        this.method = method;
        this.maxRetryCount = maxRetryCount;
        this.params = new ArrayList<>();
        for (JobParam jobParam : params) {
            this.params.add(new PropertyJobParam(jobParam));
        }
    }

}
