package io.choerodon.core.mybatis;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.mybatis.base.BaseRepository;
import org.springframework.boot.ApplicationArguments;

/**
 * date: 2020/4/14
 * description: 支持被动刷新缓存仓储，基础仓储能力继承自接口{@link BaseRepository}
 * <br/>
 * 需要自行实现方法
 * <li>
 *     <ul>{@link CacheBaseRepository#putCache(Object)} 插入缓存</ul>
 *     <ul>{@link CacheBaseRepository#removeCache(Object)} 删除缓存</ul>
 *     <ul>{@link CacheBaseRepository#initCache()} 初始化缓存</ul>
 * </li>
 * <br/>
 *
 * @author huifei.liu@hand-chian.com
 * @author archibald
 * @see CacheBaseRepositoryImpl 默认实现参考此类
 */
public interface CacheBaseRepository<T> extends BaseRepository<T> {

    /**
     * 同步缓存，用于新建时创建缓存
     *
     * @param record 数据对象, 具体传入对象取决于原始增删改参数
     */
    void putCache(T record);

    /**
     * 批量同步缓存，用于新建时创建缓存
     *
     * @param records 数据对象列表, 具体传入对象取决于原始增删改参数
     * @see CacheBaseRepository#putCache(Object)
     */
    default void batchPutCache(List<T> records) {
        if (CollectionUtils.isEmpty(records)) {
            return;
        }
        for (T record : records) {
            putCache(record);
        }
    }

    /**
     * 更新缓存，默认使用putCache,实现类可覆盖
     *
     * @param record 数据对象, 具体传入对象取决于原始增删改参数
     * @see CacheBaseRepository#putCache(Object)
     */
    default void mergeCache(T record) {
        this.putCache(record);
    }

    /**
     * 批量更新缓存，默认使用putCache,实现类可覆盖
     *
     * @param records 数据对象列表, 具体传入对象取决于原始增删改参数
     * @see CacheBaseRepository#putCache(Object)
     * @see CacheBaseRepository#mergeCache(Object)
     */
    default void batchMergeCache(List<T> records) {
        if (CollectionUtils.isEmpty(records)) {
            return;
        }
        for (T record : records) {
            mergeCache(record);
        }
    }

    /**
     * 批量删除缓存
     *
     * @param records 数据对象列表, 具体传入对象取决于原始增删改参数
     * @see CacheBaseRepository#removeCache(Object)
     */
    default void batchRemoveCache(List<T> records) {
        if (CollectionUtils.isEmpty(records)) {
            return;
        }
        for (T record : records) {
            removeCache(record);
        }
    }

    /**
     * 移除缓存, 用于删除场景
     *
     * @param record 数据对象, 具体传入对象取决于原始增删改参数
     */
    void removeCache(T record);

    /**
     * 初始化缓存，在项目启动时初始化
     *
     * @see CacheInit#run(ApplicationArguments)
     */
    void initCache();
}
