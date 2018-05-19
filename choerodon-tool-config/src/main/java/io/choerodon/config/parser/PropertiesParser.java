package io.choerodon.config.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * {@inheritDoc}
 * propertier配置文件的解析器
 *
 * @author wuguokai
 */
public class PropertiesParser implements Parser {

    /**
     * 把peoperties文件内容解析成map键值对集合
     *
     * @param file 配置文件
     * @return map
     * @throws IOException 文件读写异常
     */
    @Override
    public Map<String, Object> parse(File file) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(file));
        Map<String, Object> map = new LinkedHashMap<>();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            map.put((String) entry.getKey(), entry.getValue());
        }
        return map;
    }
}
