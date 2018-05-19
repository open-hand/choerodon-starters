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

package org.springframework.cloud.bus.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.springframework.context.ApplicationEvent;
import org.springframework.util.StringUtils;

import java.util.UUID;

/**
 * @author Spencer Gibb
 */
@SuppressWarnings("serial")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonIgnoreProperties("source")
public abstract class RemoteApplicationEvent extends ApplicationEvent {
    private static final Object TRANSIENT_SOURCE = new Object();
    private final String originService;
    private final String id;
    private final String destinationService;
    private final String destinationInstance;
    private final String configVersion;

    protected RemoteApplicationEvent() {
        // for serialization libs like jackson
        this(TRANSIENT_SOURCE, null, null, null, null);
    }

    protected RemoteApplicationEvent(Object source, String originService,
                                     String destinationService, String destinationInstance, String configVersion) {
        super(source);
        this.originService = originService;
        if (destinationService == null) {
            destinationService = "**";
        }
        if (!"**".equals(destinationService) && StringUtils.countOccurrencesOf(destinationService, ":") <= 1
                && !StringUtils.endsWithIgnoreCase(destinationService, ":**")) {
            destinationService = destinationService + ":**";
        }
        this.destinationService = destinationService;
        this.destinationInstance = destinationInstance;
        this.configVersion = configVersion;
        this.id = UUID.randomUUID().toString();
    }

    protected RemoteApplicationEvent(Object source, String originService) {
        this(source, originService, null, null, null);
    }

    public String getOriginService() {
        return originService;
    }

    public String getDestinationService() {
        return destinationService;
    }

    public String getId() {
        return id;
    }

    public String getDestinationInstance() {
        return destinationInstance;
    }

    public String getConfigVersion() {
        return configVersion;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((destinationService == null) ? 0 : destinationService.hashCode());
        result = prime * result
                + ((destinationInstance == null) ? 0 : destinationInstance.hashCode());
        result = prime * result
                + ((configVersion == null) ? 0 : configVersion.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result
                + ((originService == null) ? 0 : originService.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RemoteApplicationEvent other = (RemoteApplicationEvent) obj;
        if (destinationService == null) {
            if (other.destinationService != null)
                return false;
        } else if (!destinationService.equals(other.destinationService))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (originService == null) {
            if (other.originService != null)
                return false;
        } else if (!originService.equals(other.originService))
            return false;
        if (destinationInstance == null) {
            if (other.destinationInstance != null)
                return false;
        } else if (!destinationInstance.equals(other.destinationInstance))
            return false;
        if (configVersion == null) {
            if (other.configVersion != null)
                return false;
        } else if (!configVersion.equals(other.configVersion))
            return false;
        return true;
    }
}
