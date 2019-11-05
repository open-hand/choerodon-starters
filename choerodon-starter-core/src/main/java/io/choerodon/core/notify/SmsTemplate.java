package io.choerodon.core.notify;

/**
 * @author superlee
 * @since 2019-05-20
 **/
public interface SmsTemplate extends NotifyTemplate {

    String code();

    String name();

    @Override
    default String type() {
        return "sms";
    }
}
