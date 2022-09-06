package io.choerodon.core.common;

import io.choerodon.core.convertor.ApplicationContextHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Copyright (c) 2022. Hand Enterprise Solution Company. All right reserved.
 *
 * @author zongqi.hao@zknow.com
 * @since 2022/7/22
 */
public final class ChoerodonService {

    private static Environment environment;

    public ChoerodonService() {
    }

    public static String getRealName(String serviceName) {
        return environment.resolvePlaceholders(serviceName);
    }

    static {
        ApplicationContextHelper.asyncStaticSetter(Environment.class, ChoerodonService.class, "environment");
    }

    @Component
    public static class AgileService {
        public static final String NAME = "${choerodon.service.agile.name:agile-service}";
        public static final String CODE = "agile";
        public static final String PATH = "/agile/**";
        public static Integer PORT = 8378;
        public static Integer REDIS_DB = 12;
        public static String BUCKET_NAME = "agile";

        @Value("${choerodon.service.agile.port:8378}")
        public void setPort(Integer port) {
            PORT = port;
        }

        @Value("${choerodon.service.agile.redis-db:12}")
        public void setRedisDb(Integer redisDb) {
            REDIS_DB = redisDb;
        }

        @Value("${choerodon.service.bucket-name:agile}")
        public void setBucketName(String bucketName) {
            BUCKET_NAME = bucketName;
        }
    }

    @Component
    public static class KnowledgeBaseService {
        public static final String NAME = "${choerodon.service.agile.name:knowledgebase-service}";
        public static final String CODE = "knowledge";
        public static final String PATH = "/knowledge/**";
        public static Integer PORT = 8280;
        public static Integer REDIS_DB = 14;
        public static String BUCKET_NAME = "knowledge";

        @Value("${choerodon.service.agile.port:8280}")
        public void setPort(Integer port) {
            PORT = port;
        }

        @Value("${choerodon.service.agile.redis-db:14}")
        public void setRedisDb(Integer redisDb) {
            REDIS_DB = redisDb;
        }

        @Value("${choerodon.service.bucket-name:knowledge}")
        public void setBucketName(String bucketName) {
            BUCKET_NAME = bucketName;
        }
    }

    @Component
    public static class TestManagerService {
        public static final String NAME = "${choerodon.service.agile.name:test-manager-service}";
        public static final String CODE = "test";
        public static final String PATH = "/test/**";
        public static Integer PORT = 8093;
        public static Integer REDIS_DB = 13;
        public static String BUCKET_NAME = "test";

        @Value("${choerodon.service.agile.port:8093}")
        public void setPort(Integer port) {
            PORT = port;
        }

        @Value("${choerodon.service.agile.redis-db:13}")
        public void setRedisDb(Integer redisDb) {
            REDIS_DB = redisDb;
        }

        @Value("${choerodon.service.bucket-name:test}")
        public void setBucketName(String bucketName) {
            BUCKET_NAME = bucketName;
        }
    }

}
