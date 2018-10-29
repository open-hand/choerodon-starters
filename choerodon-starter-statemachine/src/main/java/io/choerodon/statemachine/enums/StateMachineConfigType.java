package io.choerodon.statemachine.enums;

/**
 * @author shinan.chen
 * @author dinghuang123@gmail.com
 * @since 2018/10/9
 */
public class StateMachineConfigType {

    private StateMachineConfigType() {}

    /**
     * 条件
     */
    public static final String CONDITION = "config_condition";
    /**
     * 验证
     */
    public static final String VALIDATOR = "config_validator";
    /**
     * 触发器
     */
    public static final String TRIGGER = "config_trigger";
    /**
     * 后置处理
     */
    public static final String POSTPOSITION = "config_postposition";
}
