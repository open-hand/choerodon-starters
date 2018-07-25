package io.choerodon.asgard.saga.dto;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class SagaTaskInstanceStatusDTO {

    @NotNull
    private Long id;

    @NotEmpty
    private String status;

    private String output;

    private String exceptionMessage;

    public SagaTaskInstanceStatusDTO(Long id, String status, String output, String exceptionMessage) {
        this.id = id;
        this.status = status;
        this.output = output;
        this.exceptionMessage = exceptionMessage;
    }
}
