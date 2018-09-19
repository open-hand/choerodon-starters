package io.choerodon.asgard.saga.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class SagaTaskInstanceDTO {
    private Long id;

    private Long sagaInstanceId;

    private String taskCode;

    private String sagaCode;

    private String instanceLock;

    private String status;

    private Integer seq;

    private Integer maxRetryCount;

    private Integer retriedCount;

    private Integer timeoutSeconds;

    private String timeoutPolicy;

    private String exceptionMessage;

    private String refType;

    private String refId;

    private Integer concurrentLimitNum;

    private String concurrentLimitPolicy;

    private String input;

    private String output;

    private String creationDate;

    private String description;

    private String service;

    private Date plannedStartTime;

    private Date actualStartTime;

    private Date actualEndTime;

    private Long objectVersionNumber;
}
