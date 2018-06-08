package io.choerodon.core.oauth;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;

/**
 * 定制的access token转换
 *
 * @author wuguokai
 */
@SuppressWarnings("unchecked")
public class CustomTokenConverter extends DefaultAccessTokenConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomTokenConverter.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String USER_ID = "userId";
    private static final String ORGANIZATION_ID = "organizationId";
    private static final String ADDITION_INFO = "additionInfo";

    private UserDetailsService userDetailsService;
    private ClientDetailsService clientDetailsService;

    public UserDetailsService getUserDetailsService() {
        return userDetailsService;
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public ClientDetailsService getClientDetailsService() {
        return clientDetailsService;
    }

    public void setClientDetailsService(ClientDetailsService clientDetailsService) {
        this.clientDetailsService = clientDetailsService;
    }

    /**
     * 根据jwt token和认证对象查询出用户信息map集合
     *
     * @param token          access_token
     * @param authentication 认证信息
     * @return map     用户信息的map集合
     */
    @Override
    public Map<String, ?> convertAccessToken(OAuth2AccessToken token,
                                             OAuth2Authentication authentication) {
        Map<String, Object> map = (Map<String, Object>) super.convertAccessToken(token, authentication);
        Object details = authentication.getPrincipal();
        if (details instanceof CustomUserDetails) {
            CustomUserDetails user = (CustomUserDetails) userDetailsService
                    .loadUserByUsername(((CustomUserDetails) details).getUsername());
            map.put(USER_ID, user.getUserId().toString());
            map.put("language", user.getLanguage());
            map.put("timeZone", user.getTimeZone());
            map.put("email", user.getEmail());
            map.put(ORGANIZATION_ID, user.getOrganizationId().toString());
            map.put("admin", user.getAdmin());
            if (user.getAdditionInfo() != null) {
                map.put(ADDITION_INFO, user.getAdditionInfo());
            }
        } else if (details instanceof String) {
            CustomClientDetails client = (CustomClientDetails) clientDetailsService
                    .loadClientByClientId((String) details);
            map.put(ORGANIZATION_ID, client.getOrganizationId());
            if (client.getAdditionalInformation() != null) {
                map.put(ADDITION_INFO, client.getAdditionalInformation());
            }
        }
        return map;
    }

    /**
     * 根据用户信息集合提取出一个认证信息对象
     *
     * @param map 用户信息集合
     * @return OAuth2Authentication
     */
    @Override
    public OAuth2Authentication extractAuthentication(Map<String, ?> map) {
        if (map.get("principal") != null) {
            map = (Map<String, Object>) map.get("principal");
        }
        if (!map.containsKey("user_name")) {
            ((Map<String, Object>) map).put("user_name", ((Map<String, Object>) map).get("username"));
        }
        OAuth2Authentication authentication = super.extractAuthentication(map);
        if (map.containsKey(USER_ID)) {
            CustomUserDetails user = new CustomUserDetails(authentication.getName(),
                    "unknown password", authentication.getAuthorities());
            user.setUserId((long) ((Integer) map.get(USER_ID)));
            user.setLanguage((String) map.get("language"));
            user.setTimeZone((String) map.get("timeZone"));
            user.setEmail((String) map.get("email"));
            user.setAdmin((Boolean) map.get("admin"));
            user.setOrganizationId((long) ((Integer) map.get(ORGANIZATION_ID)));
            try {
                if (map.get(ADDITION_INFO) != null) {
                    user.setAdditionInfo((Map) map.get(ADDITION_INFO));
                }
            } catch (Exception e) {
                LOGGER.warn("parser addition info error:{}", e);
            }
            authentication.setDetails(user);
        } else {
            CustomClientDetails client = new CustomClientDetails();
            client.setClientId(authentication.getName());
            client.setAuthorities(authentication.getAuthorities());
            client.setOrganizationId((long) ((Integer) map.get(ORGANIZATION_ID)));
            try {
                if (map.get(ADDITION_INFO) != null) {
                    client.setAdditionalInformation(MAPPER.readValue((String) map.get(ADDITION_INFO), Map.class));
                }
            } catch (Exception e) {
                LOGGER.warn("parser addition info error:{}", e);
            }
            authentication.setDetails(client);
        }
        return authentication;
    }

}
