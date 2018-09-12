package io.choerodon.asgard.schedule.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.transaction.annotation.Isolation;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JobTask {

    /**
     * 最大重试次数
     */
    int maxRetryCount() default 1;

    /**
     * 方法执行参数
     */
    JobParam[] params() default {};

    /**
     * 事务超时时间(秒)。默认永不超时。
     *
     * @return 事务超时时间(秒)
     */
    int transactionTimeout() default -1;

    /**
     * 是否为只读事务
     *
     * @return 是否为只读事务
     */
    boolean transactionReadOnly() default false;

    /**
     * 事务的隔离级别
     *
     * @return 事务的隔离级别
     */
    Isolation transactionIsolation() default Isolation.DEFAULT;


    /**
     * 所用的事务管理器的bean名
     *
     * @return 所用的事务管理器的bean名
     */
    String transactionManager() default "";

    /**
     * 方法编码
     *
     * @return 方法编码
     */
    String code();

    /**
     * 方法描述
     *
     * @return 方法描述
     */
    String description() default "";
}
