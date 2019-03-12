/*
 * #{copyright}#
 */
package io.choerodon.redis.impl;

import io.choerodon.redis.Cache;
import io.choerodon.redis.ICacheListener;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 采用单层 hash 结构存储数据.
 * <p>
 * 一个实例会在 redis 中多条记录,每条记录对应一个 hash 结构
 *
 * @param <T> T
 * @author shengyang.zhou@hand-china.com
 */
public class RedisCache<T> implements Cache<T>, BeanNameAware {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private SqlSessionFactory sqlSessionFactory;

    private RedisTemplate<String, String> redisTemplate;

    private ICacheListener listener;

    private String name;
    private Class<?> type;
    private String[] keyField;
    private String category = "hap:cache";

    /**
     * 是否正在加载数据:初次加载,或者 重载.
     * <p>
     * 如果正在重载过程中,则忽略一些步骤.
     */
    private boolean loading = false;

    /**
     * 缓存数据放在哪个 db 中.
     */
    private int db;
    /**
     * 是否在启动时加载全部数据.
     */
    private boolean loadOnStartUp = false;
    /**
     * 加载数据用的 sql 的 id.
     * <p>
     * 这是一个完整的名称:FunctionMapper.queryAll
     */
    private String sqlId;

    protected RedisSerializer<String> strSerializer;

    @Override
    public void init() {
        if (loadOnStartUp) {
            loading = true;
            try {
                initLoad();
            } finally {
                loading = false;
            }
        }
        if (listener != null) {
            listener.cacheInit(this);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public boolean isLoadOnStartUp() {
        return loadOnStartUp;
    }

    public void setLoadOnStartUp(boolean loadOnStartUp) {
        this.loadOnStartUp = loadOnStartUp;
    }

    public String getSqlId() {
        return sqlId;
    }

    public void setSqlId(String sqlId) {
        this.sqlId = sqlId;
    }

    public String[] getKeyField() {
        return keyField;
    }

    /**
     * 指定查询结果中 哪一的字段作为 cache 的 key.
     * <p>
     * 多个字段时,用','分割
     *
     * @param keyField bean 的属性名
     */
    public void setKeyField(String[] keyField) {
        this.keyField = keyField;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public RedisTemplate<String, String> getRedisTemplate() {
        return redisTemplate;
    }

    @Autowired
    public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.strSerializer = redisTemplate.getStringSerializer();
    }

    public SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }

    @Autowired
    @Qualifier("sqlSessionFactory")
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getValue(String key) {
        return redisTemplate.execute((RedisCallback<T>) (connection) -> {
            byte[] keyBytes = strSerializer.serialize(getFullKey(key));
            Map<byte[], byte[]> value = connection.hGetAll(keyBytes);
            if (value.size() == 0) {
                return (T) null;
            }
            try {
                Object bean = type.newInstance();
                for (Map.Entry<byte[], byte[]> entry : value.entrySet()) {
                    String pName = strSerializer.deserialize(entry.getKey());
                    String pValue = strSerializer.deserialize(entry.getValue());
                    if (bean instanceof Map) {
                        ((Map) bean).put(pName, pValue);
                        continue;
                    }
                    PropertyDescriptor pd = PropertyUtils.getPropertyDescriptor(bean, pName);
                    if (pd == null) {
                        continue;
                    }
                    Class<?> pType = pd.getPropertyType();
                    if (pType == Date.class) {
                        Long time = pValue.length() == 0 ? null : Long.parseLong(pValue);
                        BeanUtils.setProperty(bean, pName, time);
                    } else {
                        BeanUtils.setProperty(bean, pName, pValue);
                    }
                }
                return (T) bean;
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            return (T) null;
        });
    }

    @Override
    public void setValue(String key, T value) {
        try {
            Map<String, Object> map = convertToMap(value);
            setValue(key, map);
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("setValue error ", e);
            }
        }
    }

    @Override
    public void remove(String key) {
        redisTemplate.execute((RedisCallback<T>) (connection) -> {
            byte[] keyBytes = strSerializer.serialize(getFullKey(key));
            connection.del(keyBytes);
            return (T) null;
        });
    }

    @Override
    public String getCacheKey(T value) {
        try {
            return getKeyOfBean(value, keyField);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void reload() {
        loading = true;
        try {
            clear();
            initLoad();
        } finally {
            loading = false;
        }
    }

    protected boolean isLoading() {
        return loading;
    }

    private void setValue(String key, Map<String, Object> value) {
        byte[] keyBytes = strSerializer.serialize(getFullKey(key));
        Map<byte[], byte[]> data = new HashMap<>();
        value.forEach((k, v) -> {
            // 排除特殊字段
            if (k.charAt(0) == '_') {
                return;
            }
            if (v instanceof Date) {
                v = ((Date) v).getTime();
            }
            if (v != null) {
                data.put(strSerializer.serialize(k), strSerializer.serialize(v.toString()));
            }
        });

        redisTemplate.execute((RedisCallback<Object>) (connection) -> {
            connection.hMSet(keyBytes, data);
            return null;
        });
    }

    /**
     * this method will called when cache first init, and reload cache.
     * <p>
     * pls make sure NOT cause side effect when reload
     */
    protected void initLoad() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            sqlSession.select(sqlId, (resultContext) -> {
                Object row = resultContext.getResultObject();
                handleRow(row);
            });
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * when loadOnStartUp is true,every row in resultset will be passed to this.
     * method
     */
    protected void handleRow(Object row) {
        try {
            Map<String, Object> rowMap = convertToMap(row);
            String key = getKeyOfBean(row, keyField);
            setValue(key, rowMap);
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 从 bean 中取出指定的 field 值,拼接.
     *
     * @param bean       bean
     * @param properties 属性
     * @return 拼接后的
     * @throws IllegalAccessException    {@link BeanUtils#getProperty(Object, String)}
     * @throws NoSuchMethodException     {@link BeanUtils#getProperty(Object, String)}
     * @throws InvocationTargetException {@link BeanUtils#getProperty(Object, String)}
     */
    public static String getKeyOfBean(Object bean, String[] properties)
            throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        String key = BeanUtils.getProperty(bean, properties[0]);
        if (properties.length > 1) {
            StringBuilder sb = new StringBuilder(key);
            for (int i = 1; i < properties.length; ++i) {
                sb.append('.').append(BeanUtils.getProperty(bean, properties[i]));
            }
            key = sb.toString();
        }
        return key;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> convertToMap(Object obj)
            throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if (obj instanceof Map) {
            return (Map<String, Object>) obj;
        }
        Map<String, Object> map = PropertyUtils.describe(obj);
        map.remove("class"); // describe会包含 class 属性,此处无用
        return map;
    }

    protected String getFullKey(String key) {
        return new StringBuilder(getCategory()).append(":").append(getName()).append(":").append(key).toString();
    }

    @Override
    public void clear() {

    }

    public void setListener(ICacheListener listener) {
        this.listener = listener;
    }

    @Override
    public void setBeanName(String name) {
        if (getName() == null) {
            setName(name);
        }
    }

    protected void onCacheReload() {
        if (logger.isDebugEnabled()) {
            logger.debug("cache reloaded:" + getName());
        }
    }
}
