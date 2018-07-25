package io.choerodon.asgard.saga.dto;

import lombok.*;

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

    private Long userId;

    public StartInstanceDTO(String input, Long userId) {
        this.input = input;
        this.userId = userId;
    }

    public StartInstanceDTO(String input, Long userId, String refType, String refId) {
        this.input = input;
        this.userId = userId;
        this.refType = refType;
        this.refId = refId;
    }
}
