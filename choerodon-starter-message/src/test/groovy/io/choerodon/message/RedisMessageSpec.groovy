package io.choerodon.message

import io.choerodon.message.impl.redis.MessagePublisherImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.DefaultMessage
import org.springframework.data.redis.connection.MessageListener
import org.springframework.data.redis.connection.jedis.JedisConnection
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.DefaultListOperations
import org.springframework.data.redis.core.RedisCallback
import org.springframework.data.redis.core.RedisTemplate
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE


@SpringBootTest(webEnvironment = NONE, classes = [TestApplication, RedisMessageSpec], properties = ["message.provider=redis"])
class RedisMessageSpec extends Specification {

    @Autowired
    TestMonitor monitor

    @Autowired
    MessagePublisherImpl messagePublisher;

    @Autowired
    JedisConnectionFactory connectionFactory;

    @Bean(value = "v2redisConnectionFactory")
    JedisConnectionFactory v2redisConnectionFactory(){
        JedisConnection connection = GroovyMock(JedisConnection){
            DefaultMessage message = new DefaultMessage("test:topic" as byte[], "test" as byte[])
            bLPop(*_) >>> [Arrays.asList("test:queue" as byte[], "test" as byte[]),
                           Arrays.asList("test:queue" as byte[], "test" as byte[]),
                           Arrays.asList("test:queue" as byte[], "test" as byte[]), null]
            pSubscribe(*_) >> {
                MessageListener listener, byte[]... patterns -> listener.onMessage(message, patterns[0])
            }
            getClientName(*_) >> "test client"
        }
        JedisConnectionFactory connectionFactory = GroovyMock(JedisConnectionFactory){
            getConnection() >> connection
            getClientName() >> "test factor"
        }
        return connectionFactory;
    }

    @Bean(value = "redisTemplate")
    RedisTemplate v2redisTemplate() throws Exception {
        RedisTemplate redisTemplate = GroovyMock(RedisTemplate){
            opsForList() >> GroovyMock(DefaultListOperations){
                rightPush("test:topic", "test") >> 1
            }
            getConnectionFactory() >> v2redisConnectionFactory()
            setConnectionFactory(v2redisConnectionFactory())
            execute(*_) >> {
                RedisCallback action -> action.doInRedis(getConnectionFactory().getConnection())
            }
        }
        return redisTemplate;
    }

    def "Redis Message Publisher" () {
        when:
        synchronized (messagePublisher) {
            messagePublisher.message("test:queue", "test")
            messagePublisher.publish("test:topic", "test")
            messagePublisher.wait(100)
        }
        then:
        monitor.topicCount == 1
        monitor.queueCount >= 1
    }
}
