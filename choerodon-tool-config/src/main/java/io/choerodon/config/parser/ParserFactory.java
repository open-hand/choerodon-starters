package io.choerodon.config.parser;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import io.choerodon.config.utils.ConfigFileFormat;

/**
 * {@inheritDoc}
 * 解析器工厂，做解析器的初始化
 *
 * @author wuguokai
 */
public class ParserFactory {
    private static Map<ConfigFileFormat, Supplier<Parser>> parserFactorys = new EnumMap<>(ConfigFileFormat.class);

    private ParserFactory() {
    }

    static {
        parserFactorys.put(ConfigFileFormat.YAML, YamlParser::new);
        parserFactorys.put(ConfigFileFormat.YML, YamlParser::new);
        parserFactorys.put(ConfigFileFormat.PROPERTIES, PropertiesParser::new);
    }

    /**
     * 根据文件类型返回对应的解析器
     *
     * @param configFileFormat 文件类型格式
     * @return Parser
     */
    public static Parser getParser(ConfigFileFormat configFileFormat) {
        return Optional.ofNullable(parserFactorys.get(configFileFormat))
                .map(Supplier::get)
                .orElseThrow(() -> new IllegalArgumentException("当前仅支持yml与proprerties文件"));
    }
}
