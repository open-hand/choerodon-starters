package io.choerodon.redis;

import io.choerodon.redis.impl.HashStringRedisCacheGroup;
import org.springframework.stereotype.Component;

@Component
public class TestCache extends HashStringRedisCacheGroup<TestEntity> {
}
