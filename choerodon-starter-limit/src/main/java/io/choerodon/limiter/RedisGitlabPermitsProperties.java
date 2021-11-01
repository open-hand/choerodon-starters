package io.choerodon.limiter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
        prefix = "choerodon.gitlab.permits"
)
public class RedisGitlabPermitsProperties {
    /**
     * 每秒生成的令牌数
     */
    private double permitsPerSecond = 100.0;
    /**
     * 存储未使用的令牌多少秒，桶中的最大令牌数 = permitsPerSecond * maxBurstSeconds
     */
    private double maxBurstSeconds = 1.0;
    /**
     * 令牌的失效时间(s)
      */
    private int expire = 120;

    public double getPermitsPerSecond() {
        return permitsPerSecond;
    }

    public void setPermitsPerSecond(double permitsPerSecond) {
        this.permitsPerSecond = permitsPerSecond;
    }

    public double getMaxBurstSeconds() {
        return maxBurstSeconds;
    }

    public void setMaxBurstSeconds(double maxBurstSeconds) {
        this.maxBurstSeconds = maxBurstSeconds;
    }

    public int getExpire() {
        return expire;
    }

    public void setExpire(int expire) {
        this.expire = expire;
    }
}
