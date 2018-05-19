package io.choerodon.event.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.event.EventPayload;
import io.choerodon.event.consumer.exception.CannotFindTypeReferenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * 通用util
 * @author flyleft
 */
public class CommonUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonUtils.class);

    private static ObjectMapper mapper = new ObjectMapper();

    private CommonUtils() {
    }


    public static boolean needConsumer(String[] businessTypes, String message) {
        try {
            String businessType  = mapper.readTree(message).get("businessType").toString();
            businessType = businessType.substring(1, businessType.length() - 1);
            if (Arrays.asList(businessTypes).contains(businessType)) {
                return true;
            }
        } catch (Exception e) {
            LOGGER.info("消息队列中的消息必须包含businessType字段! {}", e.toString());
        }
        return false;
    }


    public static String getBusinessType (String message) {
        String businessType = null;
        try {
             businessType = mapper.readTree(message).get("businessType").toString();
             businessType = businessType.substring(1, businessType.length() - 1);
        } catch (Exception e) {
            LOGGER.info("消息队列中的消息必须包含businessType字段! {}", e.toString());
        }
        return businessType;
    }
    /**
     * 从Exception获取异常堆栈字符串
     * @param e 异常类
     * @return 异常堆栈字符串
     */
    public static String getErrorInfoFromException(Exception e) {
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return "\r\n" + sw.toString() + "\r\n";
        } catch (Exception e2) {
            return "bad getErrorInfoFromException";
        }
    }

    public static TypeReference getTypeReference(final Method method) throws CannotFindTypeReferenceException{
        Type[] types = new Type[1];
        Type paramType = method.getGenericParameterTypes()[0];
        if (paramType instanceof ParameterizedType) {
            ParameterizedType newType = (ParameterizedType)paramType;
            types[0] = newType.getActualTypeArguments()[0];
        }
        if (types[0] == null) {
            throw new CannotFindTypeReferenceException(paramType.getTypeName());
        }
        final ParameterizedType type = ParameterizedTypeImpl.make(EventPayload.class, types, EventPayload.class.getDeclaringClass());
        return new TypeReference<EventPayload>() {
            @Override
            public Type getType() {
                return type;
            }
        };
    }

}