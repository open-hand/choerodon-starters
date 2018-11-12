package io.choerodon.resource.filter

import io.choerodon.core.variable.RequestVariableHolder
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate
import org.spockframework.runtime.Sputnik
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest

@RunWith(PowerMockRunner.class)
@PrepareForTest([Enumeration.class])
@PowerMockRunnerDelegate(Sputnik.class)
class JwtTokenExtractorSpec extends Specification {
    JwtTokenExtractor jwtTokenExtractor

    void setup() {
        jwtTokenExtractor = new JwtTokenExtractor()
    }

    def "Extract-1"() {
        given: "准备mock参数"
        def request = Mock(HttpServletRequest)
        def headers = PowerMockito.mock(Enumeration)
        PowerMockito.when(headers.hasMoreElements()).thenReturn(true)
        PowerMockito.when(headers.nextElement()).thenReturn("af30f120-ad1d-4b84-9f11-0f8bf34547c6").thenReturn("Bearer af30f120-ad1d-4b84-9f11-0f8bf34547c6")
        request.getHeaders(RequestVariableHolder.HEADER_JWT) >> { return headers }
        when: "方法调用"
        jwtTokenExtractor.extract(request)
        then: "结果分析"
        noExceptionThrown()
    }

    def "Extract-2"() {
        given: "准备mock参数"
        def request = Mock(HttpServletRequest)
        def headers = PowerMockito.mock(Enumeration)
        PowerMockito.when(headers.hasMoreElements()).thenReturn(true).thenReturn(false)
        PowerMockito.when(headers.nextElement()).thenReturn("af30f120-ad1d-4b84-9f11-0f8bf34547c6").thenReturn("Bearer af30f120-ad1d-4b84-9f11-0f8bf34547c6")
        request.getHeaders(RequestVariableHolder.HEADER_JWT) >> { return headers }
        when: "方法调用"
        jwtTokenExtractor.extract(request)
        then: "结果分析"
        noExceptionThrown()
    }
}
