/*
 * #{copyright}#
 */
package io.choerodon.redis.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.redis.CacheGroup;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @param <T>
 *            元素类型
 * @author shengyang.zhou@hand-china.com
 */
public class HashStringRedisCacheGroup<T> extends RedisCache<T> implements CacheGroup<T> {

    private Logger logger = LoggerFactory.getLogger(HashStringRedisCacheGroup.class);

    private ObjectMapper objectMapper;

    private String[] groupField;

    private String valueField;

    public String[] getGroupField() {
        return groupField;
    }

    public void setGroupField(String[] groupField) {
        this.groupField = groupField;
    }

    private Map<String, HashStringRedisCache<T>> groupMap = new HashMap<>();

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String getGroupValue(Object bean) {
        String key = null;
        try {
            key = getKeyOfBean(bean, groupField);
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
            }
        }
        return key;
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public T getValue(String key) {
        return null;
    }

    @Override
    public void setValue(String key, T value) {
        String group = getGroupValue(value);
        HashStringRedisCache cache = getGroup(group);
        if(!StringUtils.isEmpty(getValueField())){
            try {
                Object ret = PropertyUtils.getProperty(value, valueField);
                cache.setValue(key,ret);
            } catch (Exception e){
                logger.error(e.getMessage(), e);
            }
        }else {
            cache.setValue(key, value);
        }
    }

    public List<T> getGroupAll(String group) {
        return getGroup(group).getAll();
    }

    @Override
    public void remove(String key) {
    }

    public HashStringRedisCache<T> getGroup(String group) {
        HashStringRedisCache hashStringRedisCache = groupMap.get(group);
        if (hashStringRedisCache == null) {
            hashStringRedisCache = new HashStringRedisCache();
            hashStringRedisCache.setRedisTemplate(getRedisTemplate());
            hashStringRedisCache.setObjectMapper(objectMapper);
            hashStringRedisCache.setName(getName() + ":" + group);
            hashStringRedisCache.setCategory(getCategory());
            hashStringRedisCache.setKeyField(getKeyField());
            hashStringRedisCache.setType(getType());
            if(!StringUtils.isEmpty(getValueField())){
                hashStringRedisCache.setValueField(getValueField());
            }
            hashStringRedisCache.init();
            groupMap.put(group, hashStringRedisCache);
        }
        return hashStringRedisCache;
    }

    @Override
    public T getValue(String group, String key) {
        return (T) getGroup(group).getValue(key);
    }

    @Override
    public void setValue(String group, String key, T value) {
        getGroup(group).setValue(key, value);
    }

    @Override
    public void remove(String group, String key) {
        getGroup(group).remove(key);
    }

    @Override
    public void removeGroup(String group) {
        getGroup(group).clear();
    }

    @Override
    protected void handleRow(Object row) {
        String group = getGroupValue(row);
        HashStringRedisCache<T> cache = getGroup(group);
        cache.handleRow(row);
    }

    @Override
    public void clear() {
        super.clear();
        groupMap.forEach((k, v) -> {
            v.clear();
        });
    }

    public String getValueField() {
        return valueField;
    }

    public void setValueField(String valueField) {
        this.valueField = valueField;
    }
}
