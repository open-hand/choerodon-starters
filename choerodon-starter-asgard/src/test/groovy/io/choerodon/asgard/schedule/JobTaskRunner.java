package io.choerodon.asgard.schedule;

import io.choerodon.asgard.schedule.annotation.JobParam;
import io.choerodon.asgard.schedule.annotation.JobTask;
import io.choerodon.core.exception.CommonException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class JobTaskRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobTaskRunner.class);

    @JobTask(code = "test",
            maxRetryCount = 2, params = {
            @JobParam(name = "isInstantly", defaultValue = "true", type = Boolean.class),
            @JobParam(name = "name", defaultValue = "zh"),
            @JobParam(name = "age", type = Integer.class)
    })
    public Map<String, Object> test(Map<String, Object> data) {
        LOGGER.info("data {}", data);
        return data;
    }

    @JobTask(code = "testException",
            maxRetryCount = 2, params = {
            @JobParam(name = "isInstantly", defaultValue = "true", type = Boolean.class),
            @JobParam(name = "name", defaultValue = "zh"),
            @JobParam(name = "age", type = Integer.class)
    })
    public Map<String, Object> testException(Map<String, Object> data) {
        throw new CommonException(data.toString());
    }

}
