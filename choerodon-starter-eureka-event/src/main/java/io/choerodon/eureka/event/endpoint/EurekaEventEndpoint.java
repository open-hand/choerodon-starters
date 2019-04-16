package io.choerodon.eureka.event.endpoint;

import io.choerodon.base.annotation.Permission;
import io.choerodon.eureka.event.EurekaEventPayload;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/v1/eureka/events")
public class EurekaEventEndpoint {

    private EurekaEventService eurekaEventService;

    public EurekaEventEndpoint(EurekaEventService eurekaEventService) {
        this.eurekaEventService = eurekaEventService;
    }

    @Permission(permissionLogin = true)
    @ApiOperation(value = "获取未消费的事件列表")
    @GetMapping
    public List<EurekaEventPayload> list(@RequestParam(value = "service", required = false) String service) {
        return eurekaEventService.unfinishedEvents(service);
    }

    @Permission(permissionLogin = true)
    @ApiOperation(value = "手动重试未消费成功的事件")
    @PostMapping("retry")
    public List<EurekaEventPayload> retry(@RequestParam(value = "id", required = false) String id,
                                          @RequestParam(value = "service", required = false) String service) {
        return eurekaEventService.retryEvents(id, service);
    }

}
