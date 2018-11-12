package io.choerodon.oauth.core.password.config;

import io.choerodon.oauth.core.password.PasswordPolicyManager;
import io.choerodon.oauth.core.password.PasswordStrategyStore;
import io.choerodon.oauth.core.password.mapper.BaseLoginAttemptTimesMapper;
import io.choerodon.oauth.core.password.mapper.BaseLoginHistoryMapper;
import io.choerodon.oauth.core.password.mapper.BasePasswordHistoryMapper;
import io.choerodon.oauth.core.password.record.LoginRecord;
import io.choerodon.oauth.core.password.record.PasswordRecord;
import io.choerodon.oauth.core.password.validator.login.MaxErrorTimeStrategy;
import io.choerodon.oauth.core.password.validator.password.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wuguokai
 */
@Configuration
public class BeanConfig {

    @Bean
    public PasswordStrategyStore passwordStrategyStore(ApplicationContext context) {
        return new PasswordStrategyStore(context);
    }

    @Bean
    public PasswordPolicyManager passwordPolicyManager(PasswordStrategyStore passwordStrategyStore,
                                                       BaseLoginAttemptTimesMapper baseLoginAttemptTimesMapper) {
        return new PasswordPolicyManager(passwordStrategyStore, baseLoginAttemptTimesMapper);
    }

    @Bean
    public DigitsCountStrategy digitsCountStategy() {
        return new DigitsCountStrategy();
    }

    @Bean
    public LowercaseCountStrategy lowercaseCountStategy() {
        return new LowercaseCountStrategy();
    }

    @Bean
    public MaxLengthStrategy maxLengthStrategy() {
        return new MaxLengthStrategy();
    }

    @Bean
    public MinLengthStrategy minLengthStrategy() {
        return new MinLengthStrategy();
    }

    @Bean
    public NotRecentStrategy notRecentStrategy(BasePasswordHistoryMapper passwordHistoryMapper) {
        return new NotRecentStrategy(passwordHistoryMapper);
    }

    @Bean
    public NotUsernameStrategy notUsernameStrategy() {
        return new NotUsernameStrategy();
    }

    @Bean
    public RegularStrategy regularStrategy() {
        return new RegularStrategy();
    }

    @Bean
    public SpecialCharCountStrategy specialCharCountStategy() {
        return new SpecialCharCountStrategy();
    }

    @Bean
    public UppercaseCountStrategy uppercaseCountStategy() {
        return new UppercaseCountStrategy();
    }

    @Bean
    public MaxErrorTimeStrategy maxErrorTimeStrategy(BaseLoginAttemptTimesMapper loginAttemptTimesMapper) {
        return new MaxErrorTimeStrategy(loginAttemptTimesMapper);
    }

    @Bean
    public LoginRecord loginRecord(BaseLoginAttemptTimesMapper baseLoginAttemptTimesMapper,
                                   BaseLoginHistoryMapper baseLoginHistoryMapper) {
        return new LoginRecord(baseLoginAttemptTimesMapper, baseLoginHistoryMapper);
    }

    @Bean
    public PasswordRecord passwordRecord(BaseLoginAttemptTimesMapper baseLoginAttemptTimesMapper,
                                         BasePasswordHistoryMapper basePasswordHistoryMapper) {
        return new PasswordRecord(baseLoginAttemptTimesMapper, basePasswordHistoryMapper);
    }
}
