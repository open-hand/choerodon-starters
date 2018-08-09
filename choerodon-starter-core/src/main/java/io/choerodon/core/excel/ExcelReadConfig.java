package io.choerodon.core.excel;

import java.util.HashMap;
import java.util.Map;

/**
 * @author superlee
 */
public class ExcelReadConfig {


    /**
     * excel列名与类字段的映射关系，map为空则excel列名要与对象字段名一致
     * key: excel中自定义的列名
     * value: JavaBean中对应的字段名，支持驼峰和下划线两种形式
     */
    private Map<String, String> propertyMap = new HashMap<>();
    /**
     * 读取excel时跳过的sheet name
     */
    private String[] skipSheetNames = {};

    public Map<String, String> getPropertyMap() {
        return propertyMap;
    }

    public void setPropertyMap(Map<String, String> propertyMap) {
        this.propertyMap = propertyMap;
    }

    public String[] getSkipSheetNames() {
        return skipSheetNames;
    }

    public void setSkipSheetNames(String[] skipSheetNames) {
        this.skipSheetNames = skipSheetNames;
    }
}
