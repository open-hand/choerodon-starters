package io.choerodon.resource.filter

import io.choerodon.resource.permission.PublicPermission
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate
import org.spockframework.runtime.Sputnik
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails
import org.springframework.security.oauth2.provider.authentication.TokenExtractor
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices
import org.springframework.web.context.support.SpringBeanAutowiringSupport
import spock.lang.Specification

import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletContext
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RunWith(PowerMockRunner.class)
@PrepareForTest([SpringBeanAutowiringSupport.class, SecurityContextHolder.class])
@PowerMockRunnerDelegate(Sputnik.class)
class JwtTokenFilterSpec extends Specification {
    private JwtTokenFilter jwtTokenFilter
    private TokenExtractor mockTokenExtractor = Mock(TokenExtractor)

    private ResourceServerTokenServices mockResourceServerTokenServices = Mock(ResourceServerTokenServices)

    def "Init"() {
        given: "参数准备"
        def publicPermissions = new HashSet<PublicPermission>()
        jwtTokenFilter = new JwtTokenFilter(mockResourceServerTokenServices, mockTokenExtractor, publicPermissions,"/v1/**")
        def filterConfig = Mock(FilterConfig)
        filterConfig.getServletContext() >> { return Mock(ServletContext) }
        and: "mock静态类"
        PowerMockito.mockStatic(SpringBeanAutowiringSupport.class)
        when: "方法调用"
        jwtTokenFilter.init(filterConfig)
        then: "无异常抛出"
        noExceptionThrown()
    }

    def "DoFilter-1"() {
        given: "jwtTokenFilter准备"
        def publicPermissions = new HashSet<PublicPermission>()
        def publicPermissions1 = new PublicPermission("path", mockHttpMethod1)
        publicPermissions.add(publicPermissions1)
        jwtTokenFilter = new JwtTokenFilter(mockResourceServerTokenServices, mockTokenExtractor, publicPermissions,"/v1/**")

        and: "参数准备"
        def request = Mock(HttpServletRequest)
        request.getRequestURI() >> { return "path" }
        request.getMethod() >> { return mockHttpMethod2 }
        def response = Mock(HttpServletResponse)
        def chain = Mock(FilterChain)
        when: "方法调用"
        jwtTokenFilter.doFilter(request, response, chain)
        then: "结果分析"
        noExceptionThrown()
        where: "分支覆盖"
        mockHttpMethod1 | mockHttpMethod2
        HttpMethod.GET  | HttpMethod.GET
        HttpMethod.GET  | HttpMethod.PUT

    }

    def "DoFilter-2"() {
        given: "jwtTokenFilter准备"
        def publicPermissions = new HashSet<PublicPermission>()
        def publicPermissions1 = new PublicPermission("path", HttpMethod.GET)
        publicPermissions.add(publicPermissions1)
        jwtTokenFilter = new JwtTokenFilter(mockResourceServerTokenServices, mockTokenExtractor, publicPermissions,"/v1/**")

        and: "参数准备"
        def request = Mock(HttpServletRequest)
        request.getRequestURI() >> { return "path" }
        request.getMethod() >> { return HttpMethod.PUT }
        def response = Mock(HttpServletResponse)
        def chain = Mock(FilterChain)

        and: "mock返回值准备"
        def authentication = Mock(AbstractAuthenticationToken)
        authentication.getPrincipal() >> { return "Bearer af30f120-ad1d-4b84-9f11-0f8bf34547c6" }
        authentication.getDetails() >> { return Mock(OAuth2AuthenticationDetails) }
        def oAuth2Authentication = Mock(OAuth2Authentication)
        oAuth2Authentication.getDetails() >> { return Mock(OAuth2AuthenticationDetails) }

        and: "mock方法"
        mockTokenExtractor.extract(_) >> { return authentication }
        mockResourceServerTokenServices.loadAuthentication(_) >> { return oAuth2Authentication }

        when: "方法调用"
        jwtTokenFilter.doFilter(request, response, chain)
        then: "结果分析"
        noExceptionThrown()
    }

    def "DoFilter-3"() {
        given: "jwtTokenFilter准备"
        def publicPermissions = new HashSet<PublicPermission>()
        def publicPermissions1 = new PublicPermission("path", HttpMethod.GET)
        publicPermissions.add(publicPermissions1)
        jwtTokenFilter = new JwtTokenFilter(mockResourceServerTokenServices, mockTokenExtractor, publicPermissions,"/v1/**")

        and: "参数准备"
        def request = Mock(HttpServletRequest)
        request.getRequestURI() >> { return "path" }
        request.getMethod() >> { return HttpMethod.PUT }
        def response = Mock(HttpServletResponse)
        def chain = Mock(FilterChain)

        and: "mock返回值准备"
        def authentication = Mock(AbstractAuthenticationToken)
        authentication.getPrincipal() >> { return "Bearer af30f120-ad1d-4b84-9f11-0f8bf34547c6" }
        authentication.getDetails() >> { return Mock(OAuth2AuthenticationDetails) }
        def oAuth2Authentication = Mock(OAuth2Authentication)
        oAuth2Authentication.getDetails() >> { return Mock(OAuth2AuthenticationDetails) }

        and: "mock方法"
        mockTokenExtractor.extract(_) >> { return authentication }
        mockResourceServerTokenServices.loadAuthentication(_) >> { return null }

        when: "方法调用"
        jwtTokenFilter.doFilter(request, response, chain)
        then: "结果分析"
        noExceptionThrown()
    }
}
