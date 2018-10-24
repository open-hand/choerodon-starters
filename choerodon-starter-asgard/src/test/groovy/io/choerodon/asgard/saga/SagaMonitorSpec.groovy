package io.choerodon.asgard.saga

import com.fasterxml.jackson.databind.ObjectMapper
import io.choerodon.asgard.IntegrationTestConfiguration
import io.choerodon.asgard.saga.dto.PollBatchDTO
import io.choerodon.asgard.saga.dto.PollCodeDTO
import io.choerodon.asgard.saga.dto.SagaTaskInstanceDTO
import io.choerodon.asgard.saga.feign.SagaMonitorClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class SagaMonitorSpec extends Specification {

    @Autowired
    SagaMonitor sagaMonitor

    def '测试invokeRunner方法'() {
        given: '创建一个PollCodeDTO'
        def codeDTOS = new ArrayList<PollCodeDTO>()
        SagaMonitor.invokeBeanMap.entrySet().each {
            codeDTOS.add(new PollCodeDTO(it.getValue().sagaTask.sagaCode(), it.getValue().sagaTask.code()))
        }
        def pollBatchDTO = new PollBatchDTO('instance-0', codeDTOS, 100)

        and: 'mock feign对象'
        def mapper = new ObjectMapper()
        def dto1 = new SagaTaskInstanceDTO()
        dto1.setId(10L)
        dto1.setSagaCode('sagaOne')
        dto1.setTaskCode('sagaTaskOne')
        dto1.setObjectVersionNumber(1L)
        dto1.setInput(mapper.writeValueAsString(new SagaTaskRunner.Data('小明', 23)))
        def sagaMonitorClient = Mock(SagaMonitorClient) {
            pollBatch(_) >> [dto1]
        }
        sagaMonitor.setSagaMonitorClient(sagaMonitorClient)

        when: '调用invokeRunner方法'
        sagaMonitor.invokeRunner(pollBatchDTO)
        Thread.sleep(1500)

        then: '验证结果'
        1 * sagaMonitorClient.updateStatus(dto1.id, _)
    }

    def '测试invokeRunner的exception方法'() {
        given: '创建一个PollCodeDTO'
        def codeDTOS = new ArrayList<PollCodeDTO>()
        SagaMonitor.invokeBeanMap.entrySet().each {
            codeDTOS.add(new PollCodeDTO(it.getValue().sagaTask.sagaCode(), it.getValue().sagaTask.code()))
        }
        def pollBatchDTO = new PollBatchDTO('instance-0', codeDTOS, 100)

        and: 'mock feign对象'
        def mapper = new ObjectMapper()
        def dto1 = new SagaTaskInstanceDTO()
        dto1.setId(10L)
        dto1.setSagaCode('sagaOne')
        dto1.setTaskCode('sagaTaskTwo')
        dto1.setObjectVersionNumber(1L)
        dto1.setInput(mapper.writeValueAsString(new SagaTaskRunner.Data('小明', 23)))
        def sagaMonitorClient = Mock(SagaMonitorClient) {
            pollBatch(_) >> [dto1]
        }
        sagaMonitor.setSagaMonitorClient(sagaMonitorClient)

        when: '调用invokeRunner方法'
        sagaMonitor.invokeRunner(pollBatchDTO)
        Thread.sleep(1500)

        then: '验证结果'
        1 * sagaMonitorClient.updateStatus(dto1.id, _)
    }

}
