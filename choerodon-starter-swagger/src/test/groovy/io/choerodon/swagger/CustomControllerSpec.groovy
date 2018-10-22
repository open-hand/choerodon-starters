package io.choerodon.swagger

import io.choerodon.swagger.notify.NotifyTemplateProcessor
import io.choerodon.swagger.swagger.ForwardedHeader
import io.choerodon.swagger.swagger.extra.ExtraDataProcessor
import io.swagger.models.Swagger
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate
import org.spockframework.runtime.Sputnik
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import org.springframework.web.util.UriComponents
import spock.lang.Specification
import springfox.documentation.service.Documentation
import springfox.documentation.spring.web.DocumentationCache
import springfox.documentation.spring.web.json.JsonSerializer
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2Mapper

import javax.servlet.http.HttpServletRequest

@RunWith(PowerMockRunner.class)
@PrepareForTest([ServletUriComponentsBuilder.class])
@PowerMockRunnerDelegate(Sputnik.class)
class CustomControllerSpec extends Specification {
    private CustomController customController
    private JsonSerializer mockJsonSerializer = Mock(JsonSerializer)

    private ExtraDataProcessor mockExtraDataProcessor = Mock(ExtraDataProcessor)

    private NotifyTemplateProcessor mockNotifyTemplateProcessor = Mock(NotifyTemplateProcessor)

    private DocumentationCache mockDocumentationCache = Mock(DocumentationCache)

    private ServiceModelToSwagger2Mapper mockMapper = Mock(ServiceModelToSwagger2Mapper)

    void setup() {
        customController = new CustomController(mockJsonSerializer, mockExtraDataProcessor, mockNotifyTemplateProcessor, mockDocumentationCache, mockMapper)
    }

    def "GetNotifyTemplates"() {
        when: '发送get请求'
//        def templates = restTemplate.getForEntity("/choerodon/templates/notify", NotifyScanData)
        def templates = customController.getNotifyTemplates()
        then: "状态码正确"
        noExceptionThrown()
        1 * mockNotifyTemplateProcessor.getTemplateScanData()
        1 * mockNotifyTemplateProcessor.getBusinessTypeScanData()
        templates.statusCode.is2xxSuccessful()
    }

    def "GetDocumentation"() {
        given: "参数准备"
        def swaggerGroup = "swaggerGroup"
        def request = Mock(HttpServletRequest)

        and: "mock静态方法"
        PowerMockito.mockStatic(ServletUriComponentsBuilder.class)
        def builder = Mock(ServletUriComponentsBuilder)
        builder.build() >> {
            def uriComponents = Mock(UriComponents)
            uriComponents.getPort() >> { return port }
            uriComponents.getPath() >> { return path }
            return uriComponents
        }
        PowerMockito.when(ServletUriComponentsBuilder.fromServletMapping(request)).thenReturn(builder)

        and: "mock返回值"
        mockDocumentationCache.documentationByGroup(_) >> { return Mock(Documentation) }
        mockMapper.mapDocumentation(_) >> {
            def swagger = Mock(Swagger)
            swagger.getHost() >> { return host }
            return swagger
        }
        request.getHeader(ForwardedHeader.NAME) >> { return forward }
        request.getHeader("X-Forwarded-Ssl") >> { return "ON" }
        request.getHeader("X-Forwarded-Port") >> { return "10291" }
        request.getHeader("X-Forwarded-Host") >> { return "" }

        when: '发送get请求'
        customController.setHostNameOverride(hostNameOverride)
        def documentation = customController.getDocumentation(swaggerGroup, request)

        then: "状态码正确"
        documentation.statusCode.is2xxSuccessful()

        where: "分支覆盖"
        forward                     | port | hostNameOverride | host | path
        "proto=proto;host=host"     | 1    | "DEFAULT"        | null | null
        "proto=proto;host=host:111" | 1    | "DEFAULT"        | ""   | null
        "proto=proto;host=host:111" | 1    | ""               | null | ""
        "source=source"             | -1   | "DEFAULT"        | ""   | ""

    }

    def "GetDocumentation[NotFound]"() {
        given: "参数准备"
        def swaggerGroup = "swaggerGroup"
        def request = Mock(HttpServletRequest)
        when: '发送get请求'
        def documentation = customController.getDocumentation(swaggerGroup, request)
        then: "状态码正确"
        documentation.statusCode.is4xxClientError()
    }
}
