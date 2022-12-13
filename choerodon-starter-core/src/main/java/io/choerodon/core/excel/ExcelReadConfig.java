package io.choerodon.core.excel;

import java.util.HashMap;
import java.util.Map;

public class ExcelReadConfig {
    private Map<String, String> propertyMap = new HashMap<>();
    private String[] skipSheetNames = new String[0];

    public ExcelReadConfig() {
    }

    public Map<String, String> getPropertyMap() {
        return this.propertyMap;
    }

    public void setPropertyMap(Map<String, String> propertyMap) {
        this.propertyMap = propertyMap;
    }

    public String[] getSkipSheetNames() {
        return this.skipSheetNames;
    }

    public void setSkipSheetNames(String[] skipSheetNames) {
        this.skipSheetNames = skipSheetNames;
    }
}
