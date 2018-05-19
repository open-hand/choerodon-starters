package io.choerodon.config.builder;

import java.util.Map;

/**
 * 构建器接口
 *
 * @author wuguokai
 */
public interface Builder {
    /**
     * 根据kv构建指定格式的字符串
     *
     * @param kv 配置键值对集合
     * @return String
     */
    String build(Map<String, Object> kv);
}
