package io.choerodon.asgard.saga.feign;

import io.choerodon.asgard.saga.dto.SagaInstanceDTO;
import io.choerodon.asgard.saga.dto.StartInstanceDTO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.function.Consumer;
import java.util.function.Function;

@FeignClient(name = "${choerodon.saga.service:go-asgard-service}", fallback = SagaClientCallback.class)
public interface SagaClient {

    /**
     *      
     *
     * @deprecated feign调用一致性低，请使用{@link io.choerodon.asgard.saga.producer.TransactionalProducer#apply(Consumer)}
     * 或者{@link io.choerodon.asgard.saga.producer.TransactionalProducer#applyAndReturn(Function)}
     * A服务接口:
     * ```java
     * try {
     * 业务代码A
     * feign调用B服务接口
     * 事务提交
     * } catch (Exception e) {
     * 事务回滚
     * }
     * ```
     * B服务接口:
     * ```java
     * try {
     * 业务代码B
     * 事务提交
     * //此时接口状态码2XX
     * } catch (Exception e) {
     * 事务回滚
     * //此时接口状态码非2XX
     * }
     * ```
     * <p>
     * 一致性分析:
     * 1. feign调用B服务接口成功，状态码为2XX。则B服务事务已经提交，A进行事务提交。
     * 若A事务提交成功，则一致； 若A事务提交失败但此时B中事务已经提交，则不一致。
     * 2. B没有接收到网络请求。B未被执行，feign调用抛出异常，A事务不进行提交，进入回滚，数据一致。
     * 3. B执行成功，请求返回时异常。B事务已经提交，feign调用B服务接口异常，A事务回滚，数据不一致。
     * 4. B调用超时。可能为B没有接收到网络请求，也可能B执行成功，请求返回时异常，也可能B收到请求响应缓慢。一致性状态不确定，都有可能。
     **/
    @Deprecated
    @PostMapping("/v1/sagas/instances/{code}")
    SagaInstanceDTO startSaga(@PathVariable("code") String code,
                              @RequestBody StartInstanceDTO dto);


    @PostMapping("/v1/sagas/instances")
    SagaInstanceDTO preCreateSaga(@RequestBody StartInstanceDTO instanceDTO);


    @PostMapping("/v1/sagas/instances/{uuid}/confirm")
    void confirmSaga(@PathVariable("uuid") String uuid, @RequestBody String json);


    @PutMapping("/v1/sagas/instances/{uuid}/cancel")
    void cancelSaga(@PathVariable("uuid") String uuid);

}