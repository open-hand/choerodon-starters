package io.choerodon.core.oauth

import io.choerodon.core.infra.common.utils.SpockUtils
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.provider.ClientDetailsService
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.OAuth2Request
import spock.lang.Specification

import java.lang.reflect.Field

/**
 * @author dengyouquan
 * */
class CustomTokenConverterSpec extends Specification {
    private UserDetailsService userDetailsService = Mock(UserDetailsService)
    private ClientDetailsService clientDetailsService = Mock(ClientDetailsService)
    private CustomTokenConverter customTokenConverter = new CustomTokenConverter()

    def setup() {
        customTokenConverter.setClientDetailsService(clientDetailsService)
        customTokenConverter.setUserDetailsService(userDetailsService)
    }

    def "ConvertAccessToken"() {
        given: "构造请求参数"
        OAuth2AccessToken token = Mock(OAuth2AccessToken)
        OAuth2Request storedRequest = Mock(OAuth2Request)
        Authentication userAuthentication = Mock(Authentication)
        OAuth2Authentication authentication = new OAuth2Authentication(storedRequest, userAuthentication)
        CustomUserDetails customUserDetails = SpockUtils.getCustomUserDetails()
        CustomClientDetails customClientDetails = new CustomClientDetails()
        customClientDetails.setOrganizationId(1L)
        String string = "string"
        OAuth2Request clientToken = new OAuth2Request(null, "1", null, true, null, null, "", null, null)


        when: "调用方法[CustomUserDetails]"
        Map<String, Object> map = customTokenConverter.convertAccessToken(token, authentication) as Map<String, Object>

        then: "校验结果"
        authentication.getOAuth2Request() >> { clientToken }
        userDetailsService.loadUserByUsername(_) >> { customUserDetails }
        token.getAdditionalInformation() >> { new HashMap<String, Object>() }
        userAuthentication.getPrincipal() >> { customUserDetails }
        map.get("userId").equals(customUserDetails.getUserId().toString())

        when: "调用方法[String]"
        map = customTokenConverter.convertAccessToken(token, authentication) as Map<String, Object>

        then: "校验结果"
        authentication.getOAuth2Request() >> { clientToken }
        clientDetailsService.loadClientByClientId(_) >> { customClientDetails }
        token.getAdditionalInformation() >> { new HashMap<String, Object>() }
        userAuthentication.getPrincipal() >> { string }
        map.get("organizationId").equals(customClientDetails.getOrganizationId())
    }

    def "ExtractAuthentication"() {
        given: "构造请求参数"
        CustomUserDetails customUserDetails = SpockUtils.getCustomUserDetails()
        Map<String, Object> map = new Hashtable<>()
        map.put("username", customUserDetails.getUsername())
        map.put("email", customUserDetails.getEmail())
        map.put("timeZone", customUserDetails.getTimeZone())
        map.put("language", customUserDetails.getLanguage())
        map.put("admin", customUserDetails.getAdmin())
        map.put("organizationId", 1)
        map.put("additionInfo", new HashMap<String, Object>())
        map.put("principal", map)

        when: "调用方法[CustomClientDetails]"
        OAuth2Authentication authentication = customTokenConverter.extractAuthentication(map)

        then: "校验结果"
        CustomClientDetails customClientDetails = authentication.getDetails() as CustomClientDetails
        customClientDetails.getOrganizationId().equals(1L)

        when: "调用方法[CustomUserDetails]"
        map.put("userId", 1)
        authentication = customTokenConverter.extractAuthentication(map)

        then: "校验结果"
        CustomUserDetails details = authentication.getDetails() as CustomUserDetails
        details.getUserId().equals(customUserDetails.getUserId())
    }
}
