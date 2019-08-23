package io.choerodon.websocket.send;

import io.choerodon.websocket.exception.GetSelfSubChannelsFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BrokerManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrokerManager.class);

    private static final String REGISTER_PREFIX = "choerodon:websocket:broker-registry:";
    private static final String BROKER_NAME_PREFIX = "choerodon:websocket:broker:";
    private static final String BROKER_HEARTBEAT_POSTFIX = ":heartbeat";
    private static final String BROKER_SUBSCRIBE_POSTFIX = ":subscribe";

    private Environment environment;

    private StringRedisTemplate redisTemplate;

    private String registerKey;

    private String brokerName;

    @Value("${spring.application.name}")
    private String application;

    @Value("${choerodon.ws.heartBeatIntervalMs:10000}")
    private Long heartBeatIntervalMs;

    private ScheduledExecutorService scheduledExecutorService;

    public BrokerManager(Environment environment, StringRedisTemplate redisTemplate, @Qualifier("registerHeartBeat")ScheduledExecutorService scheduledExecutorService) {
        this.scheduledExecutorService = scheduledExecutorService;
        this.environment = environment;
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void start() {
        // 初始化变量
        generateRegisterKey();
        generateBrokerName();
        register();
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                register();
                Optional.ofNullable(redisTemplate.opsForSet().members(registerKey)).orElse(Collections.emptySet()).forEach(t -> {
                    if (t.equals(this.brokerName)) {
                        return;
                    }
                    long lastUpdateTime = Long.parseLong(Optional.ofNullable(redisTemplate.opsForValue().get(getBrokerHeartbeatKey())).orElse("0"));
                    if (System.currentTimeMillis() - lastUpdateTime > 2 * heartBeatIntervalMs) {
                        removeDeathBroker(t);
                    }
                });
            } catch (Exception e) {
                LOGGER.error("error.redisRegister.heartBeat", e);
            }
        }, heartBeatIntervalMs, heartBeatIntervalMs, TimeUnit.MILLISECONDS);
    }

    // Broker 注册列表对应的Key
    // 在Redis中Value为BrokerName
    private void generateRegisterKey(){
        this.registerKey =  REGISTER_PREFIX+application;
    }

    private void generateBrokerName() {
        try {
            if (this.brokerName == null) {
                brokerName = BROKER_NAME_PREFIX+application + ":" + InetAddress.getLocalHost().getHostAddress()+  ":" + environment.getProperty("server.port")+  ":"+ UUID.randomUUID();
            }
        } catch (UnknownHostException e) {
            throw new GetSelfSubChannelsFailedException(e);
        }
    }

    public String getBrokerName(){
        return brokerName;
    }

    // Broker订阅的Key列表的redis key
    public String getBrokerKeyMapKey(){
        return this.brokerName+BROKER_SUBSCRIBE_POSTFIX;
    }

    // Broker订阅的Key列表的redis key
    public String getBrokerKeyMapKey(String brokerName){
        return brokerName+BROKER_SUBSCRIBE_POSTFIX;
    }

    private String getBrokerHeartbeatKey(){
        return this.brokerName+BROKER_HEARTBEAT_POSTFIX;
    }

    private String getBrokerHeartbeatKey(String brokerName){
        return brokerName+BROKER_HEARTBEAT_POSTFIX;
    }

    public String getRegisterKey(){
        return this.registerKey;
    }

    public Set<String> getActiveBrokers() {
        return redisTemplate.opsForSet().members(registerKey);
    }

    // 向注册表注册，并刷新时间
    private void register() {
        // 在Broker注册表中添加当前brokerName
        redisTemplate.opsForSet().add(this.registerKey, this.brokerName);
        // 刷新当前Broker的心跳时间
        redisTemplate.opsForValue().set(getBrokerHeartbeatKey(), Long.toString(System.currentTimeMillis()));
    }
    // 失效Broker
    private void removeDeathBroker(String deathBrokerName) {
        // 从注册表中删除Broker
        redisTemplate.opsForSet().remove(registerKey, deathBrokerName);
        // 删除心跳Key
        redisTemplate.delete(getBrokerHeartbeatKey(deathBrokerName));
        //
        //redisTemplate.delete(REGISTER_PREFIX + channel);
    }
}
