package io.choerodon.resource

import spock.lang.Specification

class ResourceServerConfigSpec extends Specification {
    private ResourceServerConfig resourceServerConfig = new ResourceServerConfig()

    def "SerializingObjectMapper"() {
        when: "方法调用"
        resourceServerConfig.serializingObjectMapper()
        then: "结果分析"
        noExceptionThrown()
    }

    def "MessageSource"() {
        when: "方法调用"
        resourceServerConfig.messageSource()
        then: "结果分析"
        noExceptionThrown()
    }

    def "ControllerExceptionHandler"() {
        when: "方法调用"
        resourceServerConfig.controllerExceptionHandler()
        then: "结果分析"
        noExceptionThrown()
    }

    def "PermissionSwaggerOperationPlugin"() {
        when: "方法调用"
        resourceServerConfig.permissionSwaggerOperationPlugin()
        then: "结果分析"
        noExceptionThrown()
    }
}
