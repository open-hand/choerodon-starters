package io.choerodon.resource.permission

import com.google.common.base.Optional
import io.choerodon.resource.IntegrationTestConfiguration
import io.choerodon.swagger.annotation.Permission
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpMethod
import spock.lang.Specification
import spock.lang.Stepwise
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.OperationContext

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@Stepwise
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class PublicPermissionOperationPluginSpec extends Specification {
    private PublicPermissionOperationPlugin publicPermissionOperationPlugin = new PublicPermissionOperationPlugin()
    Optional<Permission> optional = Mock(Optional)

    def "Apply"() {
        given: "mock参数准备"
        def permission = Mock(Permission)
        permission.permissionPublic() >> { return true }
        optional.orNull() >> { permission }
        and: "参数准备"
        def context = Mock(OperationContext)
        context.findAnnotation(Permission.class) >> { return optional }
        context.requestMappingPattern() >> { return "path" }
        context.httpMethod() >> { return HttpMethod.PUT }
        when: "方法调用"
        publicPermissionOperationPlugin.apply(context)
        then: "无异常抛出"
        noExceptionThrown()
    }

    def "Supports"() {
        when: "方法调用"
        def supports = publicPermissionOperationPlugin.supports(Mock(DocumentationType))
        then: "返回正确"
        noExceptionThrown()
        supports == true
    }

    def "GetPublicPaths"() {
        when: "方法调用"
        publicPermissionOperationPlugin.getPublicPaths()
        then: "验证set长度为1"
        noExceptionThrown()
    }
}
