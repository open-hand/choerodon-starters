package io.choerodon.event.consumer.handler;

import io.choerodon.event.consumer.domain.MsgExecuteBean;
import org.springframework.scheduling.annotation.Async;

/**
 * 消息处理的接口
 *
 * @author flyleft
 * 2018/1/25
 */
public interface MsgHandler {

    void execute(MsgExecuteBean msgExecuteBean);

    @Async("msgConsumeExecutor")
    void executeAsync(MsgExecuteBean msgExecuteBean);

    boolean filter(MsgExecuteBean bean);

}
