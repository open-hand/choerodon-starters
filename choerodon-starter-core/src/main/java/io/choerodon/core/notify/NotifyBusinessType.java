package io.choerodon.core.notify;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface NotifyBusinessType {

    /**
     * 业务类型code，不可重复。例如enableProject
     */
    String code();

    /**
     * 业务名称
     */
    String name();

    /**
     * 业务描述
     */
    String description() default "";

    /**
     * 触发通知对应的层级
     */
    Level level();

    /**
     * 最大自动重试次数
     */
    int retryCount() default 0;

    /**
     * 是否立即发送消息
     */
    boolean isSendInstantly() default true;

    /**
     * 是否手动重试
     */
    boolean isManualRetry() default false;

    /**
     * 是否允许用户配置接收通知
     */
    boolean isAllowConfig() default true;
}
