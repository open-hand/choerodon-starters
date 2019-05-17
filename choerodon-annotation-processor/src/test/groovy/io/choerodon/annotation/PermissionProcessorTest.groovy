package io.choerodon.annotation

import spock.lang.Specification

class PermissionProcessorTest extends Specification {
    def "Process Path" () {
        when:
        def result = PermissionProcessor.processPath(path)
        then:
        expect == result
        where:
        path || expect
        "/v1/api" || "/v1/api"
        "v1/api" || "/v1/api"
        "/v1//api" || "/v1/api"
    }
}
