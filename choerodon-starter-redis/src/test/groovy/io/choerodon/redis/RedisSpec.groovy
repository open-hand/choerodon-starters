package io.choerodon.redis

import com.fasterxml.jackson.databind.ObjectMapper
import io.choerodon.message.IMessagePublisher
import io.choerodon.mybatis.common.query.JoinCache
import io.choerodon.redis.impl.CacheReloadProcessor
import io.choerodon.redis.impl.HashStringRedisCache
import io.choerodon.redis.impl.HashStringRedisCacheGroupResolve
import io.choerodon.redis.impl.HashStringRedisCacheResolve
import io.choerodon.redis.impl.RedisCache
import org.apache.ibatis.session.SqlSession
import org.apache.ibatis.session.SqlSessionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.redis.connection.RedisConnection
import org.springframework.data.redis.core.RedisCallback
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import spock.lang.Specification

import java.lang.annotation.Annotation

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE


@ComponentScan
@SpringBootTest(webEnvironment = NONE, classes = [TestApplication])
class RedisSpec extends Specification {
    IMessagePublisher iMessagePublisher;
    @Autowired
    CacheReloadProcessor cacheReloadProcessor;
    @Autowired
    TestCache testCache;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    HashStringRedisCacheGroupResolve hashStringRedisCacheGroupResolve
    @Autowired
    HashStringRedisCacheResolve hashStringRedisCacheResolve;

    def "Cache Setter Getter"() {
        given:
        testCache.redisTemplate = Mock(RedisTemplate)
        TestEntity entity = new TestEntity()
        RedisConnection connection = Mock()
        when:
        cacheReloadProcessor.queue = cacheReloadProcessor.getQueue()
        entity.test1 = "test"
        testCache.setGroupField(["test1", "test2"] as String[])
        testCache.setType(String.class)
        connection.hMGet(*_) >> []
        testCache.redisTemplate.getStringSerializer() >> new StringRedisSerializer()
        testCache.redisTemplate.execute(_) >> { RedisCallback action -> action.doInRedis(connection);entity }
        testCache.setValue("test", entity)
        testCache.setValueField("test")
        then:
        cacheReloadProcessor.queue == "queue:cache:reload"
        cacheReloadProcessor.topic == ["topic:cache:reloaded"] as String[]
        testCache.getGroup("test").getValue("test").test1 == "test"
    }
    def "Cache Reload"(){
        given:
        iMessagePublisher = Mock(IMessagePublisher)
        testCache.redisTemplate = Mock(RedisTemplate)
        testCache.sqlSessionFactory = Mock(SqlSessionFactory)
        cacheReloadProcessor.messagePublisher = iMessagePublisher
        cacheReloadProcessor.setPublishMessageTo(cacheReloadProcessor.getPublishMessageTo())
        when:
        testCache.sqlSessionFactory.openSession() >> Mock(SqlSession)
        iMessagePublisher.message("queue:cache:reload", "testCache") >> cacheReloadProcessor.onQueueMessage("testCache", "queue:cache:reload")
        iMessagePublisher.publish("queue:cache:reload", "testCache") >> cacheReloadProcessor.onTopicMessage("testCache", "queue:cache:reload")
        then:
        iMessagePublisher.publish("queue:cache:reload", "testCache")
        noExceptionThrown()
        when:
        cacheReloadProcessor.onQueueMessage("noExistCache", "queue:cache:reload")
        then:
        noExceptionThrown()

    }

    def "Group Resolve" () {
        when:
        TestEntity entity = new TestEntity();
        entity.test1 = "test"
        JoinCache joinCache = new JoinCache(){
            @Override
            String joinKey() {
                return "test1"
            }

            @Override
            String cacheName() {
                return "testCache"
            }

            @Override
            String joinColumn() {
                return null
            }

            @Override
            Class<? extends Annotation> annotationType() {
                return null
            }
        }
        hashStringRedisCacheGroupResolve.resolve(joinCache, entity, "en_GB")
        then:
        thrown(RuntimeException.class)
    }

    def "Cache Resolve" () {
        when:
        TestEntity entity = new TestEntity();
        entity.test1 = "test"
        JoinCache joinCache = new JoinCache(){
            @Override
            String joinKey() {
                return "test1"
            }

            @Override
            String cacheName() {
                return "testCache"
            }

            @Override
            String joinColumn() {
                return null
            }

            @Override
            Class<? extends Annotation> annotationType() {
                return null
            }
        }
        hashStringRedisCacheResolve.resolve(joinCache, entity, "en_GB")
        then:
        thrown(RuntimeException.class)
    }

    def "Cache HMSet HMGet hVals" () {
        given:
        TestHashStringRedisCache cache = new TestHashStringRedisCache()
        cache.setObjectMapper(objectMapper)
        cache.setType(TestEntity)
        RedisTemplate redisTemplate = Mock(RedisTemplate)
        RedisConnection connection = Mock(RedisConnection)
        TestEntity entity = new TestEntity();
        when:
        redisTemplate.getStringSerializer() >> new StringRedisSerializer()
        connection.hMGet([116, 101, 115, 116] as byte[], [[116, 101, 115, 116] as byte[]] as byte[][]) >> [redisTemplate.getStringSerializer().serialize(objectMapper.writeValueAsString(entity))]
        connection.hVals([116, 101, 115, 116] as byte[]) >> [redisTemplate.getStringSerializer().serialize(objectMapper.writeValueAsString(entity))]
        connection.hVals([104, 97, 112, 58, 99, 97, 99, 104, 101, 58, 110, 117, 108, 108] as byte[]) >> [redisTemplate.getStringSerializer().serialize(objectMapper.writeValueAsString(entity))]
        redisTemplate.execute(*_) >> { RedisCallback action -> action.doInRedis(connection) }
        then:
        cache.setRedisTemplate(redisTemplate)
        cache.hMSet(connection, "test", "test", entity)
        cache.hMGet(connection, "test", "test") != null
        cache.hVals(connection, "test") != null
        cache.getAll() != null
        cache.handleRow(entity)
        cache.remove("test")
        cache.clear()
        when:
        cache.stringToObject(null)
        then:
        thrown(RuntimeException.class)
        when:
        cache.setObjectMapper(null)
        cache.objectToString(entity)
        then:
        thrown(RuntimeException.class)
    }

    def "Cache HMSet HMGet Basic" () {
        given:
        TestHashStringRedisCache cache = new TestHashStringRedisCache()
        cache.setObjectMapper(objectMapper)
        cache.setType(String)
        RedisTemplate redisTemplate = Mock(RedisTemplate)
        RedisConnection connection = Mock(RedisConnection)
        String entity = "test"
        when:
        redisTemplate.getStringSerializer() >> new StringRedisSerializer()
        connection.hMGet([116, 101, 115, 116] as byte[], [[116, 101, 115, 116] as byte[]] as byte[][]) >> [redisTemplate.getStringSerializer().serialize("test")]
        then:
        cache.setRedisTemplate(redisTemplate)
        cache.hMSet(connection, "test", "test", entity)
        cache.hMGet(connection, "test", "test") != null
        when:
        cache.stringToObject(null)
        then:
        thrown(RuntimeException.class)
        when:
        cache.objectToString(null)
        then:
        noExceptionThrown()
    }

    def "Cache getValue" () {
        given:
        TestRedisCache cache = new TestRedisCache()
        cache.setObjectMapper(objectMapper)
        cache.setType(HashMap)
        TestEntityRedisCache entityCache = new TestEntityRedisCache()
        RedisTemplate redisTemplate = Mock(RedisTemplate)
        RedisConnection connection = Mock(RedisConnection)
        when:
        redisTemplate.getStringSerializer() >> new StringRedisSerializer()
        redisTemplate.execute(*_) >> { RedisCallback action -> action.doInRedis(connection) }
        connection.hGetAll([104, 97, 112, 58, 99, 97, 99, 104, 101, 58, 110, 117, 108, 108, 58, 116, 101, 115, 116] as byte[]) >> [([116, 101, 115, 116, 49] as byte[]): ([116, 101, 115, 116] as byte[])]
        cache.setRedisTemplate(redisTemplate)
        then:
        cache.getValue("test").get("test1") == "test"
        when:
        entityCache.setRedisTemplate(redisTemplate)
        entityCache.setType(TestEntity)
        then:
        entityCache.getValue("test").test1 == "test"
    }

    class TestHashStringRedisCache extends HashStringRedisCache<Object> {

        @Override
        void hMSet(RedisConnection connection, String mapKey, String pName, Object pValue) {
            super.hMSet(connection, mapKey, pName, pValue)
        }

        @Override
        Object hMGet(RedisConnection connection, String mapKey, String pName) {
            return super.hMGet(connection, mapKey, pName)
        }

        @Override
        List<Object> hVals(RedisConnection connection, String mapKey) {
            return super.hVals(connection, mapKey)
        }

        @Override
        String objectToString(Object value) {
            return super.objectToString(value)
        }

        @Override
        Object stringToObject(String value) {
            return super.stringToObject(value)
        }

        @Override
        void handleRow(Object row) {
            super.handleRow(row)
        }
    }

    class TestRedisCache extends RedisCache<Map> {

    }
    class TestEntityRedisCache extends RedisCache<TestEntity> {

    }
}
