package io.choerodon.redis

import io.choerodon.mybatis.common.query.JoinCache
import io.choerodon.mybatis.common.query.JoinCode
import io.choerodon.mybatis.common.query.JoinLov
import org.springframework.context.annotation.Bean
import spock.lang.Specification

import java.lang.annotation.Annotation

class CacheResolveSpec extends Specification {
    def "Get Join Key"() {
        when:
        TestEntity entity = new TestEntity();
        entity.test1 = "test"
        CacheResolve resolve = new TestCacheResolve()
        then:
        resolve.getJoinKey(new JoinCache(){
            @Override
            Class<? extends Annotation> annotationType() {
                return JoinCache.class
            }

            @Override
            String joinKey() {
                return "test1"
            }

            @Override
            String cacheName() {
                return null
            }

            @Override
            String joinColumn() {
                return null
            }
        }, entity) == "test"
        resolve.getJoinKey(new JoinCode(){
            @Override
            String joinKey() {
                return "test1"
            }

            @Override
            String code() {
                return null
            }

            @Override
            Class<? extends Annotation> annotationType() {
                return null
            }
        }, entity) == "test"
        resolve.getJoinKey(new JoinLov(){
            @Override
            Class<? extends Annotation> annotationType() {
                return JoinCache.class
            }

            @Override
            String joinKey() {
                return "test1"
            }

            @Override
            String lovCode() {
                return null
            }

            @Override
            String dynamicLovColumn() {
                return null
            }
        }, entity) == "test"
        resolve.getJoinKey(new SuppressWarnings(){
            @Override
            String[] value() {
                return new String[0]
            }

            @Override
            Class<? extends Annotation> annotationType() {
                return null
            }
        }, entity) == null
    }

    class TestCacheResolve extends CacheResolve {

        @Override
        Object resolve(Object cacheEntity, Object resultMap, String lang) throws NoSuchFieldException, IllegalAccessException {
            return null
        }
    }
}
