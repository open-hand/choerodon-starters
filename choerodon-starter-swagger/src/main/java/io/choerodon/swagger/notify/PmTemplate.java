package io.choerodon.swagger.notify;

/**
 * @author dengyouquan
 **/
public interface PmTemplate extends NotifyTemplate {
    default String type(){
        return "pm";
    }
}
