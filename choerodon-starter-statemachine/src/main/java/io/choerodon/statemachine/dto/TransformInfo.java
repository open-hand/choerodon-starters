package io.choerodon.statemachine.dto;

import com.google.common.base.MoreObjects;

import java.util.List;

/**
 * 用于客户端获取某个状态的转换列表，其中节点id替换成状态id
 *
 * @author shinan.chen
 * @author dinghuang123@gmail.com
 * @since 2018/10/8
 */
public class TransformInfo {

    private Long id;

    private String name;

    private String description;

    private Long stateMachineId;

    private Long startStatusId;

    private Long endStatusId;

    private String url;

    private String type;

    private String style;

    private String conditionStrategy;

    private Long organizationId;

    private Long objectVersionNumber;

    private List<StateMachineConfigDTO> conditions;

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

    public Long getStateMachineId() {
        return stateMachineId;
    }

    public void setStateMachineId(Long stateMachineId) {
        this.stateMachineId = stateMachineId;
    }

    public Long getStartStatusId() {
        return startStatusId;
    }

    public void setStartStatusId(Long startStatusId) {
        this.startStatusId = startStatusId;
    }

    public Long getEndStatusId() {
        return endStatusId;
    }

    public void setEndStatusId(Long endStatusId) {
        this.endStatusId = endStatusId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public List<StateMachineConfigDTO> getConditions() {
        return conditions;
    }

    public void setConditions(List<StateMachineConfigDTO> conditions) {
        this.conditions = conditions;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("name", name)
                .add("description", description)
                .add("stateMachineId", stateMachineId)
                .add("startStatusId", startStatusId)
                .add("endStatusId", endStatusId)
                .add("url", url)
                .add("type", type)
                .add("style", style)
                .add("conditionStrategy", conditionStrategy)
                .add("organizationId", organizationId)
                .add("objectVersionNumber", objectVersionNumber)
                .add("conditions", conditions)
                .toString();
    }
}
