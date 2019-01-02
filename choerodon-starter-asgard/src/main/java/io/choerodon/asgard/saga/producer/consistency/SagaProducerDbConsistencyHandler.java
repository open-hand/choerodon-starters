package io.choerodon.asgard.saga.producer.consistency;

/**
 * 基于数据表实现的回查方式
 * A服务需要插入一个表transaction_record记录调用状态，提供给B服务回调。
 * <p>
 * A服务业务接口:
 * ```java
 * String uuid = generateUUID()//生成一个uuid
 * try{
 * feign.preCreate(uuid,...)//feign调用B预执行，比如B服务为创建订单服务，预创建一个订单，但状态为待确认
 * 业务代码A
 * 将uuid插入transaction_record表中
 * 事务提交
 * }catch (Exception e) {
 * 事务回滚
 * feign.cancel(uuid)//feign调用B取消，比如B服务为创建订单服务，设置该订单状态为取消
 * }
 * <p>
 * feign.confirm(uuid)//feign调用B确认，比如B服务为创建订单服务，设置该订单状态为确认，此时订单可用
 * ```
 * A服务服务回查接口，提供给B服务回查状态:
 * ```
 * get /v1/transaction/{uuid}
 * <p>
 * 从transaction_record表中查询，有则返回确认，没有则返回取消
 * ```
 * <p>
 * B服务需要提供预执行、确认、取消接口。若预执行后迟迟没有执行确认或取消，则B向A回查，根据结果确认或取消。
 * <p>
 * 一致性分析：
 * 1. A中preCreate执行异常。应抛出异常，不再执行业务代码和事件表插入uuid，去执行cancel。若B执行则状态也为未确认，不影响一致性；
 * 若cancel也执行失败，比如此时B挂掉，B重启后应去调用服务A的回查接口，确定状态。状态一致。
 * 2. 业务代码A执行失败，事务回滚，feign调用B取消。若取消成功，则状态一致；若取消失败，应抛出异常，confirm不再执行。状态一致。
 * 3. 业务代码A执行成功，事务提交失败，执行事务回滚。若回滚失败，抛出异常，不再执行cancel和confirm，B会执行超时回查确定状态；若回滚成功，则执行取消。状态一致。
 * 4. A事务提交成功，确认失败(比如A执行确认时，服务A或者B刚好挂掉)。则服务B超时回查，发现uuid存在，修改状态为确认。状态一致。
 * 5. 预处理完成后，去执行业务代码，若业务代码执行缓慢，B服务认为超时，则服务B超时回查，若A的事务还未提交，A的回查接口返回取消，
 * 则B被取消，A却提交了事务，此时出现事务状态的不一致。此时可以通过设置B稍微大的超时时间来调整，可以让服务A在预处理的feign调用时传入期待的超时时间。
 */
public class SagaProducerDbConsistencyHandler implements SagaProducerConsistencyHandler {

    private SagaProducerStore sagaProducerStore;

    public SagaProducerDbConsistencyHandler(SagaProducerStore sagaProducerStore) {
        this.sagaProducerStore = sagaProducerStore;
    }

    @Override
    public void beforeTransactionCommit(String uuid) {
        sagaProducerStore.record(uuid);
    }

    @Override
    public void beforeTransactionCancel(String uuid) {
        // do nothing
    }

    @Override
    public String asgardServiceBackCheck(String uuid) {
        return sagaProducerStore.selectByUUID(uuid);
    }

    @Override
    public void clear(long time) {
        sagaProducerStore.clear(time);
    }
}
