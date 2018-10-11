package io.choerodon.core.notify;

public interface EmailTemplate extends NotifyTemplate {
    default String type(){
        return "email";
    }
}
