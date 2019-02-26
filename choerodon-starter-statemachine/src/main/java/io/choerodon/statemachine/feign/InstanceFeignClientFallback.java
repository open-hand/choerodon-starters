package io.choerodon.statemachine.feign;

import io.choerodon.core.exception.CommonException;
import io.choerodon.statemachine.dto.ExecuteResult;
import io.choerodon.statemachine.dto.InputDTO;
import io.choerodon.statemachine.dto.StateMachineConfigDTO;
import io.choerodon.statemachine.dto.StateMachineTransformDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author shinan.chen, dinghuang123@gmail.com
 * @since 2018/9/25
 */
@Component
public class InstanceFeignClientFallback implements InstanceFeignClient {

    @Override
    public ResponseEntity<ExecuteResult> startInstance(Long organizationId, String serviceCode, Long stateMachineId, InputDTO inputDTO) {
        throw new CommonException("error.instanceFeign.startInstance");
    }

    @Override
    public ResponseEntity<ExecuteResult> executeTransform(Long organizationId, String serviceCode, Long stateMachineId, Long currentStatusId, Long transformId, InputDTO inputDTO) {
        throw new CommonException("error.instanceFeign.executeTransform");
    }

    @Override
    public ResponseEntity<Long> queryInitStatusId(Long organizationId, Long stateMachineId) {
        throw new CommonException("error.instanceFeign.queryInitStatusId");
    }

    @Override
    public ResponseEntity<StateMachineTransformDTO> queryInitTransform(Long organizationId, Long stateMachineId) {
        throw new CommonException("error.instanceFeign.queryInitTransform");
    }
}
