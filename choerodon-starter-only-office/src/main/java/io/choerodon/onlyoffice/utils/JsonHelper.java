package io.choerodon.onlyoffice.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.text.SimpleDateFormat;
import org.hzero.core.base.BaseConstants;
import org.springframework.util.Assert;

import io.choerodon.core.exception.CommonException;

/**
 * @author zmf
 * @since 20-5-8
 */
public final class JsonHelper {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private JsonHelper() {
    }

    static {
        OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        OBJECT_MAPPER.setDateFormat(new SimpleDateFormat(BaseConstants.Pattern.DATETIME));
        // 不区分属性的大小写 比如Target 转换为target
        OBJECT_MAPPER.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES,true);
    }

    /**
     * 通过jackson反序列化对象
     *
     * @param json json内容
     * @param type 类型
     * @param <T>  泛型
     * @return 对象
     */
    public static <T> T unmarshalByJackson(String json, Class<T> type) {
        Assert.hasLength(json, "JSON to be unmarshalled should not be empty");
        Assert.notNull(type, "Type should not be null");
        try {
            return OBJECT_MAPPER.readValue(json, type);
        } catch (IOException e) {
            throw new CommonException("Failed to unmarshal by jackson. It's unexpected and may be an internal error. The json is: " + json, e);
        }
    }

    /**
     * 通过jackson反序列化对象
     *
     * @param json          json内容
     * @param typeReference 类型
     * @param <T>           泛型
     * @return 对象
     */
    public static <T> T unmarshalByJackson(String json, TypeReference<T> typeReference) {
        Assert.hasLength(json, "JSON to be unmarshalled should not be empty");
        Assert.notNull(typeReference, "Type should not be null");
        try {
            return OBJECT_MAPPER.readValue(json, typeReference);
        } catch (IOException e) {
            throw new CommonException("Failed to unmarshal by jackson. It's unexpected and may be an internal error. The json is: " + json, e);
        }
    }

    /**
     * 通过jackson序列化对象
     *
     * @param object 非空对象
     * @return JSON字符串
     */
    public static String marshalByJackson(Object object) {
        Assert.notNull(object, "Object to be marshaled should not be null");
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (IOException e) {
            throw new CommonException("Failed to marshal by jackson. It's unexpected and may be an internal error. The object is: " + object.toString(), e);
        }
    }

    /**
     * 将json中的双引号替换为单引号 (不管属性值包括双引号的情况)
     *
     * @param json json
     * @return 单引号的json
     */
    public static String singleQuoteWrapped(String json) {
        return json.replaceAll("\"", "'");
    }
}
