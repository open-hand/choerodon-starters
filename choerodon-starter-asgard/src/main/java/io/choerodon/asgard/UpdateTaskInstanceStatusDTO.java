package io.choerodon.asgard;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class UpdateTaskInstanceStatusDTO {

    @NotNull
    private Long id;

    @NotEmpty
    private String status;

    private String output;

    private String exceptionMessage;

    public UpdateTaskInstanceStatusDTO(Long id, String status, String output, String exceptionMessage) {
        this.id = id;
        this.status = status;
        this.output = output;
        this.exceptionMessage = exceptionMessage;
    }
}
