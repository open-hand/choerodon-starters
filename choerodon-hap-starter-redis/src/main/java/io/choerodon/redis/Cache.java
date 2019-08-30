/*
 * #{copyright}#
 */

package io.choerodon.redis;

/**
 * @param <T>
 *            值类型
 * @author shengyang.zhou@hand-china.com
 */
public interface Cache<T> {

    /**
     * cacheManager 启动时调用.
     */
    void init();

    /**
     * @return name of cache
     */
    String getName();

    /**
     * 设置 cache name.
     * <p>
     * cacheManager 通过 cache 的 name 来区分每一个 cache
     * 
     * @param name
     *            name of cache
     */
    void setName(String name);

    /**
     * 取值.
     * 
     * @param key
     *            key
     * @return value ,may be null
     */
    T getValue(String key);

    /**
     * 放值.
     * 
     * @param key
     *            key
     * @param value
     *            value
     */
    void setValue(String key, T value);

    /**
     * 按照 key, 删除指定值.
     * 
     * @param key
     *            cacheKey
     */
    void remove(String key);

    /**
     * 根据对象,自动计算 cache Key.
     * 
     * @param value
     *            缓存对象
     * @return cacheKey
     */
    String getCacheKey(T value);

    /**
     * 重载 cache.
     */
    void reload();

    /**
     * 清空 cache.
     */
    void clear();
}
