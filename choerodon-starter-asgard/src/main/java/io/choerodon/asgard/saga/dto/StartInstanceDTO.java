package io.choerodon.asgard.saga.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StartInstanceDTO {

    private String sagaCode;

    private String input;

    @NotNull
    private String refType = "";

    @NotNull
    private String refId = "";

    private Long userId;

    public StartInstanceDTO(String sagaCode, String input, Long userId) {
        this.sagaCode = sagaCode;
        this.input = input;
        this.userId = userId;
    }

}
