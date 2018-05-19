package io.choerodon.config.parser;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * 解析器接口
 *
 * @author wuguokai
 */
public interface Parser {
    /**
     * 解析文件获取配置项信息
     *
     * @param file 配置文件
     * @return 返回property-value键值对
     */
    Map<String, Object> parse(File file) throws IOException;
}
