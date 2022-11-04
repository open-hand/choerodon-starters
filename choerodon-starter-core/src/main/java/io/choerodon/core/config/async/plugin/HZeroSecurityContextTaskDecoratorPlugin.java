package io.choerodon.core.config.async.plugin;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 猪齿鱼异步线程装饰器插件, 用于父子线程同步SecurityContext(包含{@link io.choerodon.core.oauth.CustomUserDetails})
 * @author gaokuo.dai@zknow.com 2022-11-04
 */
public class HZeroSecurityContextTaskDecoratorPlugin implements ChoerodonTaskDecoratorPlugin<SecurityContext>{

    @Override
    public int orderSeq() {
        return ChoerodonTaskDecoratorPlugin.MIDDLE_PRIORITY - 200;
    }

    @Override
    public SecurityContext getResource() {
        return SecurityContextHolder.getContext();
    }

    @Override
    public void setResource(SecurityContext resource) {
        SecurityContextHolder.setContext(resource);
    }
}
