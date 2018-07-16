package io.choerodon.resource.config;

import io.choerodon.core.oauth.CustomTokenConverter;
import io.choerodon.resource.filter.JwtTokenExtractor;
import io.choerodon.resource.filter.JwtTokenFilter;
import io.choerodon.resource.permission.PublicPermissionOperationPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import static javax.servlet.DispatcherType.REQUEST;

/**
 * @author dongfan117@gmail.com
 */
public class ChoerodonResourceServerConfiguration extends WebSecurityConfigurerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChoerodonResourceServerConfiguration.class);

    @Value("${choerodon.oauth.jwt.key:choerodon}")
    private String key;

    @Value("${choerodon.resource.pattern:/v1/*}")
    private String pattern;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/v1/**").antMatchers("/prometheus");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().disable();
    }

    @Bean
    public FilterRegistrationBean someFilterRegistration(JwtTokenFilter jwtTokenFilter) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(jwtTokenFilter);
        registration.addUrlPatterns(pattern);
        registration.setName("jwtTokenFilter");
        registration.setOrder(Ordered.LOWEST_PRECEDENCE);
        registration.setDispatcherTypes(REQUEST);
        return registration;
    }

    @Bean
    public JwtTokenExtractor jwtTokenExtractor() {
        return new JwtTokenExtractor();
    }

    @Bean
    public JwtTokenFilter jwtTokenFilter(PublicPermissionOperationPlugin publicPermissionOperationPlugin, JwtTokenExtractor jwtTokenExtractor) {
        return new JwtTokenFilter(tokenServices(), jwtTokenExtractor, publicPermissionOperationPlugin.getPublicPaths());
    }

    /**
     * DefaultTokenService Bean
     *
     * @return DefaultTokenService对象
     */
    private DefaultTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        return defaultTokenServices;
    }

    private TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    /**
     * 返回converter
     *
     * @return converter
     */
    private JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setAccessTokenConverter(new CustomTokenConverter());
        converter.setSigningKey(key);
        try {
            converter.afterPropertiesSet();
        } catch (Exception e) {
            LOGGER.warn("error.ChoerodonResourceServerConfiguration.accessTokenConverter {}", e.toString());
        }
        return converter;
    }

}