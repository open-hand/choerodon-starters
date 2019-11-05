package io.choerodon.core.notify;

/**
 * @author dengyouquan
 **/
public interface PmTemplate extends NotifyTemplate {
    String code();

    String name();

    @Override
    default String type(){
        return "pm";
    }
}
