package io.choerodon.core.config.async.plugin;

/**
 * 猪齿鱼异步线程装饰器插件接口, 用于同步子线程同步父线程的线程变量时指定同步什么变量
 * @author gaokuo.dai@zknow.com 2022-11-04
 */
public interface  ChoerodonTaskDecoratorPlugin<T> {

    int MAX_PRIORITY = Integer.MIN_VALUE;
    int MIN_PRIORITY = Integer.MAX_VALUE;
    int MIDDLE_PRIORITY = Integer.MAX_VALUE / 2;

    /**
     * @return 加载优先级, 最高优先级为{@link ChoerodonTaskDecoratorPlugin#MAX_PRIORITY}, 最低优先级为{@link ChoerodonTaskDecoratorPlugin#MAX_PRIORITY}, 默认优先级为中值{@link ChoerodonTaskDecoratorPlugin#MIDDLE_PRIORITY}
     */
    default int orderSeq() {
        return MIDDLE_PRIORITY;
    }

    /**
     * @return 如何获取线程变量
     */
    T getResource();

    /**
     * 如何设置线程变量
     * @param resource 线程变量
     */
    void setResource(T resource);

}
