package io.choerodon.statemachine.enums;

/**
 * 转换的类型
 *
 * @author shinan.chen
 * @date 2018/9/27
 */
public class TransformType {
    private TransformType() {
    }

    /**
     * 默认初始化转换
     */
    public static final String INIT = "transform_init";
    /**
     * 全部节点都转换到某个节点的转换
     */
    public static final String ALL = "transform_all";
    /**
     * 自定义转换
     */
    public static final String CUSTOM = "transform_custom";
}
