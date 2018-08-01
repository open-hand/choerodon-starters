package io.choerodon.asgard.saga.dto;

import lombok.*;

@Getter
@Setter
@ToString
public class PollCodeDTO {

    private String sagaCode;
    private String taskCode;

    public PollCodeDTO(String sagaCode, String taskCode) {
        this.sagaCode = sagaCode;
        this.taskCode = taskCode;
    }

    public PollCodeDTO() {
    }
}
