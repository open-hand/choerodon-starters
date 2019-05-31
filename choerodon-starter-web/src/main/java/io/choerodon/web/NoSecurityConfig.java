package io.choerodon.web;

import org.springframework.beans.factory.annotation.Value;
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
        "io.choerodon.hap.security.CasAutoConfiguration",
        "io.choerodon.hap.StandardSecurityConfig",
        "io.choerodon.hap.security.LdapAutoConfiguration"})
public class NoSecurityConfig extends WebSecurityConfigurerAdapter {

    public static Long userId;
    public static String userName;
    public static Long roleId;
    public static String[] allRoleId;
    public static Long companyId;
    public static String employeeCode;
    public static String locale;

    @Value("${default.security.userId:10001}")
    public void setUserId(Long userId) {
        NoSecurityConfig.userId = userId;
    }

    @Value("${default.security.roleId:10001}")
    public void setRoleId(Long roleId) {
        NoSecurityConfig.roleId = roleId;
    }

    @Value("${default.security.userName:admin}")
    public void setUserName(String userName) {
        NoSecurityConfig.userName = userName;
    }

    @Value("${default.security.allRoleId:10001}")
    public void setAllRoleId(String[] allRoleId) {
        NoSecurityConfig.allRoleId = allRoleId;
    }

    @Value("${default.security.employeeCode:ADMIN}")
    public void setEmployeeCode(String employeeCode) {
        NoSecurityConfig.employeeCode = employeeCode;
    }

    @Value("${default.security.companyId:}")
    public void setCompanyId(Long companyId) {
        NoSecurityConfig.companyId = companyId;
    }

    @Value("${default.security.locale:zh_CN}")
    public void setLocale(String locale) {
        NoSecurityConfig.locale = locale;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/*").authorizeRequests().anyRequest().permitAll();
    }
}
