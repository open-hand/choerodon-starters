package io.choerodon.resource.filter;

import io.choerodon.core.variable.RequestVariableHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * @author dongfan117@gmail.com
 */
public class JwtTokenExtractor implements TokenExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenExtractor.class);

    private static final String HEADER_BEARER = "Bearer";

    @Override
    public Authentication extract(HttpServletRequest request) {
        String tokenValue = this.extractToken(request);
        if (tokenValue != null) {
            return new PreAuthenticatedAuthenticationToken(tokenValue, "");
        } else {
            return null;
        }
    }

    protected String extractToken(HttpServletRequest request) {
        String token = this.extractHeaderToken(request);
        if (token == null) {
            LOGGER.debug("Token not found in headers. Trying request parameters.");
        }

        return token;
    }

    protected String extractHeaderToken(HttpServletRequest request) {
        Enumeration headers = request.getHeaders(RequestVariableHolder.HEADER_TOKEN);

        String value;
        do {
            if (!headers.hasMoreElements()) {
                return null;
            }

            value = (String) headers.nextElement();
        } while (!value.toLowerCase().startsWith(HEADER_BEARER.toLowerCase()));


        String authHeaderValue = value.substring(HEADER_BEARER.length()).trim();
        request.setAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_TYPE, value.substring(0, HEADER_BEARER.length()).trim());
        int commaIndex = authHeaderValue.indexOf(44);
        if (commaIndex > 0) {
            authHeaderValue = authHeaderValue.substring(0, commaIndex);
        }

        return authHeaderValue;
    }
}