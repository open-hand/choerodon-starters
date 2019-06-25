package io.choerodon.websocket.register;

import io.choerodon.websocket.ChoerodonWebSocketProperties;
import io.choerodon.websocket.exception.GetSelfSubChannelsFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class DefaultRedisChannelRegisterImpl implements RedisChannelRegister {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRedisChannelRegisterImpl.class);

    private static final Long HEART_BEAT_INTERVAL = 10000L;

    private static final String REGISTER_PREFIX = "choerodon:msg:register:";

    private Environment environment;

    private String selfSubChannel;

    private StringRedisTemplate redisTemplate;

    private String registerKey;

    @Value("${spring.application.name}")
    private String application;

    private ScheduledExecutorService scheduledExecutorService;

    private ChoerodonWebSocketProperties properties;

    public DefaultRedisChannelRegisterImpl(Environment environment,
                                           StringRedisTemplate redisTemplate,
                                           @Qualifier("registerHeartBeat") ScheduledExecutorService scheduledExecutorService,
                                           ChoerodonWebSocketProperties properties) {
        this.scheduledExecutorService = scheduledExecutorService;
        this.environment = environment;
        this.redisTemplate = redisTemplate;
        this.properties = properties;
    }

    @PostConstruct
    public void start() {
        this.registerKey = REGISTER_PREFIX + application;
        registerByChannelName();
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                String thisInstanceRegisterKey = REGISTER_PREFIX + channelName();
                redisTemplate.opsForSet().add(registerKey, channelName());
                redisTemplate.opsForValue().set(thisInstanceRegisterKey, System.currentTimeMillis() + "");
                redisTemplate.opsForSet().members(registerKey).forEach(t -> {
                    if (t.equals(channelName())) {
                        return;
                    }
                    String instanceRegisterKey = REGISTER_PREFIX + t;
                    long lastUpdateTime = Long.parseLong(redisTemplate.opsForValue().get(instanceRegisterKey));
                    if (System.currentTimeMillis() - lastUpdateTime > 2 * HEART_BEAT_INTERVAL) {
                        removeDeathChannel(t);
                    }
                });
            } catch (Exception e) {
                LOGGER.error("error.redisRegister.heartBeat", e);
            }
        }, properties.getHeartBeatIntervalMs(), properties.getHeartBeatIntervalMs(), TimeUnit.MILLISECONDS);
    }

    @Override
    public String channelName() {
        try {
            if (this.selfSubChannel == null) {
                selfSubChannel = application + ":" + InetAddress.getLocalHost().getHostAddress() + ":" + environment.getProperty("server.port");
            }
            return this.selfSubChannel;
        } catch (UnknownHostException e) {
            throw new GetSelfSubChannelsFailedException(e);
        }
    }

    @Override
    public Set<String> getSurvivalChannels() {
        return redisTemplate.opsForSet().members(registerKey);
    }

    @Override
    public void registerByChannelName() {
        redisTemplate.opsForSet().add(registerKey, channelName());
        String thisInstanceRegisterKey = REGISTER_PREFIX + channelName();
        redisTemplate.opsForValue().set(thisInstanceRegisterKey, System.currentTimeMillis() + "");
    }

    @Override
    public void removeDeathChannel(String channel) {
        redisTemplate.opsForSet().remove(registerKey, channel);
        redisTemplate.delete(channel);
        redisTemplate.delete(REGISTER_PREFIX + channel);
    }
}
