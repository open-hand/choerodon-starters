package io.choerodon.core.saga;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class GenerateJsonExampleUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateJsonExampleUtil.class);

    private GenerateJsonExampleUtil() {
    }

    public static String generate(final Class<?> claz, final ObjectMapper mapper, final boolean emptyIsNull) {
        String value = "";
        try {
            value = mapper.writeValueAsString(createExampleInstance(claz));
        } catch (JsonProcessingException e) {
            LOGGER.trace("GenerateJsonExampleUtil jsonProcessingException {}", e.getMessage());
        }
        if (emptyIsNull && StringUtils.isEmpty(value)) {
            return null;
        }
        return value;
    }


    public static Object createExampleInstance(final Class<?> claz) {
        if (claz == null) {
            return null;
        }
        if (claz.isArray() || Collection.class.isAssignableFrom(claz)) {
            return Collections.emptyList();
        }
        try {
            Object obj = claz.newInstance();
            final List<Field> fieldList = new ArrayList<>();
            getAllFields(fieldList, claz);
            for (Field field : fieldList) {
                Class<?> type = field.getType();
                if (type.equals(Boolean.class)) {
                    setter(obj, field.getName(), true, type);
                } else if (type.equals(Byte.class)) {
                    setter(obj, field.getName(), new Byte("0"), type);
                } else if (type.equals(Short.class)) {
                    setter(obj, field.getName(), new Short("0"), type);
                } else if (type.equals(Integer.class)) {
                    setter(obj, field.getName(), 0, type);
                } else if (type.equals(Long.class)) {
                    setter(obj, field.getName(), 0L, type);
                } else if (type.equals(Float.class)) {
                    setter(obj, field.getName(), 0.0f, type);
                } else if (type.equals(Double.class)) {
                    setter(obj, field.getName(), 0.0, type);
                } else if (type.equals(String.class)) {
                    setter(obj, field.getName(), "string", type);
                } else if (!type.isPrimitive()) {
                    Object value = createExampleInstance(field.getType());
                    setter(obj, field.getName(), value, type);
                }
            }
            return obj;
        } catch (Exception e) {
            LOGGER.trace("generate json example data error when createInstance," +
                    " class {}, cause {}", claz, e.getCause());
        }
        return null;

    }

    private static void getAllFields(final List<Field> fieldList, Class<?> clazz) {
        if (clazz != null) {
            fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
            getAllFields(fieldList, clazz);
        }
    }

    private static void setter(Object obj, String att, Object value, Class<?> type) {
        try {
            Method met = obj.getClass().getMethod("set" + toUpperCaseFirstOne(att), type);    // 得到setter方法
            if (met != null) {
                met.invoke(obj, value);
            }
        } catch (Exception e) {
            LOGGER.trace("generate json example data error when invoke setter," +
                    " filed {} setValue {} cause {}", att, value, e.getCause());
        }
    }

    private static String toUpperCaseFirstOne(String s) {
        if (Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    }

}
