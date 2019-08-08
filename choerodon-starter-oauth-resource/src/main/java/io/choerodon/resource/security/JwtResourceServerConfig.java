package io.choerodon.resource.security;

import io.choerodon.core.oauth.CustomTokenConverter;
import io.choerodon.resource.permission.PublicPermissionOperationPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * 自定义ResourceServer配置
 *
 * @author superlee
 * @since 2019-08-08
 */
@EnableResourceServer
public class JwtResourceServerConfig extends ResourceServerConfigurerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtResourceServerConfig.class);

    private final String key;

    private final String[] jwtIgnore;

    private OAuth2WebSecurityExpressionHandler expressionHandler;

    public JwtResourceServerConfig(@Value("${choerodon.oauth.jwt.key:choerodon}") final String key,
                                   @Value("${choerodon.resource.jwt.ignore:#{null}}") final String[] jwtIgnore,
                                   OAuth2WebSecurityExpressionHandler expressionHandler) {
        this.key = key;
        this.jwtIgnore = jwtIgnore;
        this.expressionHandler = expressionHandler;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .exceptionHandling()
                .authenticationEntryPoint(new CustomAuthExceptionEntryPoint())
                .and()
                .antMatcher("/**")
                .authorizeRequests()
                .anyRequest()
                .access("@jwtTokenEnhancer.extractor(request)");
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer config) {
        config.tokenServices(tokenServices());
        //解决jwtTokenEnhancer bean访问受限的问题
        config.expressionHandler(expressionHandler);
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setAccessTokenConverter(new CustomTokenConverter());
        converter.setSigningKey(key);
        try {
            converter.afterPropertiesSet();
        } catch (Exception e) {
            LOGGER.warn("error.ChoerodonResourceServerConfiguration.accessTokenConverter {}", e);
        }
        return converter;
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        return defaultTokenServices;
    }

    @Bean
    public JwtTokenExtractor jwtTokenExtractor() {
        return new JwtTokenExtractor();
    }

    @Bean
    public JwtTokenParser jwtTokenEnhancer(PublicPermissionOperationPlugin publicPermissionOperationPlugin) {
        return new JwtTokenParser(jwtIgnore, publicPermissionOperationPlugin, tokenServices(), jwtTokenExtractor());
    }
}
