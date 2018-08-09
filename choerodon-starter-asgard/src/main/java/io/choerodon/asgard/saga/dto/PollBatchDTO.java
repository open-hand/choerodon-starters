package io.choerodon.asgard.saga.dto;

import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PollBatchDTO {

    @NotEmpty
    private String instance;

    @NotNull
    private List<PollCodeDTO> codes;

    private Integer maxPollSize;

}
