package io.choerodon.swagger.swagger

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

@Stepwise
class ForwardedHeaderSpec extends Specification {
    @Shared
    private ForwardedHeader forwardedHeader

    def "Of"() {
        given: "参数准备"
        def source = "proto=proto;host=host;source=source=source"
        when: "方法调用"
        ForwardedHeader.of("")
        forwardedHeader = ForwardedHeader.of(source)
        then: "结果分析"
        noExceptionThrown()
    }

    def "GetProto"() {
        when: "方法调用"
        def proto = forwardedHeader.getProto()
        then: "验证proto"
        noExceptionThrown()
        proto == "proto"
    }

    def "GetHost"() {
        when: "方法调用"
        def host = forwardedHeader.getHost()
        then: "验证host"
        noExceptionThrown()
        host == "host"
    }
}
