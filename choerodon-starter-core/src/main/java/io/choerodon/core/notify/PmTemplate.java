package io.choerodon.core.notify;

/**
 * @author dengyouquan
 **/
public interface PmTemplate extends NotifyTemplate {
    default String type(){
        return "pm";
    }
}
