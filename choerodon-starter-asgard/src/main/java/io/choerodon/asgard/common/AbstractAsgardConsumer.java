package io.choerodon.asgard.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.StringUtils;

import io.choerodon.asgard.saga.consumer.MockHttpServletRequest;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

public abstract class AbstractAsgardConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAsgardConsumer.class);

    protected final Set<Long> runningTasks = new ConcurrentSkipListSet<>();

    protected final String service;

    protected final String instance;

    protected final ObjectMapper objectMapper = new ObjectMapper();

    protected final PlatformTransactionManager transactionManager;

    protected final Executor executor;


    private final ApplicationContextHelper applicationContextHelper;

    public AbstractAsgardConsumer(String service, String instance,
                                  PlatformTransactionManager transactionManager,
                                  Executor executor, ScheduledExecutorService scheduledExecutorService,
                                  ApplicationContextHelper applicationContextHelper,
                                  long pollIntervalMs) {
        this.service = service;
        this.instance = instance;
        this.transactionManager = transactionManager;
        this.executor = executor;
        this.applicationContextHelper = applicationContextHelper;
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                beforeSchedule();
                scheduleRunning(instance);
            } catch (Exception e) {
                LOGGER.warn("error.asgard.scheduleRunning, msg: {}", e.getMessage());
                e.printStackTrace();
            }
        }, 20000, pollIntervalMs, TimeUnit.MILLISECONDS);
    }

    protected abstract void scheduleRunning(String instance);

    private void beforeSchedule() {
        CustomUserDetails customUserDetails = DetailsHelper.getAnonymousDetails();
        customUserDetails.setUserId(0L);
        customUserDetails.setOrganizationId(0L);
        customUserDetails.setLanguage("zh_CN");
        customUserDetails.setTimeZone("CCT");
        DetailsHelper.setCustomUserDetails(customUserDetails);
    }

    protected void beforeInvoke(CustomUserDetails customUserDetails) {
        if (customUserDetails == null) {
            customUserDetails = new CustomUserDetails("default", "unknown", Collections.emptyList());
            customUserDetails.setUserId(0L);
            customUserDetails.setOrganizationId(0L);
            customUserDetails.setLanguage("zh_CN");
            customUserDetails.setTimeZone("CCT");
        }
        Authentication user = new UsernamePasswordAuthenticationToken("default", "N/A", Collections.emptyList());
        OAuth2Request request = new OAuth2Request(new HashMap<>(0), "", Collections.emptyList(), true,
                Collections.emptySet(), Collections.emptySet(), null, null, null);
        OAuth2Authentication authentication = new OAuth2Authentication(request, user);
        OAuth2AuthenticationDetails oAuth2AuthenticationDetails = new OAuth2AuthenticationDetails(new MockHttpServletRequest());
        oAuth2AuthenticationDetails.setDecodedDetails(customUserDetails);
        authentication.setDetails(oAuth2AuthenticationDetails);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    protected void afterInvoke() {
        SecurityContextHolder.clearContext();
    }

    protected PlatformTransactionManager getSagaTaskTransactionManager(final String transactionManagerName) {
        PlatformTransactionManager platformTransactionManager;
        if (StringUtils.isEmpty(transactionManagerName)) {
            platformTransactionManager = transactionManager;
        } else {
            platformTransactionManager = applicationContextHelper.getSpringFactory()
                    .getBean(transactionManagerName, PlatformTransactionManager.class);
        }
        return platformTransactionManager;
    }

    protected TransactionStatus createTransactionStatus(final PlatformTransactionManager transactionManager,
                                                        final int isolationLevel) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(isolationLevel);
        return transactionManager.getTransaction(def);
    }

}
