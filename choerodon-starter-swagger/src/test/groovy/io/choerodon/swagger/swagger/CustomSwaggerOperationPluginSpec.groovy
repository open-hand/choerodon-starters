package io.choerodon.swagger.swagger

import com.google.common.base.Optional
import io.choerodon.core.annotation.Permission
import io.choerodon.swagger.annotation.Label
import spock.lang.Specification
import springfox.documentation.builders.OperationBuilder
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.OperationContext

class CustomSwaggerOperationPluginSpec extends Specification {
    private CustomSwaggerOperationPlugin customSwaggerOperationPlugin = new CustomSwaggerOperationPlugin()
    Optional<Permission> optional1 = Mock(Optional)
    Optional<Permission> optional2 = Mock(Optional)

    def "Apply"() {
        given: "mock参数准备"
        def permission = Mock(Permission)
        permission.type() >> { return ResourceType.SITE }
        def label = Mock(Label)
        optional1.orNull() >> { permission }
        optional2.orNull() >> { label }

        and: "参数准备"
        def context = Mock(OperationContext)
        context.findAnnotation(Permission.class) >> { return optional1 }
        context.findAnnotation(Label.class) >> { return optional2 }
        context.getName() >> { return "name" }
        context.operationBuilder() >> { return Mock(OperationBuilder) }
        when: "方法调用"
        customSwaggerOperationPlugin.apply(context)
        then: "无异常抛出"
        noExceptionThrown()
    }

    def "Supports"() {
        when: "方法调用"
        def supports = customSwaggerOperationPlugin.supports(Mock(DocumentationType))
        then: "返回true"
        supports == true
    }
}
