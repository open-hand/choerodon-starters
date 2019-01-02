package io.choerodon.asgard

import com.fasterxml.jackson.databind.ObjectMapper
import io.choerodon.asgard.common.AsgardAutoConfiguration
import io.choerodon.core.oauth.CustomUserDetails
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.security.jwt.JwtHelper
import org.springframework.security.jwt.crypto.sign.MacSigner
import org.springframework.security.jwt.crypto.sign.Signer

import javax.annotation.PostConstruct

/**
 * @author dongfan117@gmail.com
 */
@Import(AsgardAutoConfiguration)
@TestConfiguration
class IntegrationTestConfiguration {

    @Value('${choerodon.oauth.jwt.key:choerodon}')
    String key

    final ObjectMapper objectMapper = new ObjectMapper()

    @Autowired
    TestRestTemplate template

    @PostConstruct
    void init() {
        setTestRestTemplateJWT()
    }

    private void setTestRestTemplateJWT() {
        template.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory())
        template.getRestTemplate().setInterceptors([new ClientHttpRequestInterceptor() {
            @Override
            ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
                httpRequest.getHeaders()
                        .add('JWT_Token', createJWT(key, objectMapper))
                return clientHttpRequestExecution.execute(httpRequest, bytes)
            }
        }])
    }

    static String createJWT(final String key, final ObjectMapper objectMapper) {
        Signer signer = new MacSigner(key)
        CustomUserDetails defaultUserDetails = new CustomUserDetails('default', 'unknown', Collections.emptyList())
        defaultUserDetails.setUserId(0L)
        defaultUserDetails.setOrganizationId(0L)
        defaultUserDetails.setLanguage('zh_CN')
        defaultUserDetails.setTimeZone('CCT')
        String jwtToken = null
        try {
            jwtToken = 'Bearer ' + JwtHelper.encode(objectMapper.writeValueAsString(defaultUserDetails), signer).getEncoded()
        } catch (IOException e) {
            e.printStackTrace()
        }
        return jwtToken
    }


}
