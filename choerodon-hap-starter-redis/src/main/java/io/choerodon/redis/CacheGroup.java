/*
 * #{copyright}#
 */
package io.choerodon.redis;

/**
 * @param <T>
 *            元素类型
 * @author shengyang.zhou@hand-china.com
 */
public interface CacheGroup<T> {
    /**
     * 通过 group name 获取具体的 cache.
     * 
     * @param groupName
     *            组name
     * @return 组对应的 具体的cache
     */
    Cache<T> getGroup(String groupName);

    /**
     * 对组操作getValue.
     * 
     * @param group
     *            组 name
     * @param key
     *            key
     * @return value
     */
    default T getValue(String group, String key) {
        return getGroup(group).getValue(key);
    }

    /**
     * 对组操作 setValue.
     * 
     * @param group
     *            组 name
     * @param key
     *            key
     * @param value
     *            value
     */
    default void setValue(String group, String key, T value) {
        getGroup(group).setValue(key, value);
    }

    /**
     * 对组操作 remove.
     * 
     * @param group
     *            组 name
     * @param key
     *            key
     */
    default void remove(String group, String key) {
        getGroup(group).remove(key);
    }

    /**
     * 删除组.
     * 
     * @param group
     *            组 name
     */
    void removeGroup(String group);
}
