package io.choerodon.websocket;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.choerodon.websocket.controller.Controller;
import io.choerodon.websocket.helper.*;
import io.choerodon.websocket.listener.*;
import io.choerodon.websocket.process.AbstractAgentMsgHandler;
import io.choerodon.websocket.process.MsgProcessor;
import io.choerodon.websocket.process.ProcessManager;
import io.choerodon.websocket.process.SocketMsgDispatcher;
import io.choerodon.websocket.channel.RedisMsgListener;
import io.choerodon.websocket.security.AgentSecurityInterceptor;
import io.choerodon.websocket.security.AgentTokenInterceptor;
import io.choerodon.websocket.security.SecurityCheckManager;
import io.choerodon.websocket.security.WebSecurityInterceptor;
import io.choerodon.websocket.session.*;
import io.choerodon.websocket.websocket.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * @author crock
 */
@Configuration
@EnableWebSocket
@EnableConfigurationProperties(SocketProperties.class)
public class SocketHelperAutoConfiguration implements WebSocketConfigurer {
	private static final Logger logger = LoggerFactory.getLogger(SocketHelperAutoConfiguration.class);
	public static final String BROkER_ID = UUID.randomUUID().toString().replace("-","");
	@Autowired
	SessionRepository sessionRepository;
	@Autowired
	List<MsgProcessor> msgProcessors;
	@Autowired
	SocketProperties socketProperties;


	@Bean
	SocketRegister socketRegister(RedisTemplate<String, String> stringRedisTemplate){
		return new SocketRegister(stringRedisTemplate);
	}

	@Bean
	RedisRouter redisRouter(RedisTemplate<String, String> stringRedisTemplate, SocketRegister socketRegister){
		return new RedisRouter(stringRedisTemplate,socketRegister);
	}

	@Bean
	SocketMsgDispatcher socketMsgDispatcher(RedisRouter redisRouter, RedisSender redisSender){
		return new SocketMsgDispatcher(redisRouter,redisSender);
	}
	@Bean
	SessionRepository inSessionRepository(){
		return new InMemorySessionRepository();
	}

	@Bean
	SockHandlerDelegate sockHandlerDelegate(SimpleMsgListener msgListener, SessionListenerFactory sessionListenerFactory, SessionRepository sessionRepository){
		return new SockHandlerDelegate(msgListener,sessionListenerFactory,sessionRepository);
	}

	@Bean
	MessageListenerAdapter listenerAdapter(RedisMsgListener redisMsgListener) {
		return new MessageListenerAdapter(redisMsgListener);
	}
	@Bean
	AgentCommandListener commandMsgListener(SimpleMsgListener simpleMsgListener){
		return new AgentCommandListener(simpleMsgListener);
	}
	@Bean
    EnvListener envListener(RedisTemplate<Object,Object> redisTemplate){
	    return new EnvListener(redisTemplate);
    }

	@Bean
	CommandSender commandSender(AgentCommandListener agentCommandListener){
		return new CommandSender(agentCommandListener);
	}

	@Bean
	RedisSender redisSender(RedisTemplate<Object, Object> redisTemplate){
		return new RedisSender(redisTemplate);
	}
	@Bean
	SimpleSessionListener simpleSessionListener(SocketRegister socketRegister, SessionRepository sessionRepository){
		return new SimpleSessionListener(socketRegister, sessionRepository);
	}


	@Bean
	RedisMsgListener msgListener(SocketSender socketSender,
                                 SocketRegister socketRegister,
                                 SocketProperties socketProperties){
		return new RedisMsgListener(socketSender,socketRegister,socketProperties);
	}

	@Bean
	SocketSender socketSender(SessionRepository sessionRepository,AbstractAgentMsgHandler abstractAgentMsgHandler){
		return new SocketSender(sessionRepository,abstractAgentMsgHandler);
	}

	@Bean
	AgentSessionListener executorSessionListener(SimpleSessionListener simpleSessionListener, SessionRepository sessionRepository, Optional<OptionalListener> additionalSessionListener,AgentSessionManager agentSessionManager){
		OptionalListener listener;
		listener = additionalSessionListener.orElseGet(() -> new OptionalListener() {
            @Override
            public void onConn(EnvSession envSession) {

            }

            @Override
            public void onClose(String sessionId) {

            }
        });
		return new AgentSessionListener(simpleSessionListener, sessionRepository,listener,agentSessionManager);
	}

	@Bean
	ProcessManager processManager(List<MsgProcessor> processors,
								  RedisSender redisSender,
								  RedisRouter redisRouter,
								  SocketSender socketSender,
								  SocketRegister socketRegister,
								  SocketProperties socketProperties){
		return new ProcessManager(processors,redisRouter,redisSender,socketSender,socketRegister,socketProperties);
	}

	@Bean
	SimpleMsgListener simpleMsgListener(ProcessManager processManager){
		return new SimpleMsgListener(processManager);
	}

	@Bean
    PipeSessionListener logSessionListener(SimpleSessionListener sessionListener,
                                           AgentCommandListener agentCommandListener ,
                                           SocketMsgDispatcher dispatcher){
		return new PipeSessionListener(sessionListener,agentCommandListener,dispatcher);

	}

	@Bean
    InformMsgListener informMsgListener(SimpleMsgListener msgListener){
	    return new InformMsgListener(msgListener);
    }

    @Bean
    PathHelper pathHelper(SocketProperties socketProperties){
	    return new PathHelper(socketProperties);
    }

    @Bean
    InformSender informSender(InformMsgListener informMsgListener){
	    return new InformSender(informMsgListener);
    }

    @Bean("commandTaskExecutor")
	ThreadPoolExecutor taskExecutor(){
		ThreadFactory threadFactory = new ThreadFactoryBuilder()
				.setNameFormat("CommandMsg-%d")
				.setDaemon(false)
				.setPriority(Thread.NORM_PRIORITY)
				.build();
		ThreadPoolExecutor executor = new ThreadPoolExecutor(10,
				socketProperties.getMaxRedisMsgListenerConcurrency(),
				60L,
				TimeUnit.SECONDS,
				new SynchronousQueue<Runnable>(),threadFactory);
		return executor;
	}

	@Bean
	ClRedisContainer clRedisContainer(RedisConnectionFactory redisConnectionFactory,MessageListenerAdapter listenerAdapter,@Qualifier("commandTaskExecutor") ThreadPoolExecutor commandTaskExecutor ) {
		final ClRedisContainer container = new ClRedisContainer();
		container.setConnectionFactory(redisConnectionFactory);
		container.setTaskExecutor(commandTaskExecutor);
		container.addMessageListener( listenerAdapter, new ChannelTopic(BROkER_ID));
		container.addMessageListener( listenerAdapter, new ChannelTopic("log"+BROkER_ID));
		return container;
	}

	@Scheduled(initialDelay = 10*1000,fixedRate = 10*1000)
	public void sendPing(){
		List<Session> sessions = sessionRepository.allExecutors();
		for (Session session : sessions){
			try {
				session.getWebSocketSession().sendMessage(new PingMessage());
			} catch (Exception e) {
				sessionRepository.removeById(session.getUuid());
				logger.error("remove disconnected ");
			}
		}
	}

	@Bean
	SessionListenerFactory sessionListenerFactory(SimpleSessionListener simpleSessionListener,
												  AgentSessionListener agentSessionListener,
												  PipeSessionListener pipeSessionListener){
		return new SessionListenerFactory( simpleSessionListener, agentSessionListener, pipeSessionListener);
	}

	@Bean
	WebSecurityInterceptor webSecurityInterceptor(SocketProperties socketProperties){
		return new WebSecurityInterceptor(socketProperties);
	}

	@Bean
	AgentSecurityInterceptor agentSecurityInterceptor(Optional<AgentTokenInterceptor> optionalTokenInterceptor,
													  PathHelper pathHelper,
													  SocketRegister socketRegister,
													  SocketProperties socketProperties,
													  EnvListener envListener){
		AgentTokenInterceptor agentTokenInterceptor;
		if(optionalTokenInterceptor.isPresent()){
			agentTokenInterceptor = optionalTokenInterceptor.get();
		}else {
			agentTokenInterceptor = null;
		}

		return new AgentSecurityInterceptor(agentTokenInterceptor,pathHelper,socketRegister,socketProperties, envListener);
	}

	@Bean
	SecurityCheckManager securityCheckManager(PathHelper pathHelper,
											  WebSecurityInterceptor webSecurityInterceptor,
											  AgentSecurityInterceptor agentSecurityInterceptor){
		return new SecurityCheckManager(pathHelper,webSecurityInterceptor,agentSecurityInterceptor);
	}

	@Bean
    AgentOptionListener agentOptionListener(RedisTemplate<Object, Object> redisTemplate){
	    return new AgentOptionListener(redisTemplate);
    }

    @Bean
    Controller controller(RedisTemplate<String, String> stringRedisTemplate,
                          RedisTemplate<Object,Object> redisTemplate,
                          AgentOptionListener agentOptionListener,
                          SocketProperties socketProperties,
						  AbstractAgentMsgHandler msgHandler){
	    return new Controller(stringRedisTemplate,redisTemplate, agentOptionListener,socketProperties,msgHandler);
    }


	@Autowired
    private SockHandlerDelegate sockHandlerDelegate;
	@Autowired
    private PathHelper pathHelper;
	@Autowired
	private SecurityCheckManager securityCheckManager;
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(new SocketHandler(sockHandlerDelegate,pathHelper),
				socketProperties.getAgent(), socketProperties.getFront())
				.setAllowedOrigins("*")
				.addInterceptors(new RequestParametersInterceptor(securityCheckManager));
	}

	private final List<AgentConfigurer> configurers = new ArrayList<>();


	@Autowired(required = false)
	public void setConfigurers(List<AgentConfigurer> configurers,AgentSessionManager agentSessionManager) {
		if (!CollectionUtils.isEmpty(configurers)) {
			this.configurers.addAll(configurers);
		}
		registerSessionListeners(agentSessionManager);
	}

	@Bean
	AgentSessionManager agentSessionManager(){
		return   new AgentSessionManager();
	}

	 public void registerSessionListeners(AgentSessionManager agentSessionManager) {
		for (AgentConfigurer configurer : this.configurers) {
			configurer.registerSessionListener(agentSessionManager);
		}
	}


}
