package io.choerodon.resource.security;

import io.choerodon.resource.permission.PublicPermission;
import io.choerodon.resource.permission.PublicPermissionOperationPlugin;
import io.choerodon.resource.security.exception.AuthenticationRequestFailedException;
import io.choerodon.resource.security.exception.JwtTokenNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Jwt token解析器
 *
 * @author superlee
 * @since 2019-08-08
 */
public class JwtTokenParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenParser.class);

    private final String[] jwtIgnore;

    private PublicPermissionOperationPlugin publicPermissionOperationPlugin;

    private final DefaultTokenServices defaultTokenServices;

    private final JwtTokenExtractor jwtTokenExtractor;

    private static final AntPathMatcher MATCHER = new AntPathMatcher();

    private static final String[] DEFAULT_JWT_IGNORE = {"/choerodon/**", "/", "/dis/**", "/env-config.js"};

    public JwtTokenParser(final String[] jwtIgnore,
                          PublicPermissionOperationPlugin publicPermissionOperationPlugin,
                          DefaultTokenServices defaultTokenServices,
                          JwtTokenExtractor jwtTokenExtractor) {
        this.publicPermissionOperationPlugin = publicPermissionOperationPlugin;
        this.defaultTokenServices = defaultTokenServices;
        this.jwtTokenExtractor = jwtTokenExtractor;
        if (ObjectUtils.isEmpty(jwtIgnore)) {
            this.jwtIgnore = DEFAULT_JWT_IGNORE;
        } else {
            this.jwtIgnore =
                    Stream.concat(Arrays.stream(jwtIgnore), Arrays.stream(DEFAULT_JWT_IGNORE)).toArray(String[]::new);
        }
    }

    public boolean extractor(HttpServletRequest request) {
        Set<PublicPermission> publicPermissions = publicPermissionOperationPlugin.getPublicPaths();

        for (PublicPermission publicPermission : publicPermissions) {
            if (MATCHER.match(publicPermission.path, request.getRequestURI()) &&
                    publicPermission.method.matches(request.getMethod())) {
                //public接口放行
                return true;
            }
        }

        for (String ignore : jwtIgnore) {
            if (MATCHER.match(ignore, request.getRequestURI())) {
                return true;
            }
        }
        try {
            Authentication authentication = this.jwtTokenExtractor.extract(request);

            if (authentication == null) {
                if (isAuthenticated()) {
                    LOGGER.debug("Clearing security context.");
                    SecurityContextHolder.clearContext();
                }
                throw new JwtTokenNotFoundException("No Jwt token in request");
            } else {
                request.setAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_VALUE, authentication.getPrincipal());
                if (authentication instanceof AbstractAuthenticationToken) {
                    AbstractAuthenticationToken needsDetails = (AbstractAuthenticationToken) authentication;
                    needsDetails.setDetails(new OAuth2AuthenticationDetails(request));
                }

                Authentication authResult = this.authenticate(authentication);
                LOGGER.debug("Authentication success: {}", authResult);
                SecurityContextHolder.getContext().setAuthentication(authResult);
            }
            return true;
        } catch (OAuth2Exception e) {
            SecurityContextHolder.clearContext();
            throw new AuthenticationRequestFailedException("Authentication request failed");
        }
    }

    protected Authentication authenticate(Authentication authentication) {
        if (authentication == null) {
            throw new InvalidTokenException("Invalid token (token not found)");
        } else {
            String token = (String) authentication.getPrincipal();
            OAuth2Authentication auth = defaultTokenServices.loadAuthentication(token);
            if (auth == null) {
                throw new InvalidTokenException("Invalid token: " + token);
            } else {
                if (authentication.getDetails() instanceof OAuth2AuthenticationDetails) {
                    OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) authentication.getDetails();
                    if (!details.equals(auth.getDetails())) {
                        details.setDecodedDetails(auth.getDetails());
                    }
                }

                auth.setDetails(authentication.getDetails());
                auth.setAuthenticated(true);
                return auth;
            }
        }
    }

    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && !(authentication instanceof AnonymousAuthenticationToken);
    }
}
