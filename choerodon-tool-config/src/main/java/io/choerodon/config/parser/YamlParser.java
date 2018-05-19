package io.choerodon.config.parser;

import java.util.Map;
import java.util.Set;
import java.util.LinkedHashMap;
import java.util.List;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * {@inheritDoc}
 * yaml配置文件解析器
 *
 * @author wuguokai
 */
public class YamlParser implements Parser {
    private static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory());

    /**
     * 把yaml文件内容解析成map键值对
     *
     * @param file 配置文件
     * @return map
     * @throws IOException 文件读写异常
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> parse(File file) throws IOException {
        LinkedHashMap<String, Object> root = MAPPER.readValue(file, LinkedHashMap.class);
        return (LinkedHashMap) mapParseRecursive(root);
    }

    /**
     * 递归解析map的嵌套
     *
     * @param map map键值对集合
     * @return map
     */
    @SuppressWarnings("unchecked")
    private static Object mapParseRecursive(Map<String, Object> map) {
        Map<String, Object> res = new LinkedHashMap<>();
        Set<String> keySet = map.keySet();
        for (String key : keySet) {
            Object o = map.get(key);
            if (o instanceof Map) {
                Map tmpMap = (Map) o;
                Map kvMap = (Map) mapParseRecursive(tmpMap);
                Set<String> kvKeySet = kvMap.keySet();
                for (String kvKey : kvKeySet) {
                    res.put(key + "." + kvKey, kvMap.get(kvKey));
                }
            } else if (o instanceof List) {
                Map tmpMap = listParseRecursive((List) o);
                Set<String> tmpKeySet = tmpMap.keySet();
                for (String tmpKey : tmpKeySet) {
                    res.put(key + tmpKey, tmpMap.get(tmpKey).toString());
                }
            } else {
                Object value = o;
                res.put(key, value);
            }
        }
        return res;
    }

    /**
     * 递归解析list的嵌套
     *
     * @param list list属性集合
     * @return map
     */
    @SuppressWarnings("unchecked")
    private static Map<String, Object> listParseRecursive(List list) {
        Map<String, Object> res = new LinkedHashMap<>();

        for (int i = 0; i < list.size(); i++) {
            Object o = list.get(i);
            if (o instanceof Map) {
                Map tmpMap = (Map) mapParseRecursive((Map) o);
                Set<String> keySet = tmpMap.keySet();
                for (String kvKey : keySet) {
                    res.put("[" + i + "]." + kvKey, tmpMap.get(kvKey).toString());
                }
            } else if (o instanceof List) {
                Map tmpMap = listParseRecursive((List) o);
                Set<String> keySet = tmpMap.keySet();
                for (String key : keySet) {
                    res.put("[" + i + "]" + key, tmpMap.get(key).toString());
                }
            } else if (o != null) {
                res.put("[" + i + "]", o.toString());
            }
        }
        return res;
    }
}
