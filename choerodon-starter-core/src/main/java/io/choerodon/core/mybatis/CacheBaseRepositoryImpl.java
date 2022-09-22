package io.choerodon.core.mybatis;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToIntFunction;

import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * date: 2020/4/14
 * description: 针对hzero的默认仓储实现简单包装，使hzero默认仓储方法支持缓存同步
 *
 * @author huifei.liu@hand-chian.com
 * @author Archibald
 */
public abstract class CacheBaseRepositoryImpl<T> extends BaseRepositoryImpl<T> implements CacheBaseRepository<T> {

    private final Logger logger = LoggerFactory.getLogger(CacheBaseRepositoryImpl.class);

    /**
     * 更新时是否需要跳过缓存更新
     *
     * @return 默认为false, 不会跳过
     */
    protected boolean skipUpdate() {
        return false;
    }

    /**
     * 缓存同步
     *
     * @param record     数据记录
     * @param operation  原始操作
     * @param asyncCache 缓存同步操作
     * @return 原始操作的处理结果
     */
    protected int asyncCache(T record, ToIntFunction<T> operation, Consumer<T> asyncCache) {
        // 原始操作
        int result = operation.applyAsInt(record);
        // 同步缓存
        asyncCache.accept(record);
        // 返回结果
        return result;
    }

    /**
     * 批量缓存同步
     *
     * @param records    数据记录
     * @param asyncCache 缓存同步操作
     */
    protected List<T> batchAsyncCache(List<T> records, Function<List<T>, List<T>> operation, Consumer<List<T>> asyncCache) {
        List<T> results = operation.apply(records);
        // 同步缓存
        asyncCache.accept(records);
        return results;
    }

    /**
     * 批量缓存删除
     *
     * @param records    数据记录
     * @param asyncCache 缓存同步操作
     * @return
     */
    protected Integer batchAsyncCacheDelete(List<T> records, Function<List<T>, Integer> operation, Consumer<List<T>> asyncCache) {
        Integer results = operation.apply(records);
        // 同步缓存
        asyncCache.accept(records);
        return results;
    }

    /**
     * 缓存同步
     *
     * @param key          数据记录的key
     * @param keyOperation 原始操作
     * @param asyncCache   缓存同步操作
     * @return 原始操作的处理结果
     */
    protected int asyncCacheWithKey(Object key, ToIntFunction<Object> keyOperation, Consumer<T> asyncCache) {
        T record = super.selectByPrimaryKey(key);
        return asyncCache(record, item -> keyOperation.applyAsInt(key), asyncCache);
    }

    /**
     * 从缓存中获取实例，若没有则从数据库中获取并缓存
     *
     * @param key              传入主键
     * @param cacheSelector    缓存数据获取函数
     * @param databaseSelector 数据库数据获取函数
     * @return INSTANCE
     */
    protected T selectFromCache(Object key, Function<Object, T> cacheSelector, Function<Object, T> databaseSelector) {
        T record = cacheSelector.apply(key);
        if (Objects.isNull(record)) {
            record = databaseSelector.apply(key);
            // 缓存没有则写入
            mergeCache(record);
            return record;
        }
        return record;
    }

    @Override
    public T selectByPrimaryKey(Object key) {
        return selectFromCache(key, this::getFromCacheByPrimaryKey, super::selectByPrimaryKey);
    }

    @Override
    public int updateByPrimaryKey(T record) {
        if (skipUpdate()) {
            return super.updateByPrimaryKey(record);
        }
        return asyncCache(record, super::updateByPrimaryKey, this::mergeCache);
    }

    @Override
    public int updateByPrimaryKeySelective(T record) {
        if (skipUpdate()) {
            return super.updateByPrimaryKeySelective(record);
        }
        return asyncCache(record, super::updateByPrimaryKeySelective, this::mergeCache);
    }

    @Override
    public List<T> batchUpdateByPrimaryKey(List<T> list) {
        if (skipUpdate()) {
            return super.batchUpdateByPrimaryKey(list);
        }
        return batchAsyncCache(list, super::batchUpdateByPrimaryKey, this::batchMergeCache);
    }

    @Override
    public List<T> batchUpdateByPrimaryKeySelective(List<T> list) {
        if (skipUpdate()) {
            return super.batchUpdateByPrimaryKeySelective(list);
        }
        return batchAsyncCache(list, super::batchUpdateByPrimaryKeySelective, this::batchMergeCache);
    }

    @Override
    public List<T> batchUpdateOptional(List<T> list, String... optionals) {
        if (skipUpdate()) {
            return super.batchUpdateOptional(list);
        }
        return batchAsyncCache(list, super::batchUpdateOptional, this::batchMergeCache);
    }

    @Override
    public int updateOptional(T record, String... optionals) {
        if (skipUpdate()) {
            return super.updateOptional(record, optionals);
        }
        return asyncCache(record, item -> super.updateOptional(item, optionals), this::mergeCache);
    }

    @Override
    public int delete(T record) {
        return asyncCache(record, super::delete, this::removeCache);
    }

    @Override
    public int deleteByPrimaryKey(Object key) {
        // Long 特殊支持下，保证大部分情况可以快速处理
        if (key instanceof Long) {
            return asyncCacheWithKey(key, super::deleteByPrimaryKey, this::removeCache);
        }
        try {
            // 其次，尝试强转原始对象
            return asyncCache((T) key, super::deleteByPrimaryKey, this::removeCache);
        } catch (ClassCastException e) {
            logger.error("error delete message:[{}]", e.getMessage());
            // 以上尝试都不行，采用通用方法
            return asyncCacheWithKey(key, super::deleteByPrimaryKey, this::removeCache);
        }
    }

    @Override
    public int batchDelete(List<T> list) {
        return batchAsyncCacheDelete(list, super::batchDelete, this::batchRemoveCache);
    }

    @Override
    public int batchDeleteByPrimaryKey(List<T> list) {
        return batchAsyncCacheDelete(list, super::batchDeleteByPrimaryKey, this::batchRemoveCache);
    }

    @Override
    public int insert(T record) {
        return asyncCache(record, super::insert, this::putCache);
    }

    @Override
    public int insertSelective(T record) {
        return asyncCache(record, super::insertSelective, this::putCache);
    }

    @Override
    public int insertOptional(T record, String... optionals) {
        return asyncCache(record, item -> super.insertOptional(item, optionals), this::putCache);
    }

    @Override
    public List<T> batchInsert(List<T> list) {
        return batchAsyncCache(list, super::batchInsert, this::batchPutCache);
    }

    @Override
    public List<T> batchInsertSelective(List<T> list) {
        return batchAsyncCache(list, super::batchInsertSelective, this::batchPutCache);
    }

    @Override
    public void initCache() {
    }

}
