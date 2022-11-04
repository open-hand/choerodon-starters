package io.choerodon.core.config.async.plugin;

import java.util.Optional;

import org.hzero.starter.keyencrypt.core.EncryptContext;
import org.hzero.starter.keyencrypt.core.EncryptType;

/**
 * 猪齿鱼异步线程装饰器插件, 用于父子线程同步主键加密方式
 * @author gaokuo.dai@zknow.com 2022-11-04
 */
public class HZeroEncryptContextTaskDecoratorPlugin implements ChoerodonTaskDecoratorPlugin<String>{

    @Override
    public int orderSeq() {
        return ChoerodonTaskDecoratorPlugin.MIDDLE_PRIORITY - 100;
    }

    @Override
    public String getResource() {
        return Optional.ofNullable(EncryptContext.encryptType()).map(EncryptType::toString).orElse(null);
    }

    @Override
    public void setResource(String resource) {
        EncryptContext.setEncryptType(resource);
    }
}
