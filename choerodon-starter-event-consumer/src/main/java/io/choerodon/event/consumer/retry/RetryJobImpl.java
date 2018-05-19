package io.choerodon.event.consumer.retry;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.event.consumer.domain.MsgExecuteBean;
import io.choerodon.event.consumer.handler.MsgHandler;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 消息重试时调用的job类
 * @author flyleft
 */
public class RetryJobImpl implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(RetryJobImpl.class);

    @Override
    public void execute(JobExecutionContext context) {

        JobDataMap map = context.getJobDetail().getJobDataMap();
        String uuid = map.getString("uuid");
        SchedulerContext schedulerContext;
        try {
            schedulerContext = context.getScheduler().getContext();
            MsgExecuteBean bean = (MsgExecuteBean) schedulerContext.get(uuid);
            bean.getHasRetryTimes().incrementAndGet();
            if (bean.getSuccess()) {
                return;
            }
            MsgHandler msgHandler = ApplicationContextHelper.getSpringFactory().getBean(MsgHandler.class);
            msgHandler.execute(bean);
        } catch (SchedulerException e) {
            LOGGER.warn("some error happened when execute retry", e.getMessage());
        }
    }

}
