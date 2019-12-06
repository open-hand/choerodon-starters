package io.choerodon.core.notify;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

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
     * 消息类型编码
     */
    String categoryCode() default "default";

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

    /**
     * 是否启用邮件方式发送消息。0：不启用（默认）；1：启用
     */
    boolean emailEnabledFlag() default false;

    /**
     * 是否启用站内信方式发送消息。0：不启用（默认）；1：启用
     */
    boolean pmEnabledFlag() default false;

    /**
     * 是否启用短信方式发送消息。0：不启用（默认）；1：启用
     */
    boolean smsEnabledFlag() default false;

    /**
     * 是否启用WEBHOOK方式发送消息。0：不启用（默认）；1：启用
     */
    boolean webhookEnabledFlag() default false;

    /**
     * 用于项目层通知设置触发人员
     */
    String[] targetUserType() default {};

    /**
     * 用于项目层通知设置 区分tab页
     */
    ServiceNotifyType notifyType() default ServiceNotifyType.DEFAULT_NOTIFY;

    /**
     * 项目层：是否启用邮件方式发送消息。0：不启用（默认）；1：启用
     */
    boolean proEmailEnabledFlag() default false;

    /**
     * 项目层：是否启用站内信方式发送消息。0：不启用（默认）；1：启用
     */
    boolean proPmEnabledFlag() default false;

}
