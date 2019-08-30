package io.choerodon.redis.impl

import spock.lang.Specification

class RedisNodeAutoConfigSpec extends Specification {
    def "Set Sentinels"() {
        when:
        RedisNodeAutoConfig config = new RedisNodeAutoConfig()
        config.setSentinels(["10.86.20.183:26379", "10.86.20.184:26379"] as String[])
        then:
        config.size() == 2
    }
}
