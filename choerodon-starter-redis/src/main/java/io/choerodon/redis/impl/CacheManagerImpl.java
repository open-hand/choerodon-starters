/*
 * #{copyright}#
 */
package io.choerodon.redis.impl;

import io.choerodon.redis.Cache;
import io.choerodon.redis.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author shengyang.zhou@hand-china.com
 */
@Component("cacheManager")
public class CacheManagerImpl implements CacheManager, ApplicationListener {

    private HashMap<String, Cache> cacheMap = new HashMap<>();
    private List<Cache> caches = new ArrayList<>();

    @Override
    public List<Cache> getCaches() {
        return caches;
    }

    @Override
    public <T> Cache<T> getCache(String name) {
        return cacheMap.get(name);
    }

    @Override
    public void addCache(Cache<?> cache) {
        if (!caches.contains(cache)) {
            caches.add(cache);
        }
        cacheMap.put(cache.getName(), cache);
    }

    public void contextInitialized(ApplicationContext applicationContext) {
        cacheMap.forEach((name, cache) -> {
            cache.init();
        });
        Map<String, Cache> cacheBeans = applicationContext.getBeansOfType(Cache.class);
        if (cacheBeans != null) {
            cacheBeans.forEach((k, v) -> {
                if (!caches.contains(v)) {
                    if (StringUtils.isEmpty(v.getName())) {
                        throw new RuntimeException(v + " cacheName is empty");
                    }
                    addCache(v);
                    v.init();
                }
            });
        }
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            ApplicationContext applicationContext = ((ContextRefreshedEvent) event).getApplicationContext();
            contextInitialized(applicationContext);
        }
    }
}
