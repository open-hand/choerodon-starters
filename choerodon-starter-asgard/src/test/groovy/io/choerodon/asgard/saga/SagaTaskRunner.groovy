package io.choerodon.asgard.saga

import com.fasterxml.jackson.databind.ObjectMapper
import io.choerodon.asgard.saga.annotation.SagaTask
import io.choerodon.core.exception.CommonException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class SagaTaskRunner {

    def LOGGER = LoggerFactory.getLogger(SagaTaskRunner)

    private final ObjectMapper objectMapper = new ObjectMapper();

    @SagaTask(code = "sagaTaskOne",
            sagaCode = "sagaOne",
            description = "sagaTaskOne",
            enabledDbRecord = true,
            maxRetryCount = 10,
            timeoutSeconds = 10,
            timeoutPolicy = SagaDefinition.TimeoutPolicy.ALERT_ONLY,
            seq = 1,
            concurrentLimitNum = 2,
            concurrentLimitPolicy = SagaDefinition.ConcurrentLimitPolicy.TYPE)
    Data testTask(String json) {
        def data = objectMapper.readValue(json, Data)
        LOGGER.info("data: {}", data)
        return data
    }

    @SagaTask(code = "sagaTaskTwo",
            sagaCode = "sagaOne",
            description = "sagaTaskOne",
            enabledDbRecord = true,
            maxRetryCount = 10,
            timeoutSeconds = 10,
            timeoutPolicy = SagaDefinition.TimeoutPolicy.ALERT_ONLY,
            seq = 1,
            concurrentLimitNum = 2,
            concurrentLimitPolicy = SagaDefinition.ConcurrentLimitPolicy.TYPE)
    Data testTaskException(String json) {
        throw new CommonException(json)
    }

    static class Data {
        private String name
        private Integer age

        Data() {
        }

        Data(String name, Integer age) {
            this.name = name
            this.age = age
        }

        String getName() {
            return name
        }

        void setName(String name) {
            this.name = name
        }

        Integer getAge() {
            return age
        }

        void setAge(Integer age) {
            this.age = age
        }

        @Override
        String toString() {
            return "Data{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}'
        }
    }

}
