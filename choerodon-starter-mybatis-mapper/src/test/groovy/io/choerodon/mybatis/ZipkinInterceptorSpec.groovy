package io.choerodon.mybatis

import org.apache.ibatis.mapping.BoundSql
import org.apache.ibatis.mapping.MappedStatement
import org.apache.ibatis.plugin.Invocation
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate
import org.spockframework.runtime.Sputnik
import org.springframework.cloud.sleuth.Span
import org.springframework.cloud.sleuth.SpanAccessor
import spock.lang.Specification

/**
 * Created by superlee on 2018/10/16.
 */
@PrepareForTest(MappedStatement.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Sputnik.class)
class ZipkinInterceptorSpec extends Specification {

    def "Intercept"() {
        given: ""
        def invocation = Mock(Invocation)
        def spanAccessor = Mock(SpanAccessor)
        def mappedStatement = PowerMockito.mock(MappedStatement.class)
        def boundSql = Mock(BoundSql)
        PowerMockito.when(mappedStatement.getBoundSql(Mockito.any())).thenReturn(boundSql)
        def arrays = [mappedStatement, boundSql] as Object[]

        def span = Mock(Span)

        when: ""
        def zipkinInterceptor = new ZipkinInterceptor(null)
        def value = zipkinInterceptor.intercept(invocation)

        then: ""
        1 * invocation.getArgs() >> arrays
        1 * invocation.proceed() >> "value"
        value == "value"

        when: ""
        def zipkinInterceptor1 = new ZipkinInterceptor(spanAccessor)
        def value1 = zipkinInterceptor1.intercept(invocation)

        then: ""
        2 * invocation.getArgs() >> arrays
        1 * invocation.proceed() >> "value1"
        2 * spanAccessor.getCurrentSpan() >> span
        1 * boundSql.getSql() >> ""
        value1 == "value1"


    }

    def "Plugin"() {
        given: ""
        def spanAccessor = Mock(SpanAccessor)
        def zipkinInterceptor = new ZipkinInterceptor(spanAccessor)

        when: ""
        def value = zipkinInterceptor.plugin(new Object())

        then: ""
        value instanceof Object
    }

    def "SetProperties"() {
    }
}
