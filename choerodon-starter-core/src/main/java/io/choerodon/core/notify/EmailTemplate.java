package io.choerodon.core.notify;

public interface EmailTemplate extends NotifyTemplate {
    String code();

    String name();

    default String type() {
        return "email";
    }
}
