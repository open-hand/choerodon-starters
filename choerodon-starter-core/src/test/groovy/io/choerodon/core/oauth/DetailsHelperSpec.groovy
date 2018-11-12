package io.choerodon.core.oauth

import io.choerodon.core.infra.common.utils.SpockUtils
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PowerMockIgnore
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate
import org.spockframework.runtime.Sputnik
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails
import spock.lang.Specification

/**
 * @author dengyouquan
 * */
@RunWith(PowerMockRunner)
@PowerMockRunnerDelegate(Sputnik)
@PrepareForTest([SecurityContextHolder])
@PowerMockIgnore("javax.security.*")
class DetailsHelperSpec extends Specification {
    private SecurityContext securityContext = Mock(SecurityContext)
    private Authentication authentication = Mock(Authentication)

    def setup() {
        PowerMockito.mockStatic(SecurityContextHolder)
        PowerMockito.when(SecurityContextHolder.getContext()).thenReturn(securityContext)
    }

    def "GetUserDetails"() {
        given: "构造请求参数"
        CustomUserDetails customUserDetails = SpockUtils.getCustomUserDetails()
        OAuth2AuthenticationDetails auth2AuthenticationDetails = Mock(OAuth2AuthenticationDetails)

        when: "调用方法[为null]"
        CustomUserDetails result = DetailsHelper.getUserDetails()
        then: "校验结果"
        result == null

        when: "调用方法"
        result = DetailsHelper.getUserDetails()
        then: "校验结果"
        result.equals(customUserDetails)
        securityContext.getAuthentication() >> { authentication }
        authentication.getPrincipal() >> { customUserDetails }

        when: "调用方法"
        result = DetailsHelper.getUserDetails()
        then: "校验结果"
        result.equals(customUserDetails)
        securityContext.getAuthentication() >> { authentication }
        authentication.getDetails() >> { auth2AuthenticationDetails }
        auth2AuthenticationDetails.getDecodedDetails() >> { customUserDetails }
    }

    def "GetClientDetails"() {
        given: "构造请求参数"
        CustomClientDetails customClientDetails = new CustomClientDetails()
        customClientDetails.setOrganizationId(1L)
        OAuth2AuthenticationDetails auth2AuthenticationDetails = Mock(OAuth2AuthenticationDetails)

        when: "调用方法[为null]"
        CustomClientDetails result = DetailsHelper.getClientDetails()
        then: "校验结果"
        result == null

        when: "调用方法"
        result = DetailsHelper.getClientDetails()
        then: "校验结果"
        result.equals(customClientDetails)
        securityContext.getAuthentication() >> { authentication }
        authentication.getPrincipal() >> { customClientDetails }

        when: "调用方法"
        result = DetailsHelper.getClientDetails()
        then: "校验结果"
        result.equals(customClientDetails)
        securityContext.getAuthentication() >> { authentication }
        authentication.getDetails() >> { auth2AuthenticationDetails }
        auth2AuthenticationDetails.getDecodedDetails() >> { customClientDetails }
    }
}
