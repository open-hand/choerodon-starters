/*
 * #{copyright}#
 */

package io.choerodon.redis.impl;

import org.springframework.data.redis.connection.RedisNode;
import org.springframework.util.StringUtils;

import java.util.ArrayList;

/**
 * auto create RedisNode list, using a config list.
 * <p>
 * invalid sentinel config will be ignore
 * 
 * @author shengyang.zhou@hand-china.com
 */
public class RedisNodeAutoConfig extends ArrayList<RedisNode> {

    public void setSentinels(String[] sentinels) {
        for (String s : sentinels) {
            if (StringUtils.isEmpty(s) || s.contains("$")) {
                continue;
            }
            String[] ss = s.split(":");
            RedisNode redisNode = new RedisNode(ss[0].trim(), Integer.parseInt(ss[1].trim()));
            add(redisNode);
        }
    }
}
