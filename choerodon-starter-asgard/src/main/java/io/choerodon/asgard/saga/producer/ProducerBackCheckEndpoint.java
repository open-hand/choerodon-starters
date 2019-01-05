package io.choerodon.asgard.saga.producer;

import io.choerodon.asgard.saga.dto.SagaStatusQueryDTO;
import io.choerodon.asgard.saga.producer.consistency.SagaProducerConsistencyHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProducerBackCheckEndpoint {


    private SagaProducerConsistencyHandler handler;

    public ProducerBackCheckEndpoint(SagaProducerConsistencyHandler handler) {
        this.handler = handler;
    }

    @GetMapping("/choerodon/saga/{uuid}")
    public SagaStatusQueryDTO backCheck(@PathVariable String uuid) {
        return handler.asgardServiceBackCheck(uuid);
    }


}
