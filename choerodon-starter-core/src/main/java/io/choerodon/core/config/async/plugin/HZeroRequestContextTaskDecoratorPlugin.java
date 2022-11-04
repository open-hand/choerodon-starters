package io.choerodon.core.config.async.plugin;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * 猪齿鱼异步线程装饰器插件, 用于父子线程同步HTTP请求信息
 * @author gaokuo.dai@zknow.com 2022-11-04
 */
public class HZeroRequestContextTaskDecoratorPlugin implements ChoerodonTaskDecoratorPlugin<RequestAttributes>{

    @Override
    public int orderSeq() {
        return ChoerodonTaskDecoratorPlugin.MIDDLE_PRIORITY - 300;
    }

    @Override
    public RequestAttributes getResource() {
        try {
            return RequestContextHolder.currentRequestAttributes();
        } catch (IllegalStateException e) {
            // ignore
            // 如果在一个非HTTP请求环境(如子线程)里调用RequestContextHolder.currentRequestAttributes(), 就会触发此异常, 返回空值即可
            return null;
        }
    }

    @Override
    public void setResource(RequestAttributes resource) {
        RequestContextHolder.setRequestAttributes(resource);
    }
}
