package io.choerodon.mybatis

import io.choerodon.liquibase.LiquibaseConfig
import io.choerodon.liquibase.LiquibaseExecutor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Import

import javax.annotation.PostConstruct

/**
 * @author superlee
 */

@TestConfiguration
@Import(LiquibaseConfig)
class IntegrationTestConfiguration {

//    @Value('${choerodon.oauth.jwt.key:choerodon}')
//    String key
//
//    @Autowired
//    TestRestTemplate testRestTemplate

    @Autowired
    LiquibaseExecutor liquibaseExecutor

//    final ObjectMapper objectMapper = new ObjectMapper()

    @PostConstruct
    void init() {
        //通过liquibase初始化h2数据库
        liquibaseExecutor.execute()
        //给TestRestTemplate的请求头部添加JWT
//        setTestRestTemplateJWT()
    }

//    private void setTestRestTemplateJWT() {
//        testRestTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory())
//        testRestTemplate.getRestTemplate().setInterceptors([new ClientHttpRequestInterceptor() {
//            @Override
//            ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
//                httpRequest.getHeaders()
//                        .add('JWT_Token', createJWT(key, objectMapper))
//                return clientHttpRequestExecution.execute(httpRequest, bytes)
//            }
//        }])
//    }

//    static String createJWT(final String key, final ObjectMapper objectMapper) {
//        Signer signer = new MacSigner(key)
//        CustomUserDetails defaultUserDetails = new CustomUserDetails('default', 'unknown', Collections.emptyList())
//        defaultUserDetails.setUserId(1L)
//        defaultUserDetails.setOrganizationId(1L)
//        defaultUserDetails.setLanguage('zh_CN')
//        defaultUserDetails.setTimeZone('CCT')
//        String jwtToken = null
//        try {
//            jwtToken = 'Bearer ' + JwtHelper.encode(objectMapper.writeValueAsString(defaultUserDetails), signer).getEncoded()
//        } catch (IOException e) {
//            e.printStackTrace()
//        }
//        return jwtToken
//    }

}