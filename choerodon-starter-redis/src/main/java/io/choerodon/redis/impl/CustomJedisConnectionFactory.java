package io.choerodon.redis.impl;

import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.util.Pool;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author xiangyu.qi@hand-china.com on 2017/11/29.
 */
public class CustomJedisConnectionFactory extends JedisConnectionFactory {

    public CustomJedisConnectionFactory() {
        super();
    }

    public CustomJedisConnectionFactory(RedisSentinelConfiguration sentinelConfig) {
        super(sentinelConfig);
    }

    public CustomJedisConnectionFactory(RedisClusterConfiguration clusterConfig) {
        super(clusterConfig);
    }

    @Override
    protected Pool<Jedis> createRedisPool() {
        return new JedisPool(getPoolConfig(), getShardInfo().getHost(), getShardInfo().getPort(),
                getShardInfo().getSoTimeout(), getShardInfo().getPassword(), getDatabase());
    }

    @Override
    protected Pool<Jedis> createRedisSentinelPool(RedisSentinelConfiguration config) {
        return new JedisSentinelPool(config.getMaster().getName(), convertToJedisSentinelSet(config.getSentinels()),
                getPoolConfig() != null ? getPoolConfig() : new JedisPoolConfig(), getShardInfo().getSoTimeout(),
                getShardInfo().getPassword(), getDatabase());
    }

    public void setPoolDb(int poolDb) {
        this.setDatabase(poolDb);
    }

    private Set<String> convertToJedisSentinelSet(Collection<RedisNode> nodes) {

        if (CollectionUtils.isEmpty(nodes)) {
            return Collections.emptySet();
        }

        Set<String> convertedNodes = new LinkedHashSet<String>(nodes.size());
        for (RedisNode node : nodes) {
            if (node != null) {
                convertedNodes.add(node.asString());
            }
        }
        return convertedNodes;
    }
}
