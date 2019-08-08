//package io.choerodon.resource.config
//
//import io.choerodon.resource.security.JwtTokenExtractor
//import io.choerodon.resource.filter.JwtTokenFilter
//import io.choerodon.resource.permission.PublicPermissionOperationPlugin
//import org.junit.runner.RunWith
//import org.powermock.api.mockito.PowerMockito
//import org.powermock.core.classloader.annotations.PrepareForTest
//import org.powermock.modules.junit4.PowerMockRunner
//import org.powermock.modules.junit4.PowerMockRunnerDelegate
//import org.spockframework.runtime.Sputnik
//import org.springframework.security.config.annotation.web.builders.WebSecurity
//
//@RunWith(PowerMockRunner.class)
//@PrepareForTest([WebSecurity.class])
//@PowerMockRunnerDelegate(Sputnik.class)
//class ChoerodonResourceServerConfigurationSpec extends spock.lang.Specification {
//
//    void setup() {
//        String[] pattern = ["/v1/**"]
//    }
//
//    def "Configure"() {
//        given: "参数准备"
//        def web = PowerMockito.mock(WebSecurity.class)
//        def ignoredRequestConfigurer = PowerMockito.mock(WebSecurity.IgnoredRequestConfigurer.class)
//        PowerMockito.when(ignoredRequestConfigurer.antMatchers("/v1/**")).thenReturn(ignoredRequestConfigurer)
//        PowerMockito.when(web.ignoring()).thenReturn(ignoredRequestConfigurer)
//        when: "方法调用"
//        then: "无异常抛出"
//        noExceptionThrown()
//    }
//
//    def "SomeFilterRegistration"() {
//        given: "参数准备"
//        def jwtTokenFilter = Mock(JwtTokenFilter)
//        when: "方法调用"
//        then: "无异常抛出"
//        noExceptionThrown()
//    }
//
//    def "JwtTokenExtractor"() {
//        when: "方法调用"
//        then: "无异常抛出"
//        noExceptionThrown()
//    }
//
//    def "JwtTokenFilter"() {
//        given: "创建Mock参数"
//        def publicPermissionOperationPlugin = Mock(PublicPermissionOperationPlugin)
//        def jwtTokenExtractor = Mock(JwtTokenExtractor)
//        when: "方法调用"
//        def filter = choerodonResourceServerConfiguration.jwtTokenFilter(publicPermissionOperationPlugin, jwtTokenExtractor)
//        then: "无异常抛出"
//    }
//}
