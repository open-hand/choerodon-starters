package io.choerodon.onlyoffice.utils;


import static org.hzero.core.util.StringPool.EMPTY;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import org.hzero.core.util.TokenUtils;
import org.hzero.starter.keyencrypt.core.EncryptContext;
import org.hzero.starter.keyencrypt.core.EncryptType;
import org.hzero.starter.keyencrypt.core.IEncryptionService;
import org.springframework.util.CollectionUtils;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.exception.CommonException;

/**
 * @author zmf
 * @since 2020/7/9
 */
public final class KeyDecryptHelper {
    private static final Gson GSON = new Gson();
    public static final String COMMA = ",";
    /**
     * spring用于处理HTTP消息的ObjectMapper
     * 这个Bean由主键加密组件配置过了
     * ObjectMapper是线程安全的 (只要不是正在使用中再进行未经同步的配置操作)
     */
    private static ObjectMapper SPRINT_OBJECT_MAPPER;
    private static IEncryptionService ENCRYPTION_SERVICE;

    private KeyDecryptHelper() {
    }

    private static void ensureInitObjectMapper() {
        if (SPRINT_OBJECT_MAPPER == null) {
            // 获取容器中的 ObjectMapper, 也就是spring本身处理http message的ObjectMapper
            SPRINT_OBJECT_MAPPER = ApplicationContextHelper.getContext().getBean(ObjectMapper.class);
        }
    }

    private static void ensureEncryptService() {
        if (ENCRYPTION_SERVICE == null) {
            ENCRYPTION_SERVICE = ApplicationContextHelper.getContext().getBean(IEncryptionService.class);
        }
    }

    /**
     * 用于解密逗号分隔的加密id (用于加密时未提供key的解密过程)
     *
     * @param commaSeparatedEncryptedIds 逗号分隔的加密id
     * @return 逗号分隔的原值id
     */
    @Nullable
    public static String decryptCommaSeparatedIds(@Nullable String commaSeparatedEncryptedIds) {
        if (commaSeparatedEncryptedIds == null || !EncryptContext.isEncrypt()) {
            return commaSeparatedEncryptedIds;
        }
        ensureEncryptService();
        String[] encryptedIds = commaSeparatedEncryptedIds.split(COMMA);
        for (int i = 0; i < encryptedIds.length; i++) {
            encryptedIds[i] = ENCRYPTION_SERVICE.decrypt(encryptedIds[i], EMPTY);
        }
        return Joiner.on(COMMA).join(encryptedIds);
    }

    /**
     * 用于解密 加密id json 数组 (用于加密时未提供key的解密过程)
     *
     * @param idJsonArray 加密id json 数组
     * @return 原值id 数组 json
     */
    @Nullable
    public static String decryptJsonIds(@Nullable String idJsonArray) {
        // 如果为空或者不加密，原样返回
        if (idJsonArray == null || !EncryptContext.isEncrypt()) {
            return idJsonArray;
        }
        List<String> idStringList = GSON.fromJson(idJsonArray, new TypeToken<List<String>>() {
        }.getType());
        ensureEncryptService();
        List<Long> result = new ArrayList<>(idStringList.size());
        for (String s : idStringList) {
            result.add(Long.valueOf(ENCRYPTION_SERVICE.decrypt(s, EMPTY)));
        }
        return GSON.toJson(result);
    }

    /**
     * 解密加密id字符串的数组为Long数组
     *
     * @param ids 字符串数组
     * @return Long数据
     */
    @Nullable
    public static Long[] decryptIdArray(@Nullable String[] ids) {
        if (ids == null || ids.length == 0) {
            return null;
        }

        Long[] result = new Long[ids.length];
        if (EncryptContext.isEncrypt()) {
            ensureEncryptService();
            for (int i = 0; i < ids.length; i++) {
                result[i] = Long.parseLong(ENCRYPTION_SERVICE.decrypt(ids[i], EMPTY));
            }
        } else {
            for (int i = 0; i < ids.length; i++) {
                result[i] = Long.parseLong(ids[i]);
            }
        }
        return result;
    }

    /**
     * 解密加密id字符串的列表为Long列表
     *
     * @param ids 字符串数组
     * @return Long数据
     */
    public static List<Long> decryptIdList(List<?> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }

        List<Long> list = new ArrayList<>(ids.size());
        if (EncryptContext.isEncrypt()) {
            ensureEncryptService();
            ids.forEach(encryptedId -> list.add(Long.parseLong(ENCRYPTION_SERVICE.decrypt(String.valueOf(encryptedId), EMPTY))));
        } else {
            ids.forEach(encryptedId -> {
                if (encryptedId instanceof Long) {
                    list.add((Long) encryptedId);
                } else {
                    list.add(Long.parseLong(String.valueOf(encryptedId)));
                }
            });
        }
        return list;
    }

    /**
     * 对json进行反序列化, 同时对相应的字段进行解密
     *
     * @param json json
     * @param type 类型
     * @param <T>  泛型
     * @return 对象
     */
    public static <T> T decryptJson(String json, Class<T> type) {
        if (EncryptContext.isEncrypt()) {
            ensureInitObjectMapper();
            try {
                return SPRINT_OBJECT_MAPPER.readValue(json, type);
            } catch (IOException e) {
                throw new CommonException("error.decrypt.json", e);
            }
        } else {
            return JsonHelper.unmarshalByJackson(json, type);
        }
    }

    /**
     * 对对象进行序列化操作, 同时对相应的字段进行加密
     *
     * @param object 对象
     * @param <T>    泛型
     * @return 对字段加密后的json数据
     */
    public static <T> String encryptJson(T object) {
        if (EncryptContext.isAllowedEncrypt()) {
            ensureInitObjectMapper();
            try {
                return SPRINT_OBJECT_MAPPER.writeValueAsString(object);
            } catch (Exception ex) {
                throw new CommonException("error.encrypt.json", ex);
            }
        } else {
            EncryptContext.setEncryptType(EncryptType.TO_STRING.name());
            try {
                ensureInitObjectMapper();
                return SPRINT_OBJECT_MAPPER.writeValueAsString(object);
            } catch (JsonProcessingException e) {
                throw new CommonException("error.encrypt.json", e);
            }
        }
    }

    /**
     * 将对象转为字符串然后加密
     *
     * @param object 对象
     * @return 加密后的字符串
     */
    public static String encryptValue(Object object) {
        if (EncryptContext.isAllowedEncrypt()) {
            ensureEncryptService();
            return ENCRYPTION_SERVICE.encrypt(String.valueOf(object), EMPTY);
        } else {
            return String.valueOf(object);
        }
    }

    /**
     * 将对象转为字符串然后加密, 针对非前端来的请求的情况
     *
     * @param object 对象
     * @return 加密后的字符串
     */
    public static String encryptValueWithoutToken(Object object) {
        if (EncryptContext.isAllowedEncrypt()) {
            ensureEncryptService();
            return ENCRYPTION_SERVICE.encrypt(String.valueOf(object), EMPTY, EMPTY);
        } else {
            return String.valueOf(object);
        }
    }

    /**
     * 解密字符串
     *
     * @param object 对象
     * @return 主键
     */
    public static Long decryptValue(String object) {
        return decryptValue(object, true);
    }

    /**
     * 解密字符串
     *
     * @param object             对象
     * @param ignoreUserConflict 忽视用户的token校验
     * @return 主键
     */
    public static Long decryptValue(String object, boolean ignoreUserConflict) {
        return decryptValue(object, TokenUtils.getToken(), ignoreUserConflict);
    }

    /**
     * 解密字符串
     *
     * @param object             对象
     * @param accessToken        用户的token
     * @param ignoreUserConflict 忽视用户的token校验
     * @return 主键
     */
    public static Long decryptValue(String object, String accessToken, boolean ignoreUserConflict) {
        if (object == null) {
            return null;
        }
        ensureEncryptService();
        if (EncryptContext.isEncrypt()
                && EncryptContext.isAllowedEncrypt()
                && ENCRYPTION_SERVICE.isCipher(object)) {
            return Long.valueOf(ENCRYPTION_SERVICE.decrypt(object, EMPTY, accessToken, ignoreUserConflict));
        } else {
            return Long.valueOf(object);
        }
    }

    /**
     * 解密字符串, 如果不是加密字符串直接返回
     * 如
     * =some==   =>   123
     * some      =>   some
     *
     * @param object 对象
     * @return 主键字符串或者原非加密字符串
     */
    public static String decryptValueOrIgnore(String object) {
        if (object == null) {
            return null;
        }
        ensureEncryptService();
        if (EncryptContext.isEncrypt()
                && EncryptContext.isAllowedEncrypt()
                && ENCRYPTION_SERVICE.isCipher(object)) {
            return ENCRYPTION_SERVICE.decrypt(object, EMPTY, EMPTY, true);
        } else {
            return object;
        }
    }

    /**
     * 为websocket连接下的处理逻辑解密字符串, 如果不是加密字符串直接返回
     * 如
     * =some==   =>   123
     * some      =>   some
     *
     * @param object 对象
     * @return 主键字符串或者原非加密字符串
     */
    public static String decryptValueOrIgnoreForWs(String object) {
        if (object == null) {
            return null;
        }
        ensureEncryptService();
        // websocket 的背景下, 不能判断当前用户上下文
        if (ENCRYPTION_SERVICE.isCipher(object)) {
            try {
                // websocket没有上下文，如果要解密，那就设置上下文为要解密
                EncryptContext.setEncryptType(EncryptType.ENCRYPT.name());
                return ENCRYPTION_SERVICE.decrypt(object, EMPTY, EMPTY, true);
            } catch (Exception ex) {
                // 发生异常还是返回原值
                return object;
            } finally {
                EncryptContext.clear();
            }
        } else {
            return object;
        }
    }
}
