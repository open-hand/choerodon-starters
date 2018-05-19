package io.choerodon.oauth.core.password.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.choerodon.oauth.core.password.PasswordPolicyManager;
import io.choerodon.oauth.core.password.PasswordStrategyStore;
import io.choerodon.oauth.core.password.mapper.BaseLoginAttemptTimesMapper;
import io.choerodon.oauth.core.password.mapper.BasePasswordHistoryMapper;
import io.choerodon.oauth.core.password.record.LoginRecord;
import io.choerodon.oauth.core.password.record.PasswordRecord;
import io.choerodon.oauth.core.password.validator.login.MaxErrorTimeStrategy;
import io.choerodon.oauth.core.password.validator.password.*;

/**
 * @author wuguokai
 */
@Configuration
public class BeanConfig {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private BasePasswordHistoryMapper passwordHistoryMapper;

    @Autowired
    private BaseLoginAttemptTimesMapper loginAttemptTimesMapper;

    @Bean
    public PasswordStrategyStore passwordStrategyStore(){
        return new PasswordStrategyStore(context);
    }

    @Bean
    public PasswordPolicyManager passwordPolicyManager(){
        return new PasswordPolicyManager();
    }

    @Bean
    public DigitsCountStategy digitsCountStategy(){
        return new DigitsCountStategy();
    }

    @Bean
    public LowercaseCountStategy lowercaseCountStategy(){
        return new LowercaseCountStategy();
    }

    @Bean
    public MaxLengthStrategy maxLengthStrategy(){
        return new MaxLengthStrategy();
    }

    @Bean
    public MinLengthStrategy minLengthStrategy(){
        return new MinLengthStrategy();
    }

    @Bean
    public NotRecentStrategy notRecentStrategy(){
        return new NotRecentStrategy(passwordHistoryMapper);
    }

    @Bean
    public NotUsernameStrategy notUsernameStrategy(){
        return new NotUsernameStrategy();
    }

    @Bean
    public RegularStrategy regularStrategy(){
        return new RegularStrategy();
    }

    @Bean
    public SpecialCharCountStategy specialCharCountStategy(){
        return new SpecialCharCountStategy();
    }

    @Bean
    public UppercaseCountStategy uppercaseCountStategy(){
        return new UppercaseCountStategy();
    }

    @Bean
    public MaxErrorTimeStrategy maxErrorTimeStrategy(){
        return new MaxErrorTimeStrategy(loginAttemptTimesMapper);
    }

    @Bean
    public LoginRecord loginRecord() {
        return new LoginRecord();
    }

    @Bean
    public PasswordRecord passwordRecord() {
        return new PasswordRecord();
    }
}
