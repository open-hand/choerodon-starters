package io.choerodon.websocket.v2.helper;

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
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class BrokerHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrokerHelper.class);

    private static final String REGISTER_PREFIX = "choerodon:msg:register:";

    private Environment environment;

    private String selfSubChannel;

    private StringRedisTemplate redisTemplate;

    private String registerKey;

    @Value("${spring.application.name}")
    private String application;

    @Value("${choerodon.ws.heartBeatIntervalMs}")
    private Long heartBeatIntervalMs;

    private ScheduledExecutorService scheduledExecutorService;

    public BrokerHelper(Environment environment, StringRedisTemplate redisTemplate) {
        this.scheduledExecutorService = Executors.newScheduledThreadPool(1);
        this.environment = environment;
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void start() {
        this.registerKey = REGISTER_PREFIX + application;
        registerByBrokerName();
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                String thisInstanceRegisterKey = REGISTER_PREFIX + brokerName();
                redisTemplate.opsForSet().add(registerKey, brokerName());
                redisTemplate.opsForValue().set(thisInstanceRegisterKey, Long.toString(System.currentTimeMillis()));
                Optional.ofNullable(redisTemplate.opsForSet().members(registerKey)).orElse(Collections.emptySet()).forEach(t -> {
                    if (t.equals(brokerName())) {
                        return;
                    }
                    String instanceRegisterKey = REGISTER_PREFIX + t;
                    long lastUpdateTime = Long.parseLong(Optional.ofNullable(redisTemplate.opsForValue().get(instanceRegisterKey)).orElse("0"));
                    if (System.currentTimeMillis() - lastUpdateTime > 2 * heartBeatIntervalMs) {
                        removeDeathBroker(t);
                    }
                });
            } catch (Exception e) {
                LOGGER.error("error.redisRegister.heartBeat", e);
            }
        }, heartBeatIntervalMs, heartBeatIntervalMs, TimeUnit.MILLISECONDS);
    }

    public String brokerName() {
        try {
            if (this.selfSubChannel == null) {
                selfSubChannel = application + ":" + InetAddress.getLocalHost().getHostAddress() + ":" + environment.getProperty("server.port");
            }
            return this.selfSubChannel;
        } catch (UnknownHostException e) {
            throw new GetSelfSubChannelsFailedException(e);
        }
    }

    public Set<String> getSurvivalBrokers() {
        return redisTemplate.opsForSet().members(registerKey);
    }

    private void registerByBrokerName() {
        redisTemplate.opsForSet().add(registerKey, brokerName());
        String thisInstanceRegisterKey = REGISTER_PREFIX + brokerName();
        redisTemplate.opsForValue().set(thisInstanceRegisterKey, System.currentTimeMillis() + "");
    }

    private void removeDeathBroker(String channel) {
        redisTemplate.opsForSet().remove(registerKey, channel);
        redisTemplate.delete(channel);
        redisTemplate.delete(REGISTER_PREFIX + channel);
    }
}
