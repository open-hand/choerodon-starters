package io.choerodon.message

import io.choerodon.message.impl.rabbit.MessagePublisherImpl
import io.choerodon.mybatis.entity.BaseDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Ignore
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE

@Ignore
//测试需要RabbitMQ环境，如果有条件可以删除这个注解，启用测试
@SpringBootTest(webEnvironment = NONE, classes = [TestApplication], properties = ["message.provider=rabbitmq"])
class RabbitMessageSpec extends Specification {
    @Autowired
    TestMonitor monitor
    @Autowired
    TestEntityMonitor entityMonitor;

    @Autowired
    MessagePublisherImpl messagePublisher;

    def "Rabbit Message Publisher"() {
        when:
        synchronized (messagePublisher) {
            messagePublisher.message("test:queue", null)
            messagePublisher.publish("test:topic", null)
            messagePublisher.message("test:queue", "test")
            messagePublisher.publish("test:topic", "test")
            messagePublisher.wait(100)
        }
        then:
        monitor.topicCount == 2
        monitor.queueCount == 2
    }

    def "Rabbit Message Publisher Entity"() {
        when:
        synchronized (messagePublisher) {
            messagePublisher.message("test:queueEntity", new BaseDTO())
            messagePublisher.publish("test:topicEntity", new BaseDTO())
            messagePublisher.wait(100)
        }
        then:
        entityMonitor.topicCount == 1
        entityMonitor.queueCount == 1
    }
}
