package io.choerodon.resource.handler

import io.choerodon.core.exception.CommonException
import io.choerodon.core.exception.FeignException
import io.choerodon.core.exception.NotFoundException
import io.choerodon.resource.IntegrationTestConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Import
import org.springframework.dao.DuplicateKeyException
import org.springframework.jdbc.BadSqlGrammarException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.multipart.MultipartException
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class ControllerExceptionHandlerSpec extends Specification {
    private ControllerExceptionHandler controllerExceptionHandler
    private MessageSource messageSource = Mock(MessageSource)

    void setup() {
        controllerExceptionHandler = new ControllerExceptionHandler()
        controllerExceptionHandler.setMessageSource(messageSource)
    }

    def "ProcessFeignException"() {
        given: "mock"
        def exception = Mock(FeignException)
        and: "mock异常"
        messageSource.getMessage(_, _, _) >> { throw new Exception("") }
        when: "方法调用"
        controllerExceptionHandler.processFeignException(exception)
        then: "无异常抛出"
        noExceptionThrown()
    }

    def "Process"() {
        given: "mock"
        def a = 1
        messageSource.getMessage(_, _, _) >> { throw new Exception("") }
        when: "方法调用"
        controllerExceptionHandler.process(exception)
        then: "无异常抛出"
        a == data
        noExceptionThrown()
        where: "重载"
        exception                             | data
        Mock(CommonException)                 | 1
        Mock(NotFoundException)               | 1
        Mock(MethodArgumentNotValidException) | 1
        Mock(DuplicateKeyException)           | 1
        Mock(MultipartException)              | 1
        Mock(BadSqlGrammarException)          | 1
    }
}
