package io.choerodon.asgard.saga.feign;

import io.choerodon.asgard.common.QueryStatusException;
import io.choerodon.asgard.common.UpdateStatusException;
import io.choerodon.asgard.common.UpdateStatusDTO;
import io.choerodon.asgard.saga.dto.PollSagaTaskInstanceDTO;
import io.choerodon.asgard.saga.dto.SagaTaskInstanceDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

public class SagaConsumerClientCallback implements SagaConsumerClient {

    private SagaConsumerClient sagaConsumerClient;

    public SagaConsumerClientCallback(SagaConsumerClient sagaConsumerClient) {
        this.sagaConsumerClient = sagaConsumerClient;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(SagaClient.class);

    @Override
    public List<SagaTaskInstanceDTO> pollBatch(PollSagaTaskInstanceDTO pollSagaTaskInstanceDTO) {
        LOGGER.warn("error.sagaConsumer.poll, pollSagaTaskInstanceDTO: {}", pollSagaTaskInstanceDTO);
        return Collections.emptyList();
    }

    /**
     * 如果更新状态失败，则去回查状态，以确定状态确实更新失败还是更新状态成功却因网络、超时造成的失败
     * 查询状态失败，则会阻塞当前线程，直到asgard服务可用
     * 如果asgard服务一直不可用，阻塞的线程过多，则丢入线程池等待队列，等待队列达到最大值，服务会异常退出。
     */
    @Override
    public void updateStatus(Long id, UpdateStatusDTO statusDTO) {
        while (true) {
            try {
                Thread.sleep(200);
                SagaTaskInstanceDTO dto = sagaConsumerClient.queryStatus(id);
                if (dto != null && !statusDTO.getStatus().equals(dto.getStatus())) {
                    throw new UpdateStatusException(id, statusDTO.getStatus());
                } else if (dto == null) {
                    LOGGER.error("error.sagaConsumer.queryStatus, return null: {}", id);
                }
                break;
            } catch (QueryStatusException e) {
                LOGGER.info("error.sagaConsumer.queryStatus, instanceId: {}", id);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new QueryStatusException(id);
            }
        }
    }

    @Override
    public SagaTaskInstanceDTO queryStatus(Long id) {
        throw new QueryStatusException(id);
    }

    @Override
    public void retryUpdateStatus(Long id, UpdateStatusDTO statusDTO) {
        throw new UpdateStatusException(id, statusDTO.getStatus());
    }
}
