package io.choerodon.redis.impl;

import io.choerodon.mybatis.common.query.JoinCache;
import io.choerodon.redis.Cache;
import io.choerodon.redis.CacheManager;
import io.choerodon.redis.CacheResolve;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * @author jialong.zuo@hand-china.com on 2017/6/1.
 */
@Component(value = "hashStringRedisCacheGroupResolve")
public class HashStringRedisCacheGroupResolve<T> extends CacheResolve {

    @Autowired
    private CacheManager cacheManager;

    @Override
    public Object resolve(Object cacheEntity, Object resultMap, String lang) throws NoSuchFieldException, IllegalAccessException {
        Object joinKey = getJoinKey(cacheEntity, resultMap);
        if (joinKey == null) {
            return joinKey;
        }
        JoinCache joinCache = (JoinCache) cacheEntity;
        Cache cache = cacheManager.getCache(joinCache.cacheName());
        HashStringRedisCacheGroup hashStringRedisCacheGroup = (HashStringRedisCacheGroup) cache;
        HashStringRedisCache hashStringRedisCache = hashStringRedisCacheGroup.getGroup(lang);
        Object result = hashStringRedisCache.getValue(joinKey.toString());
        if (result == null) {
            throw new RuntimeException("查询关联属性" + joinCache.joinColumn() + "失败！   关联字段: " + joinCache.joinKey() + "  对应值: " + joinKey);
        }
        Field field = result.getClass().getDeclaredField(joinCache.joinColumn());
        field.setAccessible(true);
        return field.get(result);
    }

}
