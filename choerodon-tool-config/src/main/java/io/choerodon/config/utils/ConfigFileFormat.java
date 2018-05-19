package io.choerodon.config.utils;

/**
 * {@inheritDoc}
 * 配置文件类型的枚举类
 *
 * @author wuguokai
 */
public enum ConfigFileFormat {
    PROPERTIES("properties"), YML("yml"), YAML("yaml");

    private String value;

    ConfigFileFormat(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * 根据配置文件的后缀返回枚举类型
     *
     * @param value 文件后缀
     * @return ConfigFileFormat
     */
    public static ConfigFileFormat fromString(String value) {
        if (value == null || value.length() == 0) {
            throw new IllegalArgumentException("value can not be empty");
        }
        switch (value) {
            case "properties":
                return PROPERTIES;
            case "yml":
                return YML;
            case "yaml":
                return YAML;
            default:
                throw new IllegalArgumentException(value + " can not map enum");
        }
    }

    /**
     * {@inheritDoc}
     * @param value 属性值
     * @return boolean
     */
    public static boolean isValidFormat(String value) {
        try {
            fromString(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
