package io.choerodon.swagger

import io.choerodon.swagger.notify.NotifyTemplateProcessor
import io.choerodon.swagger.swagger.CustomSwaggerOperationPlugin
import io.choerodon.swagger.swagger.OperationCustomPageRequestReader
import io.choerodon.swagger.swagger.extra.ExtraDataProcessor
import spock.lang.Specification
import springfox.documentation.spring.web.DocumentationCache
import springfox.documentation.spring.web.json.JsonSerializer
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2Mapper

class SwaggerConfigSpec extends Specification {
    SwaggerConfig swaggerConfig = new SwaggerConfig()

    def "Docket"() {
        when: "Bean方法调用测试"
        def docket = swaggerConfig.docket()
        def plugin = swaggerConfig.customSwaggerOperationPlugin()
        def reader = swaggerConfig.operationCustomPageRequestReader()
        def processor1 = swaggerConfig.extraDataProcessor()
        def processor = swaggerConfig.notifyTemplateProcessor()
        def controller = swaggerConfig.customSwagger2Controller(Mock(JsonSerializer),
                Mock(DocumentationCache),
                Mock(ServiceModelToSwagger2Mapper))
        then: "结果测试"
        noExceptionThrown()
        docket instanceof Docket
        plugin instanceof CustomSwaggerOperationPlugin
        reader instanceof OperationCustomPageRequestReader
        processor1 instanceof ExtraDataProcessor
        processor instanceof NotifyTemplateProcessor
        controller instanceof CustomController
    }
}
