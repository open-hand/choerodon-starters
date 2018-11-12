package io.choerodon.swagger.swagger

import com.google.common.base.Optional
import io.choerodon.swagger.annotation.CustomPageRequest
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import spock.lang.Specification
import springfox.documentation.builders.OperationBuilder
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.OperationContext

class OperationCustomPageRequestReaderSpec extends Specification {
    private OperationCustomPageRequestReader operationCustomPageRequestReader = new OperationCustomPageRequestReader()
    Optional<CustomPageRequest> annotation = Mock(Optional)

    def "Apply"() {
        given: "参数准备"
        def context = Mock(OperationContext)
        annotation.isPresent() >> { return true }
        annotation.get() >> {
            def mock = Mock(CustomPageRequest)
            mock.apiImplicitParams() >> {
                def mock1 = Mock(ApiImplicitParams)
                mock1.value() >> {
                    def a = new ApiImplicitParam[1]
                    def param = Mock(ApiImplicitParam)
                    param.allowableValues() >> { return "allowableValues" }
                    param.allowMultiple() >> { return true }
                    a[0] = param
                    return a
                }
                return mock1
            }
            return mock
        }
        context.findAnnotation(CustomPageRequest.class) >> { return annotation }
        context.operationBuilder() >> { return Mock(OperationBuilder) }
        when: "方法调用"
        operationCustomPageRequestReader.apply(context)
        then: "无异常抛出"
        noExceptionThrown()
    }

    def "Supports"() {
        when: "方法调用"
        operationCustomPageRequestReader.supports(Mock(DocumentationType))
        then: "无异常抛出"
        noExceptionThrown()
    }

    def "ImplicitParameter"() {
        given: "参数准备"
        def param = Mock(ApiImplicitParam)
        param.allowableValues() >> { return "allowableValues" }
        param.allowMultiple() >> { return false }
        when: "方法调用"
        OperationCustomPageRequestReader.implicitParameter(param)
        then: "结果分析"
        noExceptionThrown()
    }
}
