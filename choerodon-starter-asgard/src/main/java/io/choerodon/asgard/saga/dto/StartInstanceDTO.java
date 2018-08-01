package io.choerodon.asgard.saga.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class StartInstanceDTO {

    private String sagaCode;

    private String input;

    @NotNull
    private String refType = "";

    @NotNull
    private String refId = "";

    public StartInstanceDTO(String input) {
        this.input = input;
    }

    public StartInstanceDTO(String input, String refType, String refId) {
        this.input = input;
        this.refType = refType;
        this.refId = refId;
    }

}
