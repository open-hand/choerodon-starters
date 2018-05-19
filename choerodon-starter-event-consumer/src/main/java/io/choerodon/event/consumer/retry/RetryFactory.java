package io.choerodon.event.consumer.retry;

import static org.quartz.DateBuilder.IntervalUnit.MILLISECOND;
import static org.quartz.DateBuilder.futureDate;

import io.choerodon.event.consumer.domain.MsgExecuteBean;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 重试工厂
 * @author flyleft
 * 17-11-14
 */
public class RetryFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(RetryFactory.class);

    private Scheduler retryScheduler;

    public RetryFactory(Scheduler retryScheduler) {
        this.retryScheduler = retryScheduler;
    }


    /**
     * 重试工厂添加重试消息
     * @param bean 消息执行信息封装类
     */
    public void addRetry(MsgExecuteBean bean) {
        String uuid =  bean.getPayload().getUuid();
        Trigger trigger = TriggerBuilder
                .newTrigger()
                .withIdentity(uuid)
                .withSchedule(
                        SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInMilliseconds(bean.eventListener.retryInterval())
                                .withRepeatCount(bean.eventListener.retryTimes() - 1))
                .startAt(futureDate(bean.eventListener.firstInterval(), MILLISECOND))
                .build();
        JobDetail job = JobBuilder.newJob(RetryJobImpl.class)
                .withIdentity(uuid)
                .usingJobData("uuid", uuid)
                .build();
        try {
            retryScheduler.getContext().put(uuid, bean);
            retryScheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            LOGGER.warn("some error happened when addRetry, cause: {}", e.getMessage());
        }
    }

}
