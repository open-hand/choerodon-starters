package io.choerodon.config.utils;

/**
 * 服务类型枚举类
 *
 * @author wuguokai
 */
public enum ServiceType {

    DEAFAULT("default"), API_GATEWAY("api-gateway");

    private String value;

    ServiceType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * 判断服务类型
     *
     * @param value 服务名
     * @return 服务类型
     */
    public static ServiceType fromString(int value) {
        if (value == 0) {
            return API_GATEWAY;
        }
        return DEAFAULT;
    }
}
