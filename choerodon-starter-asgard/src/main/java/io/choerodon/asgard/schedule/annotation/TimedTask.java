package io.choerodon.asgard.schedule.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.choerodon.asgard.schedule.QuartzDefinition;

/**
 * 服务自定义定时任务
 * @author Eugen
 * 2018/10/22 17:44
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TimedTask {
    /**
     * @return 定时任务名称
     */
    String name();

    /**
     * @return 定时任务描述
     */
    String description();

    /**
     * @return 是否只执行一次，true：只执行一次；false：每次部署时执行一次
     */
    boolean oneExecution();

    /**
     * @return 方法执行参数
     */
    TaskParam[] params();

    /**
     * @return simple-trigger的重复次数
     */
    int repeatCount();

    /**
     * @return simple-trigger的重复间隔值：重复间隔形如 '100SECONDS' 则为100
     */
    long repeatInterval();

    /**
     * @return simple-trigger的重复间隔单位：重复间隔形如 '100SECONDS' 则为SECONDS
     */
    QuartzDefinition.SimpleRepeatIntervalUnit repeatIntervalUnit();
}
