package io.choerodon.asgard.saga.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.asgard.saga.dto.StartInstanceDTO;
import io.choerodon.asgard.saga.exception.SagaProducerException;
import io.choerodon.core.iam.ResourceLevel;
import org.springframework.util.StringUtils;

public final class StartSagaBuilder {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private StartInstanceDTO startInstanceDTO;

    private StartSagaBuilder() {
        startInstanceDTO = new StartInstanceDTO();
    }

    public static StartSagaBuilder newBuilder() {
        return new StartSagaBuilder();
    }

    public StartSagaBuilder withSagaCode(String sagaCode) {
        startInstanceDTO.setSagaCode(sagaCode);
        return this;
    }

    public StartSagaBuilder withJson(String json) {
        startInstanceDTO.setInput(json);
        return this;
    }

    public StartSagaBuilder withPayloadAndSerialize(Object payload) {
        try {
            if (payload != null) {
                String json = MAPPER.writeValueAsString(payload);
                startInstanceDTO.setInput(json);
            }
        } catch (JsonProcessingException e) {
            throw new SagaProducerException("error.startSagaBuilder.withPayloadAndSerialize", e);
        }
        return this;
    }

    public StartSagaBuilder withRefType(String refType) {
        startInstanceDTO.setRefType(refType);
        return this;
    }

    public StartSagaBuilder withRefId(String refId) {
        startInstanceDTO.setRefId(refId);
        return this;
    }

    public StartSagaBuilder withLevel(ResourceLevel level) {
        startInstanceDTO.setLevel(level.value());
        return this;
    }

    public StartSagaBuilder withSourceId(String sourceId) {
        startInstanceDTO.setRefId(sourceId);
        return this;
    }

    StartSagaBuilder withService(String service) {
        startInstanceDTO.setService(service);
        return this;
    }

    StartSagaBuilder withUuid(String uuid) {
        startInstanceDTO.setUuid(uuid);
        return this;
    }

    StartInstanceDTO preBuild() {
        if (StringUtils.isEmpty(startInstanceDTO.getSagaCode())) {
            throw new SagaProducerException("error.startSaga.sagaCodeIsEmpty");
        }
        if (startInstanceDTO.getLevel() == null) {
            startInstanceDTO.setLevel(ResourceLevel.SITE.value());
        }
        return startInstanceDTO;
    }

    String getPayloadJson() {
        if (startInstanceDTO.getInput() == null) {
            throw new SagaProducerException("error.startSaga.inputIsNull");
        }
        return startInstanceDTO.getInput();
    }
}
