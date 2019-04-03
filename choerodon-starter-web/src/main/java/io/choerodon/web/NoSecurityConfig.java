package io.choerodon.web;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

/**
 * 默认没有安全权限控制.
 *
 * @author qiang.zeng
 * @since 2019/3/19.
 */
@Order(HIGHEST_PRECEDENCE)
@Configuration
@ConditionalOnMissingClass(value = {
        "com.hand.hap.security.CasAutoConfiguration",
        "com.hand.hap.StandardSecurityConfig",
        "com.hand.hap.security.LdapAutoConfiguration"})
public class NoSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/*").authorizeRequests().anyRequest().permitAll();
    }
}
