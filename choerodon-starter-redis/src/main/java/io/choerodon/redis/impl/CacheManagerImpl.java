/*
 * #{copyright}#
 */
package io.choerodon.redis.impl;

import io.choerodon.redis.Cache;
import io.choerodon.redis.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;

/**
 * @author shengyang.zhou@hand-china.com
 */
@Component("cacheManager")
public class CacheManagerImpl implements CacheManager {
    private HashMap<String, Cache> cacheMap = new HashMap<>();

    @Autowired
    private List<Cache> caches;

    public void setCaches(List<Cache> caches) {
        this.caches = caches;
        if (caches != null) {
            for (Cache c : caches) {
                cacheMap.put(c.getName(), c);
            }
        }
    }

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

    @PostConstruct
    public void initCache() throws Exception {
        cacheMap.forEach((name, cache) -> {
            cache.init();
        });
        caches.forEach(cache -> {
            if (StringUtils.isEmpty(cache.getName())) {
                throw new RuntimeException(cache + " cacheName is empty");
            }
            cacheMap.put(cache.getName(), cache);
            cache.init();
        });
    }
}
