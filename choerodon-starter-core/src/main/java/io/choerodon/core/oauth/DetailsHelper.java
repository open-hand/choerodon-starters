package io.choerodon.core.oauth;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

/**
 * 根据请求解析出userDetail和clientDetail对象
 *
 * @author wuguokai
 */
public class DetailsHelper {

    private DetailsHelper() {
    }

    /**
     * {@inheritDoc}
     * 获取访问用户的userDetail对象
     *
     * @return CustomUserDetails
     */
    public static CustomUserDetails getUserDetails() {
        if (SecurityContextHolder.getContext() == null
                || SecurityContextHolder.getContext().getAuthentication() == null) {
            return null;
        }
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return (CustomUserDetails) principal;
        }
        Object details = SecurityContextHolder.getContext().getAuthentication().getDetails();
        if (details instanceof OAuth2AuthenticationDetails) {
            Object decodedDetails = ((OAuth2AuthenticationDetails) details).getDecodedDetails();
            if (decodedDetails instanceof CustomUserDetails) {
                return (CustomUserDetails) decodedDetails;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * 获取访问的clientDetail对象
     *
     * @return CustomClientDetails
     */
    public static CustomClientDetails getClientDetails() {
        if (SecurityContextHolder.getContext() == null
                || SecurityContextHolder.getContext().getAuthentication() == null) {
            return null;
        }
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomClientDetails) {
            return (CustomClientDetails) principal;
        }
        Object details = SecurityContextHolder.getContext().getAuthentication().getDetails();
        if (details instanceof OAuth2AuthenticationDetails) {
            Object decodedDetails = ((OAuth2AuthenticationDetails) details).getDecodedDetails();
            if (decodedDetails instanceof CustomClientDetails) {
                return (CustomClientDetails) decodedDetails;
            }
        }
        return null;
    }
}
