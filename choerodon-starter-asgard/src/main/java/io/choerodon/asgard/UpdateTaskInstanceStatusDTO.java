package io.choerodon.asgard;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
public class UpdateTaskInstanceStatusDTO {

    private Long id;

    @NotEmpty
    private String status;

    private String output;

    private String exceptionMessage;

    private Long objectVersionNumber;

    public UpdateTaskInstanceStatusDTO(Long id, String status, String output, String exceptionMessage, Long objectVersionNumber) {
        this.id = id;
        this.status = status;
        this.output = output;
        this.exceptionMessage = exceptionMessage;
        this.objectVersionNumber = objectVersionNumber;
    }
}
