package io.choerodon.feign;

import java.io.IOException;
import java.util.Collections;
import javax.annotation.PostConstruct;

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

import io.choerodon.core.oauth.CustomUserDetails;


/**
 * 拦截feign请求，为requestTemplate加上oauth token请求头
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
        if (SecurityContextHolder.getContext() != null
                && SecurityContextHolder.getContext().getAuthentication() != null) {
            OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) SecurityContextHolder
                    .getContext().getAuthentication().getDetails();
            template.header(RequestVariableHolder.HEADER_JWT,details.getTokenType() + " " + details.getTokenValue());
        } else {
            try {
                String jwtToken = OAUTH_TOKEN_PREFIX
                        + JwtHelper.encode(OBJECT_MAPPER.writeValueAsString(defaultUserDetails), signer).getEncoded();
                template.header(RequestVariableHolder.HEADER_JWT,jwtToken);
            } catch (IOException e) {
                LOGGER.error("generate jwt token failed {}", e);
            }
        }
        setLabel(template);
    }

    private void setLabel(RequestTemplate template) {
        if (HystrixRequestContext.isCurrentThreadInitialized()) {
            String label = RequestVariableHolder.LABEL.get();
            if (label != null) {
                template.header(RequestVariableHolder.HEADER_LABEL, label);
            }
        }

    }

}
