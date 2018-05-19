package io.choerodon.swagger.custom.extra;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wuguokai
 */
public class ExtraData {

    public static final String EXTRA_DATA_KEY = "extraData";

    public static final String ZUUL_ROUTE_DATA = "choerodon_route";

    private Map<String, Object> data;

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public void put(String key, Object o) {
        if (this.data == null) {
            this.data = new HashMap<>();
        }
        this.data.put(key, o);
    }

    @Override
    public String toString() {
        return "ExtraData{" +
                "data=" + data +
                '}';
    }
}
