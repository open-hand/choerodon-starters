package io.choerodon.asgard.schedule

import io.choerodon.asgard.IntegrationTestConfiguration
import io.choerodon.asgard.schedule.dto.ScheduleInstanceConsumerDTO
import io.choerodon.asgard.schedule.feign.ScheduleMonitorClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class ScheduleMonitorSpec extends Specification {

    @Autowired
    ScheduleMonitor scheduleMonitor

    def '测试invokeRunner方法'() {
        given: '创建feign调用返回的job对象'
        def dto1 = new ScheduleInstanceConsumerDTO()
        dto1.setId(1L)
        dto1.setMethod('test')
        dto1.setExecuteParams('{}')

        and: 'mock feign'
        def feign = Mock(ScheduleMonitorClient){
            pollBatch(_,_) >> [dto1]
        }
        scheduleMonitor.setScheduleMonitorClient(feign)

        when: '调用invokeRunner方法'
        scheduleMonitor.invokeRunner(Collections.emptySet(), 'instance')
        Thread.sleep(1500)

        then: '验证updateStatus被调用'
        1 * feign.updateStatus(dto1.getId(), _)

    }


    def '测试invokeRunner发生异常'() {
        given: '创建feign调用返回的job对象'
        def dto1 = new ScheduleInstanceConsumerDTO()
        dto1.setId(1L)
        dto1.setMethod('testException')
        dto1.setExecuteParams('{}')

        and: 'mock feign'
        def feign = Mock(ScheduleMonitorClient){
            pollBatch(_,_) >> [dto1]
        }
        scheduleMonitor.setScheduleMonitorClient(feign)

        when: '调用invokeRunner方法'
        scheduleMonitor.invokeRunner(Collections.emptySet(), 'instance')
        Thread.sleep(1500)

        then: '验证updateStatus被调用'
        1 * feign.updateStatus(dto1.getId(), _)
    }


}
