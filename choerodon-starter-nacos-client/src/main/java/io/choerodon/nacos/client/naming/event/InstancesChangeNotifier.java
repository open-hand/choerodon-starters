/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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

package io.choerodon.nacos.client.naming.event;

import io.choerodon.nacos.api.naming.listener.AbstractEventListener;
import io.choerodon.nacos.api.naming.listener.EventListener;
import io.choerodon.nacos.api.naming.listener.NamingEvent;
import io.choerodon.nacos.api.naming.pojo.ServiceInfo;
import io.choerodon.nacos.common.notify.Event;
import io.choerodon.nacos.common.notify.listener.Subscriber;
import io.choerodon.nacos.common.utils.CollectionUtils;
import io.choerodon.nacos.common.utils.ConcurrentHashSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A subscriber to notify eventListener callback.
 *
 * @author horizonzy
 * @since 1.4.1
 */
public class InstancesChangeNotifier extends Subscriber<InstancesChangeEvent> {
    
    private final Map<String, ConcurrentHashSet<EventListener>> listenerMap = new ConcurrentHashMap<String, ConcurrentHashSet<EventListener>>();
    
    private final Object lock = new Object();
    
    /**
     * register listener.
     *
     * @param serviceName combineServiceName, such as 'xxx@@xxx'
     * @param clusters    clusters, concat by ','. such as 'xxx,yyy'
     * @param listener    custom listener
     */
    public void registerListener(String serviceName, String clusters, EventListener listener) {
        String key = ServiceInfo.getKey(serviceName, clusters);
        ConcurrentHashSet<EventListener> eventListeners = listenerMap.get(key);
        if (eventListeners == null) {
            synchronized (lock) {
                eventListeners = listenerMap.get(key);
                if (eventListeners == null) {
                    eventListeners = new ConcurrentHashSet<EventListener>();
                    listenerMap.put(key, eventListeners);
                }
            }
        }
        eventListeners.add(listener);
    }
    
    /**
     * deregister listener.
     *
     * @param serviceName combineServiceName, such as 'xxx@@xxx'
     * @param clusters    clusters, concat by ','. such as 'xxx,yyy'
     * @param listener    custom listener
     */
    public void deregisterListener(String serviceName, String clusters, EventListener listener) {
        String key = ServiceInfo.getKey(serviceName, clusters);
        ConcurrentHashSet<EventListener> eventListeners = listenerMap.get(key);
        if (eventListeners == null) {
            return;
        }
        eventListeners.remove(listener);
        if (CollectionUtils.isEmpty(eventListeners)) {
            listenerMap.remove(key);
        }
    }
    
    /**
     * check serviceName,clusters is subscribed.
     *
     * @param serviceName combineServiceName, such as 'xxx@@xxx'
     * @param clusters    clusters, concat by ','. such as 'xxx,yyy'
     * @return is serviceName,clusters subscribed
     */
    public boolean isSubscribed(String serviceName, String clusters) {
        String key = ServiceInfo.getKey(serviceName, clusters);
        ConcurrentHashSet<EventListener> eventListeners = listenerMap.get(key);
        return CollectionUtils.isNotEmpty(eventListeners);
    }
    
    public List<ServiceInfo> getSubscribeServices() {
        List<ServiceInfo> serviceInfos = new ArrayList<ServiceInfo>();
        for (String key : listenerMap.keySet()) {
            serviceInfos.add(ServiceInfo.fromKey(key));
        }
        return serviceInfos;
    }
    
    @Override
    public void onEvent(InstancesChangeEvent event) {
        String key = ServiceInfo.getKey(event.getServiceName(), event.getClusters());
        ConcurrentHashSet<EventListener> eventListeners = listenerMap.get(key);
        if (CollectionUtils.isEmpty(eventListeners)) {
            return;
        }
        for (final EventListener listener : eventListeners) {
            final io.choerodon.nacos.api.naming.listener.Event namingEvent = transferToNamingEvent(event);
            if (listener instanceof AbstractEventListener && ((AbstractEventListener) listener).getExecutor() != null) {
                ((AbstractEventListener) listener).getExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        listener.onEvent(namingEvent);
                    }
                });
                continue;
            }
            listener.onEvent(namingEvent);
        }
    }
    
    private io.choerodon.nacos.api.naming.listener.Event transferToNamingEvent(
            InstancesChangeEvent instancesChangeEvent) {
        return new NamingEvent(instancesChangeEvent.getServiceName(), instancesChangeEvent.getGroupName(),
                instancesChangeEvent.getClusters(), instancesChangeEvent.getHosts());
    }
    
    @Override
    public Class<? extends Event> subscribeType() {
        return InstancesChangeEvent.class;
    }
    
}
