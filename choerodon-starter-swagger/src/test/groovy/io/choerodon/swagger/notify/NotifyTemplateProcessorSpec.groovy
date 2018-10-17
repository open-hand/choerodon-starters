package io.choerodon.swagger.notify

import io.choerodon.core.notify.NotifyTemplate
import spock.lang.Specification

class NotifyTemplateProcessorSpec extends Specification {
    private NotifyTemplateProcessor notifyTemplateProcessor = new NotifyTemplateProcessor()

    def "PostProcessBeforeInitialization"() {
        when: "方法调用"
        def mock = Mock(Object)
        def initialization = notifyTemplateProcessor.postProcessBeforeInitialization(mock, "beanName")
        then: "无异常抛出"
        initialization == mock
    }

    def "PostProcessAfterInitialization"() {
        when: "方法调用"
        def template = Mock(NotifyTemplate)
        template.businessTypeCode() >> { return businessTypeCode }
        template.code() >> { return code }
        template.name() >> { return name }
        template.title() >> { return title }
        template.content() >> { return content }
        template.type() >> { return type }
        notifyTemplateProcessor.postProcessAfterInitialization(template, "beanName")
        then: "无异常抛出"
        noExceptionThrown()
        where: "条件覆盖"
        businessTypeCode   | code   | name   | title   | content                                     | type
        null               | null   | null   | null    | null                                        | null
        "businessTypeCode" | null   | null   | null    | null                                        | null
        "businessTypeCode" | "code" | null   | null    | null                                        | null
        "businessTypeCode" | "code" | "name" | null    | null                                        | null
        "businessTypeCode" | "code" | "name" | "title" | null                                        | null
        "businessTypeCode" | "code" | "name" | "title" | "classpath://content"                       | null
        "businessTypeCode" | "code" | "name" | "title" | "content"                                   | "email"
        "businessTypeCode" | "code" | "name" | "title" | "classpath://contentcontentcontentcontentc" | "email"
    }

    def "TemplateScanData"() {
        when: "get"
        notifyTemplateProcessor.getTemplateScanData()
        notifyTemplateProcessor.getBusinessTypeScanData()
        then: "无异常抛出"
        noExceptionThrown()
    }
}
