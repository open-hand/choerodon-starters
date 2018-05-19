package io.choerodon.event.consumer.annotation;

import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author flyleft
 * 2018/4/10
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventListener {

    /**
     * 订阅的topic名称
     * @return 订阅的topic名称
     */
    String topic();

    /**
     * 业务类型
     * @return 业务类型
     */
    String[] businessType();

    /**
     * 重试次数
     * @return 重试次数
     */
    int retryTimes() default 0;

    /**
     * 第一次重试间隔，单位：毫秒
     * @return 第一次重试间隔，单位：毫秒
     */
    int firstInterval() default 1000;

    /**
     * 重试间隔，单位：毫秒
     * @return 重试间隔，单位：毫秒
     */
    long retryInterval() default 1000;

    /**
     * 事务传播行为，默认0，范围-1 ～ 7
     * @return 事务传播行为，默认0，范围-1 ～ 7
     */
    int transactionDefinition() default DefaultTransactionDefinition.PROPAGATION_REQUIRED;

}
