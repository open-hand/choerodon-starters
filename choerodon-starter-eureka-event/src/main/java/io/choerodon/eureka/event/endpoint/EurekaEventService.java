package io.choerodon.eureka.event.endpoint;

import io.choerodon.eureka.event.EurekaEventPayload;

import java.util.List;

public interface EurekaEventService {

    List<EurekaEventPayload> unfinishedEvents(String service);

    List<EurekaEventPayload> retryEvents(String id, String service);

}
