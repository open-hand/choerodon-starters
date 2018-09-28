package io.choerodon.config.execute;

import java.io.IOException;

/**
 * 执行器接口
 *
 * @author wuguokai
 */
public interface Executor {

    void execute(String serviceName, String serviceVersion, String configFile) throws IOException;

}
