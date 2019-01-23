package io.choerodon.config.execute;

import io.choerodon.config.utils.InitConfigProperties;

import java.io.IOException;

/**
 * 执行器接口
 *
 * @author wuguokai
 */
public interface Executor {

    void execute(InitConfigProperties properties, String configFile) throws IOException;

}
