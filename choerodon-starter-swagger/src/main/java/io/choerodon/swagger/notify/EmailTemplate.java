package io.choerodon.swagger.notify;

public interface EmailTemplate extends NotifyTemplate {
    default String type(){
        return "email";
    }
}
