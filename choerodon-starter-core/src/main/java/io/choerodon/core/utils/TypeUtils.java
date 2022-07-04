package io.choerodon.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by younger on 2018/3/29.
 */
public class TypeUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(TypeUtils.class);
    public static final String SEARCH_PARAM = "searchParam";
    public static final String PARAMS = "params";

    private TypeUtils() {
    }

    /**
     * obj转string类型
     */

    public static String objToString(Object obj) {
        if (obj == null) {
            return null;
        }
        return String.valueOf(obj);
    }

    /**
     * obj转integer类型
     */

    public static Integer objToInteger(Object obj) {
        if (obj == null || "".equals(obj)) {
            return null;
        }
        return Integer.valueOf(String.valueOf(obj));
    }

    /**
     * obj转long类型
     */

    public static Long objToLong(Object obj) {
        if (obj == null || "".equals(obj)) {
            return null;
        }
        return Long.valueOf(String.valueOf(obj));
    }

    /**
     * obj转double类型
     */

    public static double objTodouble(Object obj) {
        if (obj == null || "".equals(obj)) {
            return 0;
        }
        return Double.parseDouble(String.valueOf(obj));
    }

    /**
     * obj转int类型
     */

    public static int objToInt(Object obj) {
        if (obj == null || "".equals(obj)) {
            return 0;
        }
        return Integer.parseInt(String.valueOf(obj));
    }

    /**
     * obj转boolean类型
     */

    public static Boolean objToBoolean(Object obj) {
        if (obj == null || "".equals(obj)) {
            return false;
        }
        return Boolean.valueOf(String.valueOf(obj));
    }

    /**
     * 对象转换
     *
     * @param obj obj
     * @param <T> t
     * @return t
     */
    public static <T> T cast(Object obj) {
        if (obj == null) {
            return null;
        } else {
            return (T) obj;
        }
    }

}
