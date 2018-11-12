package io.choerodon.statemachine.dto;

import com.google.common.base.MoreObjects;

/**
 * @author peng.jiang@hand-china.com
 * @author dinghuang123@gmail.com
 * @since 2018/10/23
 */
public class StateMachineConfigDTO {

    private Long id;

    private Long transfId;

    private Long stateMachineId;

    private String code;

    private String type;

    /**
     * 条件描述
     */
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTransfId() {
        return transfId;
    }

    public void setTransfId(Long transfId) {
        this.transfId = transfId;
    }

    public Long getStateMachineId() {
        return stateMachineId;
    }

    public void setStateMachineId(Long stateMachineId) {
        this.stateMachineId = stateMachineId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("transfId", transfId)
                .add("stateMachineId", stateMachineId)
                .add("code", code)
                .add("type", type)
                .add("description", description)
                .toString();
    }
}
