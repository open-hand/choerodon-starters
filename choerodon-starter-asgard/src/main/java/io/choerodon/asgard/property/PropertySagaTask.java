package io.choerodon.asgard.property;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class PropertySagaTask {

    private String code;

    private String description;

    private String sagaCode;

    private Integer seq;

    private Integer maxRetryCount;

    private Integer timeoutSeconds;

    private String timeoutPolicy;

    private Integer concurrentLimitNum;

    private String concurrentLimitPolicy;

    private String outputSchema;

    private String outputSchemaSource;


    public PropertySagaTask(String code, String description, String sagaCode, Integer seq, Integer maxRetryCount) {
        this.code = code;
        this.description = description;
        this.sagaCode = sagaCode;
        this.seq = seq;
        this.maxRetryCount = maxRetryCount;
    }

}