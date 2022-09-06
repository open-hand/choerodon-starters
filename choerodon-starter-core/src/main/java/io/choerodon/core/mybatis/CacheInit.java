package io.choerodon.core.mybatis;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 缓存预热方法
 * <br/>
 * 调用通用仓储缓存进行缓存初始化
 *
 * @see CacheBaseRepository#initCache()
 * @see ApplicationRunner#run(ApplicationArguments)
 */
@Component
@ConditionalOnProperty(value = "choerodon.cache.init", havingValue = "true", matchIfMissing = true)
public class CacheInit implements ApplicationRunner {

    private final Logger logger = LoggerFactory.getLogger(CacheInit.class);

    private final List<CacheBaseRepository<?>> cacheBaseRepositories;

    public CacheInit(@Autowired(required = false) List<CacheBaseRepository<?>> cacheBaseRepositories) {
        this.cacheBaseRepositories = cacheBaseRepositories;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (CollectionUtils.isEmpty(cacheBaseRepositories)) {
            return;
        }
        try {
            cacheBaseRepositories.forEach(CacheBaseRepository::initCache);
        } catch (Exception e) {
            logger.error("初始化全局仓储缓存失败", e);
        }
    }
}
