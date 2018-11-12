package io.choerodon.core.infra.common.utils;

import io.choerodon.core.oauth.CustomUserDetails;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dengyouquan
 **/
public class SpockUtils {
    public static CustomUserDetails getCustomUserDetails() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        CustomUserDetails customUserDetails = new CustomUserDetails("dengyouquan", "123456", authorities);
        customUserDetails.setOrganizationId(1L);
        customUserDetails.setEmail("youquan.deng@hand-china.com");
        customUserDetails.setAdmin(true);
        customUserDetails.setTimeZone("CTT");
        customUserDetails.setUserId(1L);
        customUserDetails.setLanguage("zh_CN");
        return customUserDetails;
    }

    public static CustomUserDetails getNotAdminCustomUserDetails() {
        CustomUserDetails customUserDetails = getCustomUserDetails();
        customUserDetails.setAdmin(false);
        return customUserDetails;
    }
}
