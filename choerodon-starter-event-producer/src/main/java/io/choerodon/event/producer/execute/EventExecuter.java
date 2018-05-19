package io.choerodon.event.producer.execute;

/**
 * @author flyleft
 * 2018/4/8
 */
public interface EventExecuter {
    /**
     * 具体业务执行代码回调
     * @param uuid 传入的uuid
     */
    void doSomething(final String uuid);
}
