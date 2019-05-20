package io.choerodon.core.notify;

/**
 * @author dengyouquan
 **/
public interface PmTemplate extends NotifyTemplate {
    @Override
    default String type(){
        return "pm";
    }
}
