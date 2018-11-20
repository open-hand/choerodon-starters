package io.choerodon.core.notify;

import java.util.List;
import java.util.Map;

public class NoticeSendDTO {

    /**
     * 发送的业务类型code
     */
    private String code;

    /**
     * 触发发送通知的组织或项目id，如果是site层，则不传或传0
     */
    private Long sourceId;

    /**
     * 发送者(目前用于发送站内信的发送者字段)
     */
    private Long fromUserId;

    /**
     * 目标用户
     */
    private List<Long> targetUsersIds;

    /**
     * 模版渲染参数(标题和内容渲染都在此参数中)
     */
    private Map<String, Object> params;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public Long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public List<Long> getTargetUsersIds() {
        return targetUsersIds;
    }

    public void setTargetUsersIds(List<Long> targetUsersIds) {
        this.targetUsersIds = targetUsersIds;
    }
}
