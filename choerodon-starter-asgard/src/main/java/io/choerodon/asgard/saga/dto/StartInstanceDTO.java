package io.choerodon.asgard.saga.dto;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    private String level;

    private Long sourceId;

    public StartInstanceDTO(String input) {
        this.input = input;
    }

    public StartInstanceDTO(String input, String refType, String refId) {
        this.input = input;
        this.refType = refType;
        this.refId = refId;
    }

    public StartInstanceDTO(String input, String refType, String refId, String level, Long sourceId) {
        this.input = input;
        this.refType = refType;
        this.refId = refId;
        this.level = level;
        this.sourceId = sourceId;
    }
}
