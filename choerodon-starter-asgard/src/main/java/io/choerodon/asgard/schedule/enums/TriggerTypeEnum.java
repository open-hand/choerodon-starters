package io.choerodon.asgard.schedule.enums;

/**
 * @author scp
 * @date 2020/8/4
 * @description
 */
public enum TriggerTypeEnum {
    /**
     * 简单执行
     */
    SIMPLE_TRIGGER("simple-trigger"),
    /**
     * cron执行
     */
    CRON_TRIGGER("cron-trigger");
    private String type;

    TriggerTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
