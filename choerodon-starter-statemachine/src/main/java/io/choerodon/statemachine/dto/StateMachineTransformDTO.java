package io.choerodon.statemachine.dto;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/2/19
 */
public class StateMachineTransformDTO {
    private Long id;
    private String name;
    private String description;
    private Long stateMachineId;
    private Long startNodeId;
    private Long endNodeId;
    /**
     * 页面方案id
     */
    private String url;
    private Long objectVersionNumber;
    private String type;
    private String style;
    private String conditionStrategy;
    private Long organizationId;
    private Long endStatusId;
    private List<StateMachineConfigDTO> conditions;
    private List<StateMachineConfigDTO> validators;
    private List<StateMachineConfigDTO> triggers;
    private List<StateMachineConfigDTO> postpositions;

    public Long getEndStatusId() {
        return endStatusId;
    }

    public void setEndStatusId(Long endStatusId) {
        this.endStatusId = endStatusId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getStartNodeId() {
        return startNodeId;
    }

    public void setStartNodeId(Long startNodeId) {
        this.startNodeId = startNodeId;
    }

    public Long getEndNodeId() {
        return endNodeId;
    }

    public void setEndNodeId(Long endNodeId) {
        this.endNodeId = endNodeId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getStateMachineId() {
        return stateMachineId;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public void setStateMachineId(Long stateMachineId) {
        this.stateMachineId = stateMachineId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public List<StateMachineConfigDTO> getConditions() {
        return conditions;
    }

    public void setConditions(List<StateMachineConfigDTO> conditions) {
        this.conditions = conditions;
    }

    public List<StateMachineConfigDTO> getValidators() {
        return validators;
    }

    public void setValidators(List<StateMachineConfigDTO> validators) {
        this.validators = validators;
    }

    public List<StateMachineConfigDTO> getTriggers() {
        return triggers;
    }

    public void setTriggers(List<StateMachineConfigDTO> triggers) {
        this.triggers = triggers;
    }

    public List<StateMachineConfigDTO> getPostpositions() {
        return postpositions;
    }

    public void setPostpositions(List<StateMachineConfigDTO> postpositions) {
        this.postpositions = postpositions;
    }


    public String getConditionStrategy() {
        return conditionStrategy;
    }

    public void setConditionStrategy(String conditionStrategy) {
        this.conditionStrategy = conditionStrategy;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
}
