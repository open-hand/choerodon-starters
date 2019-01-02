package io.choerodon.asgard.saga.producer.consistency;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A服务业务接口:
 * ```java
 * private final ConcurrentMap<String, Long> uuids = new ConcurrentHashMap<>();
 * <p>
 * String uuid = generateUUID()//生成一个uuid
 * try{
 * feign.preCreate(uuid,...)//feign调用B预执行，比如B服务为创建订单服务，预创建一个订单，但状态为待确认
 * 业务代码A
 * uuids.put(uuid, System.currentTimeMillis())
 * 事务提交
 * }catch (Exception e) {
 * uuidTimeMap.remove(uuid);
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
 * public String asgardServiceBackCheck(String uuid) {
 * if (uuidTimeMap.containsKey(uuid)) {
 * return "confirm";
 * }
 * return "cancel";
 * }
 * ```
 * <p>
 * 一致性分析：
 * 1. A中preCreate执行异常。应抛出异常，不再执行业务代码和事件表插入uuid，去执行cancel。
 * 若cancel也执行失败，比如此时B挂掉，B重启后应去调用服务A的回查接口，确定状态。状态一致。
 * 2. 业务代码A执行失败，事务回滚，feign调用B取消。若取消成功，则状态一致；若取消失败，应抛出异常，confirm不再执行。状态一致。
 * 3. 业务代码A执行成功，事务提交失败，执行事务回滚。若回滚失败，抛出异常，不再执行cancel和confirm，B会执行超时回查确定状态；若回滚成功，则执行取消。状态一致。
 * 4. A事务提交成功，确认失败(比如A执行确认时，服务A或者B刚好挂掉)。则服务B超时回查，发现uuid不存在，此时状态**不一致**。
 * 5. 预处理完成后，去执行业务代码，若业务代码执行缓慢，B服务认为超时，则服务B超时回查，若A的事务还未提交，A的回查接口返回取消，
 * 则B被取消，A却提交了事务，此时出现事务状态的**不一致**。此时可以通过设置B稍微大的超时时间来调整，可以让服务A在预处理的feign调用时传入期待的超时时间。
 */
public class SagaProducerMemoryConsistencyHandler implements SagaProducerConsistencyHandler {

    private final ConcurrentMap<String, Long> uuidTimeMap = new ConcurrentHashMap<>();

    @Override
    public void beforeTransactionCommit(String uuid) {
        uuidTimeMap.put(uuid, System.currentTimeMillis());
    }

    @Override
    public void beforeTransactionCancel(String uuid) {
        uuidTimeMap.remove(uuid);
    }

    @Override
    public String asgardServiceBackCheck(String uuid) {
        if (uuidTimeMap.containsKey(uuid)) {
            return uuid;
        }
        return null;
    }

    @Override
    public void clear(long time) {
        Iterator<Map.Entry<String, Long>> it = uuidTimeMap.entrySet().iterator();
        long current = System.currentTimeMillis();
        while (it.hasNext()) {
            Map.Entry<String, Long> entry = it.next();
            if (entry.getValue() + time < current) {
                it.remove();
            }
        }
    }
}
