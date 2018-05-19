package io.choerodon.config.builder;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import io.choerodon.config.utils.ConfigFileFormat;

/**
 * 配置文件构建器工厂
 *
 * @author wuguokai
 */
public class BuilderFactory {
    private static Map<ConfigFileFormat, Supplier<Builder>> builderFactorys = new EnumMap<>(ConfigFileFormat.class);

    private BuilderFactory() {
    }

    static {
        builderFactorys.put(ConfigFileFormat.YAML, YamlBuilder::new);
        builderFactorys.put(ConfigFileFormat.YML, YamlBuilder::new);
        builderFactorys.put(ConfigFileFormat.PROPERTIES, PropertiesBuilder::new);
    }

    /**
     * 根据文件类型返回对应的构建器
     *
     * @param configFileFormat 文件枚举类型
     * @return Builder
     */
    public static Builder getBuilder(ConfigFileFormat configFileFormat) {
        return Optional.ofNullable(builderFactorys.get(configFileFormat))
                .map(Supplier::get)
                .orElseThrow(() -> new IllegalArgumentException("当前仅支持yml、proprerties文件"));
    }
}
