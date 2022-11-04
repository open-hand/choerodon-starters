package io.choerodon.core.config.async;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.task.TaskDecorator;

import io.choerodon.core.config.async.plugin.ChoerodonTaskDecoratorPlugin;

/**
 * 猪齿鱼异步线程装饰器, 用于同步子线程同步父线程的线程变量<br/>
 * 具体同步哪些环境变量是通过ChoerodonTaskDecoratorPlugin插件机制实现的
 * @author gaokuo.dai@zknow.com 2022-11-04
 */
public class ChoerodonTaskDecorator implements TaskDecorator {

    protected final List<ChoerodonTaskDecoratorPlugin<?>> taskDecoratorPlugins;

    public ChoerodonTaskDecorator(List<ChoerodonTaskDecoratorPlugin<?>> taskDecoratorPlugins) {
        if(taskDecoratorPlugins == null) {
            this.taskDecoratorPlugins = Collections.emptyList();
        } else {
            taskDecoratorPlugins.sort(Comparator.comparing(ChoerodonTaskDecoratorPlugin::orderSeq));
            this.taskDecoratorPlugins = taskDecoratorPlugins;
        }
    }

    @Override
    @Nonnull
    @SuppressWarnings({"rawtypes", "unchecked"})// 类型体操, 忽略警告
    public Runnable decorate(@Nonnull Runnable runnable) {
        if(CollectionUtils.isEmpty(taskDecoratorPlugins)) {
            return runnable;
        }
        // 获取父线程资源值
        // 注意, Collectors.toMap不接受null key和null value, 所以这里用Optional包装一下, 下文同理
        final Map<ChoerodonTaskDecoratorPlugin, Optional<?>> parentThreadPluginToResourceMap = this.taskDecoratorPlugins.stream()
                .collect(Collectors.toMap(Function.identity(), plugin -> Optional.ofNullable(plugin.getResource())));
        return () -> {
            // 获取子线程资源原值
            final Map<ChoerodonTaskDecoratorPlugin, Optional<?>> childThreadPluginToResourceMap = this.taskDecoratorPlugins.stream()
                    .collect(Collectors.toMap(Function.identity(), plugin -> Optional.ofNullable(plugin.getResource())));
            try {
                // 设置子线程资源值为父线程值
                parentThreadPluginToResourceMap.forEach((plugin, optionalResource) -> plugin.setResource(optionalResource.orElse(null)));
                // 执行原异步函数
                runnable.run();
            } finally {
                // 还原子线程资源值
                childThreadPluginToResourceMap.forEach((plugin, optionalResource) -> plugin.setResource(optionalResource.orElse(null)));
            }

        };
    }

}
