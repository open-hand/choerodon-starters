/*
 * Copyright 2013-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.config.client;

import io.choerodon.core.convertor.ApplicationContextHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.cloud.config.helper.HelperZuulRoutesProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.MapPropertySource;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.retry.annotation.Retryable;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;

import static java.util.stream.Collectors.*;
import static org.springframework.cloud.config.client.ConfigClientProperties.STATE_HEADER;
import static org.springframework.cloud.config.client.ConfigClientProperties.TOKEN_HEADER;

/**
 * spring cloud client源代码
 *
 * @author Dave Syer
 * @author Mathieu Ouellet
 */
public class ConfigServicePropertySourceLocator implements PropertySourceLocator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigServicePropertySourceLocator.class);

    private RestTemplate restTemplate;
    private ConfigClientProperties defaultProperties;

    public ConfigServicePropertySourceLocator(ConfigClientProperties defaultProperties) {
        this.defaultProperties = defaultProperties;
    }

    @Override
    @Retryable(interceptor = "configServerRetryInterceptor")
    public org.springframework.core.env.PropertySource<?> locate(
            org.springframework.core.env.Environment environment) {
        ConfigClientProperties properties = this.defaultProperties.override(environment);
        CompositePropertySource composite = new CompositePropertySource("configService");
        RestTemplate localRestTemplate = this.restTemplate == null ? getSecureRestTemplate(properties)
                : this.restTemplate;
        Exception error = null;
        String errorBody = null;
        LOGGER.info("Fetching config from server at: " + properties.getRawUri());
        try {
            String[] labels = new String[]{""};
            if (StringUtils.hasText(properties.getLabel())) {
                labels = StringUtils.commaDelimitedListToStringArray(properties.getLabel());
            }

            String state = ConfigClientStateHolder.getState();
            for (String label : labels) {
                Environment result = getRemoteEnvironment(localRestTemplate,
                        properties, label.trim(), state);
                if (result != null) {
                    LOGGER.info("refresh config {}", result);
                    if (result.getPropertySources() != null) {
                        for (PropertySource source : result.getPropertySources()) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> map = (Map<String, Object>) source
                                    .getSource();

                            List<ZuulRoute> zuulRoutes = map.entrySet().stream()
                                    .filter(t -> t.getKey().startsWith("zuul.routes"))
                                    .collect(groupingBy(t -> t.getKey().split("\\.")[2]))
                                    .values().stream().map(ConfigServicePropertySourceLocator::toRoute)
                                    .collect(toList());
                            Map<String, ZuulProperties.ZuulRoute> routeHashMap = new HashMap<>();
                            for (ZuulRoute zuulRoute : zuulRoutes) {
                                routeHashMap.put(zuulRoute.getPath().trim(), beanCopy(zuulRoute));
                                LOGGER.info("zuul route: {}", zuulRoute);
                            }
                            MemoryRouteLocator.setMap(routeHashMap);
                            DefaultListableBeanFactory beanFactory = ApplicationContextHelper.getSpringFactory();
                            if (beanFactory != null) {
                                try {
                                    ZuulProperties zuulProperties = beanFactory.getBean(ZuulProperties.class);
                                    Map<String, ZuulProperties.ZuulRoute> zuulMap = routeHashMap.values()
                                            .stream().collect(toMap(ZuulProperties.ZuulRoute::getId,
                                                    Function.identity()));
                                    zuulProperties.setRoutes(zuulMap);
                                } catch (Exception e) {
                                    LOGGER.warn("some error happened when refresh zuulProperties, cause: {}", e.toString());
                                }
                                try {
                                    HelperZuulRoutesProperties helperZuulRoutesProperties = beanFactory.getBean(HelperZuulRoutesProperties.class);
                                    Map<String, ZuulRoute> zuulRouteMap = new HashMap<>(zuulRoutes.size());
                                    for (ZuulRoute zuulRoute : zuulRoutes) {
                                        zuulRouteMap.put(zuulRoute.getId(), zuulRoute);
                                    }
                                    helperZuulRoutesProperties.setRoutes(zuulRouteMap);
                                } catch (BeansException e) {
                                    LOGGER.warn("some error happened when refresh helperZuulRoutesProperties, cause: {}", e.toString());
                                }
                                try {
                                    RouterOperator routerOperator = beanFactory.getBean(RouterOperator.class);
                                    routerOperator.refreshRoutes();
                                } catch (BeansException e) {
                                    LOGGER.info("some error happened when RouterOperator refreshRoutes, cause: {}", e.toString());
                                }
                            }
                            composite.addPropertySource(new MapPropertySource(source
                                    .getName(), map));
                        }
                    }
                    if (StringUtils.hasText(result.getState()) || StringUtils.hasText(result.getVersion())) {
                        HashMap<String, Object> map = new HashMap<>();
                        putValue(map, "config.client.state", result.getState());
                        putValue(map, "config.client.version", result.getVersion());
                        composite.addFirstPropertySource(new MapPropertySource("configClient", map));
                    }
                    return composite;
                }
            }
        } catch (HttpServerErrorException e) {
            error = e;
            if (MediaType.APPLICATION_JSON.includes(e.getResponseHeaders()
                    .getContentType())) {
                errorBody = e.getResponseBodyAsString();
            }
        } catch (Exception e) {
            error = e;
        }
        if (properties.isFailFast()) {
            throw new IllegalStateException(
                    "Could not locate PropertySource and the fail fast property is set, failing",
                    error);
        }
        LOGGER.warn("Could not locate PropertySource: "
                + (errorBody == null ? error == null ? "label not found" : error.getMessage() : errorBody));
        return null;

    }

    private void putValue(HashMap<String, Object> map, String key, String value) {
        if (StringUtils.hasText(value)) {
            map.put(key, value);
        }
    }

    private Environment getRemoteEnvironment(RestTemplate restTemplate, ConfigClientProperties properties,
                                             String label, String state) {
        String path = "/{name}/{profile}";
        String name = properties.getName();
        String profile = properties.getProfile();
        String token = properties.getToken();
        String uri = properties.getRawUri();

        Object[] args = new String[]{name, profile};
        if (StringUtils.hasText(label)) {
            args = new String[]{name, profile, label};
            path = path + "/{label}";
        }
        ResponseEntity<Environment> response = null;
        LOGGER.info("refresh config, request uri {}{}", uri, path);
        try {
            HttpHeaders headers = new HttpHeaders();
            if (StringUtils.hasText(token)) {
                headers.add(TOKEN_HEADER, token);
            }
            if (StringUtils.hasText(state)) {
                headers.add(STATE_HEADER, state);
            }
            final HttpEntity<Void> entity = new HttpEntity<>((Void) null, headers);
            response = restTemplate.exchange(uri + path, HttpMethod.GET,
                    entity, Environment.class, args);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() != HttpStatus.NOT_FOUND) {
                throw e;
            }
        }

        if (response == null || response.getStatusCode() != HttpStatus.OK) {
            return null;
        }
        return response.getBody();
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @SuppressWarnings("deprecation")
    private RestTemplate getSecureRestTemplate(ConfigClientProperties client) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setReadTimeout((60 * 1000 * 3) + 5000);
        RestTemplate template = new RestTemplate(requestFactory);
        String username = client.getUsername();
        String password = client.getPassword();
        String authorization = client.getAuthorization();
        Map<String, String> headers = new HashMap<>(client.getHeaders());

        if (password != null && authorization != null) {
            throw new IllegalStateException(
                    "You must set either 'password' or 'authorization'");
        }

        if (password != null) {
            byte[] token = Base64Utils.encode((username + ":" + password).getBytes());
            headers.put("Authorization", "Basic " + new String(token));
        } else if (authorization != null) {
            headers.put("Authorization", authorization);
        }

        if (!headers.isEmpty()) {
            template.setInterceptors(Arrays.<ClientHttpRequestInterceptor>asList(
                    new GenericRequestHeaderInterceptor(headers)));
        }

        return template;
    }

    public static class GenericRequestHeaderInterceptor
            implements ClientHttpRequestInterceptor {

        private final Map<String, String> headers;

        public GenericRequestHeaderInterceptor(Map<String, String> headers) {
            this.headers = headers;
        }

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                            ClientHttpRequestExecution execution) throws IOException {
            for (Entry<String, String> header : headers.entrySet()) {
                request.getHeaders().add(header.getKey(), header.getValue());
            }
            return execution.execute(request, body);
        }
    }

    private static ZuulRoute toRoute(List<Entry<String, Object>> list) {
        ZuulRoute zuulRoute = new ZuulRoute();
        for (Entry<String, Object> entry : list) {
            String[] str = entry.getKey().split("\\.");
            String key = str[3];
            if ("id".equals(key)) {
                zuulRoute.setId((String) entry.getValue());
            }
            if ("path".equals(key)) {
                zuulRoute.setPath((String) entry.getValue());
            }
            if ("serviceId".equals(key)) {
                zuulRoute.setServiceId((String) entry.getValue());
            }
            if ("url".equals(key)) {
                zuulRoute.setUrl((String) entry.getValue());
            }
            if ("stripPrefix".equals(key)) {
                zuulRoute.setStripPrefix(Boolean.parseBoolean(entry.getValue().toString()));
            }
            if ("retryable".equals(key)) {
                zuulRoute.setRetryable(Boolean.parseBoolean(entry.getValue().toString()));
            }
            if ("helperService".equals(key)) {
                zuulRoute.setHelperService((String) entry.getValue());
            }
            if ("customSensitiveHeaders".equals(key)) {
                zuulRoute.setCustomSensitiveHeaders(Boolean.parseBoolean(entry.getValue().toString()));
            }
            if ("sensitiveHeaders".equals(key)) {
                zuulRoute.setSensitiveHeadersJson((String) entry.getValue());
                String[] headers = ((String) entry.getValue()).split(",");
                Set<String> set = new HashSet<>();
                for (String header : headers) {
                    set.add(header.trim());
                }
                zuulRoute.setSensitiveHeaders(set);
            }
            if (StringUtils.isEmpty(zuulRoute.getId())) {
                zuulRoute.setId(str[2]);
            }
        }
        return zuulRoute;
    }

    private static ZuulProperties.ZuulRoute beanCopy(ZuulRoute route) {
        ZuulProperties.ZuulRoute zuulRoute = new ZuulProperties.ZuulRoute();
        org.springframework.beans.BeanUtils.copyProperties(route, zuulRoute);
        return zuulRoute;
    }

}
