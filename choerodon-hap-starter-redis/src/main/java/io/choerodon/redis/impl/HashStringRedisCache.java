/*
 * #{copyright}#
 */
package io.choerodon.redis.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 将一整个表的数据,存放到一个 hash 结构中.
 * <p>
 * 每个实例,在 redis 中仅有一条记录:一个 hash 结构:
 * 
 * <pre>
 *     hap:cache:[cache name] --&gt;   {
 *                                   [key] : [value of string type]
 *                                  }
 * </pre>
 * 
 * hash 结构中的 key 值,有 keyField 属性决定.<br>
 * hash 结构的 value 值,由 valueField 属性决定,如果没有指定,则用整条记录作为值.<br>
 * 如果指定了 valueField 属性, 则 type 的类型必须符合 valueField 字段的类型.
 * <p>
 * 简单类型,使用 String.valueOf 和 相应类型的 String 构造器 完成 value 和 String 的转换.<br>
 * 复杂类型,使用 ObjectMapper.
 * <p>
 * 
 * @param <T>
 *            元素类型,如果指定了 valueField,则必须与其类型相符
 * @author shengyang.zhou@hand-china.com
 */
public class HashStringRedisCache<T> extends RedisCache<T> {

    private String valueField;

    private ObjectMapper objectMapper;

    private boolean isBasic = false;
    private Constructor stringConstructor;

    private Logger logger = LoggerFactory.getLogger(HashStringRedisCache.class);

    private String fullKey;
    private String topic;

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    public void setType(Class<?> type) {
        super.setType(type);
        if (String.class == type || Boolean.class == type || Number.class.isAssignableFrom(type)) {
            isBasic = true;
        }
        if (isBasic) {
            try {
                stringConstructor = type.getConstructor(String.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void setName(String name) {
        super.setName(name);
        this.topic = "cache." + name;
    }

    @Override
    public T getValue(String key) {
        return getRedisTemplate().execute((RedisCallback<T>) (connection) -> {
            return hMGet(connection, getFullKey(key), key);
        });

    }

    @Override
    public void   setValue(String key, T value) {
        if (value == null) {
            remove(key);
            return;
        }
        getRedisTemplate().execute((RedisCallback<T>) (connection) -> {
            hMSet(connection, getFullKey(key), key, value);
            return null;
        });
        if (!isLoading()) {
            getRedisTemplate().convertAndSend(topic, key);
        }
    }

    protected String objectToString(Object value) {
        if (isBasic) {
            return String.valueOf(value);
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            if (logger.isWarnEnabled()) {
                logger.warn("invalid json: " + value);
            }
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    protected T stringToObject(String value) {
        if (isBasic) {
            try {
                return (T) stringConstructor.newInstance(value);
            } catch (Exception e) {
                if (logger.isWarnEnabled()) {
                    logger.warn("invalid value: " + value);
                }
                throw new RuntimeException(e);
            }
        }
        try {
            return objectMapper.readValue(value, (Class<T>) getType());
        } catch (Exception e) {
            if (logger.isWarnEnabled()) {
                logger.warn("invalid value: " + value);
            }
            throw new RuntimeException(e);
        }
    }

    protected void hMSet(RedisConnection connection, String mapKey, String pName, Object pValue) {
        String string = objectToString(pValue);
        Map<byte[], byte[]> v = new HashMap<>();
        v.put(strSerializer.serialize(pName), strSerializer.serialize(string));
        connection.hMSet(strSerializer.serialize(mapKey), v);
    }

    @SuppressWarnings("unchecked")
    protected <E> E hMGet(RedisConnection connection, String mapKey, String pName) {
        byte[] mapKeyBytes = strSerializer.serialize(mapKey);
        List<byte[]> result = connection.hMGet(mapKeyBytes, strSerializer.serialize(pName));
        if (result.isEmpty() || result.get(0) == null) {
            return null;
        }
        String string = strSerializer.deserialize(result.get(0));
        Object obj = stringToObject(string);
        return (E) obj;
    }

    @SuppressWarnings("unchecked")
    protected <E> List<E> hVals(RedisConnection connection, String mapKey) {
        byte[] mapKeyBytes = strSerializer.serialize(mapKey);
        List<byte[]> result = connection.hVals(mapKeyBytes);
        List list = new ArrayList();
        for (byte[] bs : result) {
            String string = strSerializer.deserialize(bs);
            Object obj = stringToObject(string);
            list.add(obj);
        }
        return list;
    }

    /**
     * 取出所有数据.
     *
     * @return map 中所有条目,反序列化后放在一个 list
     */
    public List<T> getAll() {
        return getRedisTemplate().execute((RedisCallback<List<T>>) (connection) -> {
            return hVals(connection, getFullKey(null));
        });
    }

    @Override
    public void remove(String key) {
        getRedisTemplate().execute((RedisCallback<String>) (connection) -> {
            byte[] mapKeyBytes = strSerializer.serialize(getFullKey(key));
            byte[] valueKeyBytes = strSerializer.serialize(key);
            connection.hDel(mapKeyBytes, valueKeyBytes);
            return null;
        });
        if (!isLoading()) {
            getRedisTemplate().convertAndSend(topic, key);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void handleRow(Object row) {
        try {
            String[] keyField = getKeyField();
            String key = getKeyOfBean(row, keyField);
            if (valueField != null) {
                Object v = BeanUtils.getProperty(row, valueField);
                setValue(key, (T) v);
            } else {
                setValue(key, (T) row);
            }
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    protected String getFullKey(String key) {
        if (fullKey == null) {
            fullKey = getCategory() + ":" + getName();
        }
        return fullKey;
    }

    @Override
    public void clear() {
        getRedisTemplate().execute((RedisCallback) (connection) -> {
            connection.del(strSerializer.serialize(getFullKey(null)));
            return null;
        });
    }

    public String getValueField() {
        return valueField;
    }

    public void setValueField(String valueField) {
        this.valueField = valueField;
    }
}
