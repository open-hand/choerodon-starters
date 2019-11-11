package io.choerodon.feign;

import com.google.gson.Gson;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ZoneAvoidanceRule;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static io.choerodon.core.variable.RequestVariableHolder.HEADER_ROUTE_RULE;
import io.choerodon.core.oauth.CustomUserDetails;


/**
 * 根据用户自定义路由规则选择目标server
 *
 * @author zongw.lee@gmail.com
 */
public class CustomMetadataRule extends ZoneAvoidanceRule {

    private static final String JWT_SPLIT = ".";
    private static final String HEADER_BEARER = "Bearer";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String HEADER_TOKEN = "token";
    private static final Gson GSON = new Gson();
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomMetadataRule.class);

    private CommonProperties commonProperties;
    private Random random = new Random();

    public void setCommonProperties(CommonProperties commonProperties) {
        this.commonProperties = commonProperties;
    }

    @Override
    public Server choose(Object key) {
        LOGGER.info("Start to choose server to route");
        List<Server> servers = this.getPredicate().getEligibleServers(getLoadBalancer().getAllServers(), key);
        servers.forEach(v -> LOGGER.info("One of all servers: HOST => {}, IP => {}, Scheme => {}", v.getHost(), v.getPort(), v.getScheme()));
        if (servers.isEmpty()) {
            return null;
        }
        // 查询当前访问的CustomUserDetails
        CustomUserDetails customUserDetails = getCustomUserDetails();
        // 如果当前是oauth请求用户认证，则不需要做灰度发布策略
        if (customUserDetails == null) {
            LOGGER.info("CustomUserDetails is Empty");
            return servers.get(random.nextInt(servers.size()));
        }
        if (!StringUtils.isEmpty(customUserDetails.getRouteRuleCode())) {
            LOGGER.info("Start to handle grayscale launching strategy, Route_Rule: {}", customUserDetails.getRouteRuleCode());
            List<Server> ruleServers = servers.stream()
                    .filter(server -> judgeRouteRuleEnable(extractMetadata(server), customUserDetails.getRouteRuleCode()))
                    .collect(Collectors.toList());
            if (!ruleServers.isEmpty()) {
                LOGGER.info("Route to specific server ····");
                // 包含规则路由
                return ruleServers.get(random.nextInt(ruleServers.size()));
            }
        }

        return getDefaultRouteServer(servers);
    }

    private CustomUserDetails getCustomUserDetails() {
        LOGGER.info("Start to get CustomUserDetails");
        if (RequestContextHolder.getRequestAttributes() == null) {
            return null;
        }
        Object token = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getAttribute(HEADER_TOKEN);
        String jwtToken = null;
        if (!ObjectUtils.isEmpty(token)) {
            LOGGER.info("Get CustomUserDetails: start to prase jwtToken. Token:{}", token);
            jwtToken = token.toString().substring(HEADER_BEARER.length()).trim();
        } else {
            String authorization = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader(HEADER_AUTHORIZATION);
            if (ObjectUtils.isEmpty(authorization)) {
                return null;
            }
            LOGGER.info("Get CustomUserDetails: start to prase jwtToken. Authorization:{}", authorization);
            jwtToken = authorization.substring(HEADER_BEARER.length()).trim();
        }
        MacSigner macSigner = new MacSigner(commonProperties.getOauthJwtKey());
        if (!jwtToken.contains(JWT_SPLIT)) {
            return null;
        }
        String userInfo = JwtHelper.decodeAndVerify(jwtToken, macSigner).getClaims();
        LOGGER.info("CustomUserDetails info, userInfo: {}", userInfo);
        return GSON.fromJson(userInfo, CustomUserDetails.class);
    }

    private Server getDefaultRouteServer(List<Server> servers) {
        List<Server> noRuleServers = servers.stream()
                .filter(server -> extractMetadata(server).get(HEADER_ROUTE_RULE) == null)
                .collect(Collectors.toList());
        noRuleServers.forEach(v -> LOGGER.info("No rule's servers: Host => {}, IP => {}, Scheme => {}", v.getHost(), v.getPort(), v.getScheme()));
        if (noRuleServers.isEmpty()) {
            // 随机所有路由
            LOGGER.info("Route to one of all servers");
            return servers.get(random.nextInt(servers.size()));
        } else {
            // 无规则路由
            LOGGER.info("Route to one of no rule's servers");
            return noRuleServers.get(random.nextInt(noRuleServers.size()));
        }
    }

    private boolean judgeRouteRuleEnable(Map<String, String> metadata, String routeRuleCode) {
        String sourceRouteRuleCode = metadata.get(HEADER_ROUTE_RULE);
        return sourceRouteRuleCode != null && sourceRouteRuleCode.equals(routeRuleCode);
    }

    private Map<String, String> extractMetadata(Server server) {
        return ((DiscoveryEnabledServer) server).getInstanceInfo().getMetadata();
    }

}
