/*
 * Copyright 2013-2017 the original author or authors.
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
 *
 */

package org.springframework.cloud.bus;

import org.springframework.cloud.bus.event.RemoteApplicationEvent;
import org.springframework.cloud.netflix.eureka.CloudEurekaInstanceConfig;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaRegistration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * @author Spencer Gibb
 */
public class ServiceMatcher implements ApplicationContextAware {

    private PathMatcher matcher;

    private Optional<EurekaRegistration> eurekaRegistration;

    private ApplicationContext context;


    public ServiceMatcher(Optional<EurekaRegistration> eurekaRegistration) {
        this.eurekaRegistration = eurekaRegistration;
    }

    public void setMatcher(PathMatcher matcher) {
        this.matcher = matcher;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.context = applicationContext;
    }

    public boolean isFromSelf(RemoteApplicationEvent event) {
        String originService = event.getOriginService();
        String serviceId = getServiceId();
        return this.matcher.match(originService, serviceId);
    }

    public boolean isForSelf(RemoteApplicationEvent event) {
        String destinationService = event.getDestinationService();
        String destinationInstanceId = event.getDestinationInstance();
        boolean serviceMatch = (destinationService == null || destinationService.trim().isEmpty() || this.matcher
                .match(destinationService, getServiceId()));
        if (!serviceMatch) {
            return false;
        }
        Boolean instanceMatch = true;
        if (eurekaRegistration.isPresent()) {
            CloudEurekaInstanceConfig cloudEurekaInstanceConfig = eurekaRegistration.get().getInstanceConfig();
            if (cloudEurekaInstanceConfig instanceof EurekaInstanceConfigBean) {
                EurekaInstanceConfigBean eurekaInstanceConfigBean = (EurekaInstanceConfigBean) cloudEurekaInstanceConfig;
                instanceMatch = (StringUtils.isEmpty(destinationInstanceId)
                        || destinationInstanceId.equals(eurekaInstanceConfigBean.getInstanceId()));
            }
        }
        return instanceMatch;
    }

    public String getServiceId() {
        return context.getId();
    }

}
