package io.choerodon.asgard.saga.dto;

import lombok.Data;

@Data
public class SagaTaskInstanceDTO {

    private Long id;

    private Long sagaInstanceId;

    private String sagaCode;

    private String taskCode;

    private String status;

    private String input;

    private String output;

    private String instanceLock;

    private Integer seq;

    private String refType;

    private String refId;

    private String creationDate;

    private Integer concurrentLimitNum;


}
