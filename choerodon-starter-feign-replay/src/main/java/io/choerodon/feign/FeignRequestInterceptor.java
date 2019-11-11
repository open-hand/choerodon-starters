package io.choerodon.feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.security.jwt.crypto.sign.Signer;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;

import static io.choerodon.core.variable.RequestVariableHolder.HEADER_ROUTE_RULE;
import static io.choerodon.core.variable.RequestVariableHolder.HEADER_TOKEN;
import io.choerodon.core.oauth.CustomUserDetails;


/**
 * 拦截feign请求，为requestTemplate加上oauth token请求头
 *
 * @author jiatong.li
 */
@Component
public class FeignRequestInterceptor implements RequestInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(FeignRequestInterceptor.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String OAUTH_TOKEN_PREFIX = "Bearer ";
    private Signer signer;
    private CustomUserDetails defaultUserDetails;

    private CommonProperties commonProperties;

    public FeignRequestInterceptor(CommonProperties commonProperties) {
        this.commonProperties = commonProperties;
    }

    @PostConstruct
    private void init() {
        signer = new MacSigner(commonProperties.getOauthJwtKey());
        defaultUserDetails = new CustomUserDetails("default", "unknown", Collections.emptyList());
        defaultUserDetails.setUserId(commonProperties.getServiceAccountId());
        defaultUserDetails.setOrganizationId(0L);
        defaultUserDetails.setLanguage("zh_CN");
        defaultUserDetails.setTimeZone("CCT");
    }

    @Override
    public void apply(RequestTemplate template) {
        try {
            String token = null;
            if (SecurityContextHolder.getContext() != null
                    && SecurityContextHolder.getContext().getAuthentication() != null
                    && SecurityContextHolder.getContext().getAuthentication().getDetails() instanceof OAuth2AuthenticationDetails) {
                OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) SecurityContextHolder
                        .getContext().getAuthentication().getDetails();
                if (details.getTokenType() != null && details.getTokenValue() != null) {
                    token = details.getTokenType() + " " + details.getTokenValue();
                } else if (details.getDecodedDetails() instanceof CustomUserDetails) {
                    token = OAUTH_TOKEN_PREFIX
                            + JwtHelper.encode(OBJECT_MAPPER.writeValueAsString(details.getDecodedDetails()), signer).getEncoded();
                }
            }
            if (token == null) {
                token = OAUTH_TOKEN_PREFIX + JwtHelper.encode(OBJECT_MAPPER.writeValueAsString(defaultUserDetails), signer).getEncoded();
            }
            template.header(HEADER_TOKEN, token);
            setRouteRule(template);
        } catch (Exception e) {
            LOGGER.error("generate jwt token failed {}", e);
        }
    }

    private void setRouteRule(RequestTemplate template) {
        if (HystrixRequestContext.isCurrentThreadInitialized()) {
            String rule = RequestVariableHolder.ROUTE_RULE.get();
            if (rule != null) {
                template.header(HEADER_ROUTE_RULE, rule);
            }
        }

    }

}
