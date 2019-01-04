package io.choerodon.asgard.saga.producer;

import io.choerodon.asgard.saga.producer.consistency.SagaProducerConsistencyHandler;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProducerBackCheckEndpoint {

    public static final String STATUS_CANCEL = "cancel";

    public static final String STATUS_CONFIRM = "confirm";

    private SagaProducerConsistencyHandler handler;

    public ProducerBackCheckEndpoint(SagaProducerConsistencyHandler handler) {
        this.handler = handler;
    }

    @GetMapping("/choerodon/saga/{uuid}")
    public String backCheck(@PathVariable String uuid) {
        if (handler.asgardServiceBackCheck(uuid) == null) {
            return STATUS_CANCEL;
        }
        return STATUS_CONFIRM;
    }

    @DeleteMapping("/choerodon/saga/{time}")
    public void clear(@PathVariable long time) {
        handler.clear(time);
    }


}
