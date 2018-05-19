/*
 * Copyright 2015 the original author or authors.
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

package org.springframework.cloud.config.monitor;

import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.bus.event.RefreshRemoteApplicationEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * HTTP endpoint for webhooks coming from repository providers.
 *
 * @author Dave Syer
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "${spring.cloud.config.monitor.endpoint.path:}/monitor")
@CommonsLog
public class PropertyPathEndpoint
        implements ApplicationEventPublisherAware, ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyPathEndpoint.class);

    private static final String KEY_INSTANCE = "instanceId";

    private static final String KEY_CONFIG_VERSION = "configVersion";

    private static final String APPLICATION = "application";

    private final PropertyPathNotificationExtractor extractor;
    private ApplicationEventPublisher applicationEventPublisher;

    private String contextId = UUID.randomUUID().toString();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.contextId = applicationContext.getId();
    }

    @Override
    public void setApplicationEventPublisher(
            ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @RequestMapping(method = RequestMethod.POST)
    public Set<String> notifyByPath(@RequestHeader HttpHeaders headers,
                                    @RequestBody Map<String, Object> request) {
        PropertyPathNotification notification = this.extractor.extract(headers, request);
        if (notification != null) {
            String destinationInstance = null;
            Object instance = request.get(KEY_INSTANCE);
            if (instance != null && instance instanceof String) {
                destinationInstance = (String) instance;
            }
            String configVersion = null;
            Object config = request.get(KEY_CONFIG_VERSION);
            if (config != null && config instanceof String) {
                configVersion = (String) config;
            }
            LOGGER.info("spring.cloud.config.monitor get refresh config request {}", request);
            Set<String> services = new LinkedHashSet<>();

            for (String path : notification.getPaths()) {
                services.addAll(guessServiceName(path));
            }
            if (this.applicationEventPublisher != null) {
                for (String service : services) {
                    log.info("Refresh for: " + service);
                    this.applicationEventPublisher
                            .publishEvent(new RefreshRemoteApplicationEvent(this,
                                    this.contextId, service, destinationInstance, configVersion));
                }
                return services;
            }

        }
        return Collections.emptySet();
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Set<String> notifyByForm(@RequestHeader HttpHeaders headers,
                                    @RequestParam("path") List<String> request) {
        Map<String, Object> map = new HashMap<>();
        String key = "path";
        map.put(key, request);
        return notifyByPath(headers, map);
    }

    private Set<String> guessServiceName(String path) {
        Set<String> services = new LinkedHashSet<>();
        if (path != null) {
            String stem = StringUtils
                    .stripFilenameExtension(StringUtils.getFilename(StringUtils.cleanPath(path)));
            // TODO: correlate with service registry
            int index = stem.indexOf('-');
            while (index >= 0) {
                String name = stem.substring(0, index);
                String profile = stem.substring(index + 1);
                if (APPLICATION.equals(name)) {
                    services.add("*:" + profile);
                } else if (!name.startsWith(APPLICATION)) {
                    services.add(name + ":" + profile);
                }
                index = stem.indexOf('-', index + 1);
            }
            String name = stem;
            if (APPLICATION.equals(name)) {
                services.add("*");
            } else if (!name.startsWith(APPLICATION)) {
                services.add(name);
            }
        }
        return services;
    }

}
