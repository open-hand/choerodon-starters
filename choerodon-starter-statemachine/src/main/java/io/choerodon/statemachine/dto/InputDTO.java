package io.choerodon.statemachine.dto;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2018/11/8
 */
public class InputDTO {
    private Long instanceId;
    private String invokeCode;
    private String input;
    private List<StateMachineConfigDTO> configs;

    public InputDTO() {
    }

    public InputDTO(Long instanceId, String invokeCode, String input) {
        this.instanceId = instanceId;
        this.invokeCode = invokeCode;
        this.input = input;
    }

    public String getInvokeCode() {
        return invokeCode;
    }

    public void setInvokeCode(String invokeCode) {
        this.invokeCode = invokeCode;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

    public List<StateMachineConfigDTO> getConfigs() {
        return configs;
    }

    public void setConfigs(List<StateMachineConfigDTO> configs) {
        this.configs = configs;
    }
}
