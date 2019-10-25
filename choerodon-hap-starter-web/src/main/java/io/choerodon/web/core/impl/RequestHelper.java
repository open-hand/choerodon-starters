package io.choerodon.web.core.impl;

import io.choerodon.core.helper.ApplicationContextHelper;
import io.choerodon.web.NoSecurityConfig;
import io.choerodon.web.core.IRequest;
import io.choerodon.web.core.IRequestListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 维护 IRequest 实例.
 *
 * @author shengyang.zhou@hand-china.com
 */
public final class RequestHelper {
    private static final Logger logger = LoggerFactory.getLogger(RequestHelper.class);

    private static ThreadLocal<IRequest> localRequestContext = new ThreadLocal<>();

    private static IRequestListener requestListener = new DefaultRequestListener();

    public IRequestListener getRequestListener() {
        return requestListener;
    }

    /**
     * requestListener可以更改.
     *
     * @param requestListener requestListener
     */

    public static void setRequestListener(IRequestListener requestListener) {
        RequestHelper.requestListener = requestListener;
    }

    public static IRequest newEmptyRequest() {
        return requestListener.newInstance();
    }

    /**
     * 设置 IRequest.
     * <p>
     * 不检查是否已经存在实例.(存在的话将被替换)
     *
     * @param request 新的 IRequest 实例
     */
    public static void setCurrentRequest(IRequest request) {
        localRequestContext.set(request);
    }

    /**
     * 清除当前实例.
     * <p>
     * 理论上优于 setCurrentRequest(null)
     */
    public static void clearCurrentRequest() {
        localRequestContext.remove();
    }

    /**
     * @return 当前session信息.
     */
    public static IRequest getCurrentRequest() {
        return getCurrentRequest(false);
    }

    /**
     * 取得当前线程 IRequest.
     * <p>
     *
     * @param returnEmptyForNull 是否在没有值的时候返回一个空的实例.<br>
     *                           注意,返回的空的实例不会设置为当前实例
     * @return 当前 IRequest 实例,或者一个空的实例
     */
    public static IRequest getCurrentRequest(boolean returnEmptyForNull) {
        IRequest request = localRequestContext.get();
        if (request == null && returnEmptyForNull) {
            return newEmptyRequest();
        }
        return request;
    }

    public static IRequest createServiceRequest(HttpServletRequest httpServletRequest) {
        IRequest requestContext = requestListener.newInstance();
        HttpSession session = httpServletRequest.getSession(false);
        try {
            ApplicationContextHelper.getApplicationContext().getBean("noSecurityConfig");
            logger.debug("Using default security config!");
            getDefaultSecurity(requestContext);
        } catch (Exception e) {
            logger.debug("Using custom security config!");
            if (session != null) {
                if (session.getAttribute(IRequest.FIELD_USER_ID) != null) {
                    requestContext.setUserId((Long) session.getAttribute(IRequest.FIELD_USER_ID));
                }
                if (session.getAttribute(IRequest.FIELD_ROLE_ID) != null) {
                    requestContext.setRoleId((Long) session.getAttribute(IRequest.FIELD_ROLE_ID));
                }
                if (session.getAttribute(IRequest.FIELD_USER_NAME) != null) {
                    requestContext.setUserName((String) session.getAttribute(IRequest.FIELD_USER_NAME));
                }
                if (session.getAttribute(IRequest.FIELD_COMPANY_ID) != null) {
                    requestContext.setCompanyId((Long) session.getAttribute(IRequest.FIELD_COMPANY_ID));
                }
                Object roleIds = session.getAttribute(IRequest.FIELD_ALL_ROLE_ID);
                if (roleIds instanceof Long[]) {
                    requestContext.setAllRoleId((Long[]) roleIds);
                }
                requestContext.setEmployeeCode((String) session.getAttribute(IRequest.FIELD_EMPLOYEE_CODE));
                Locale locale = getLocale(httpServletRequest);
                if (locale != null) {
                    requestContext.setLocale(locale.toString());
                }
            } else {
                //todo 设置oauth2 token信息 因为目前需要引入oauth2依赖 暂时先注释 看后面有没有更好的实现方式
           /* Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication instanceof OAuth2Authentication) {
                OAuth2Authentication oauth2Authentication = (OAuth2Authentication) authentication;
                if (oauth2Authentication.getUserAuthentication() != null) {
                    requestContext.setUserName(oauth2Authentication.getPrincipal().toString());
                    Map<String, Serializable> extensions = oauth2Authentication.getOAuth2Request().getExtensions();
                    if (extensions.get(IRequest.FIELD_USER_ID) != null) {
                        requestContext.setUserId(Long.valueOf(extensions.get(IRequest.FIELD_USER_ID).toString()));
                    }
                    if (extensions.get(IRequest.FIELD_ALL_ROLE_ID) != null) {
                        List ids = (List) extensions.get(IRequest.FIELD_ALL_ROLE_ID);
                        Long[] idsArry = new Long[ids.size()];
                        for (int i = 0; i < ids.size(); i++) {
                            idsArry[i] = Long.valueOf(ids.get(i).toString());
                        }
                        requestContext.setAllRoleId(idsArry);
                    }
                    if (extensions.get(IRequest.FIELD_COMPANY_ID) != null) {
                        requestContext.setCompanyId(Long.valueOf(extensions.get(IRequest.FIELD_COMPANY_ID).toString()));
                    }
                    if (extensions.get(IRequest.FIELD_EMPLOYEE_CODE) != null) {
                        requestContext.setEmployeeCode(extensions.get(IRequest.FIELD_EMPLOYEE_CODE).toString());
                    }
                    if (extensions.get(BaseConstants.PREFERENCE_LOCALE) != null) {
                        requestContext.setLocale(extensions.get(BaseConstants.PREFERENCE_LOCALE).toString());
                    } else {
                        Locale locale = getLocale(httpServletRequest);
                        if (locale != null) {
                            requestContext.setLocale(locale.toString());
                        }
                    }
                }
            }*/
            }
        }
        Map<String, String> mdcMap = MDC.getCopyOfContextMap();
        if (mdcMap != null) {
            mdcMap.forEach((k, v) -> requestContext.setAttribute(IRequest.MDC_PREFIX.concat(k), v));
        }
        requestListener.afterInitialize(httpServletRequest, requestContext);
        return requestContext;
    }

    /**
     * 配置默认安全.
     */
    private static void getDefaultSecurity(IRequest requestContext) {
        requestContext.setUserId(NoSecurityConfig.userId);
        requestContext.setUserName(NoSecurityConfig.userName);
        requestContext.setRoleId(NoSecurityConfig.roleId);
        if (NoSecurityConfig.allRoleId != null && NoSecurityConfig.allRoleId.length > 0) {
            List<Long> roleIds = new ArrayList<>();
            for (String roleId : NoSecurityConfig.allRoleId) {
                roleIds.add(Long.parseLong(roleId));
            }
            requestContext.setAllRoleId(roleIds.toArray(new Long[roleIds.size()]));
        }
        requestContext.setEmployeeCode(NoSecurityConfig.employeeCode);
        requestContext.setCompanyId(NoSecurityConfig.companyId);
        requestContext.setLocale(NoSecurityConfig.locale);
    }

    /**
     * 获取请求Locale信息.
     *
     * @param request HttpServletRequest
     * @return Locale
     */
    private static Locale getLocale(HttpServletRequest request) {
        Locale locale;
        LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
        if (localeResolver != null) {
            locale = localeResolver.resolveLocale(request);
        } else {
            locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
            if (locale == null) {
                locale = request.getLocale();
            }
        }
        return locale;
    }
}
