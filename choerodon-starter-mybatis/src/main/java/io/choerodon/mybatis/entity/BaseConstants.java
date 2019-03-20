package io.choerodon.mybatis.entity;

import java.util.regex.Pattern;

/**
 * 常量基类.
 *
 * @author chenjingxiong
 */
public interface BaseConstants {

    String VIEW_REDIRECT = "redirect:";

    /**
     * 基本常量 - 是 标记.
     */
    String YES = "Y";

    /**
     * 基本常量 - 否 标记.
     */
    String NO = "N";

    String SYSTEM_MAX_DATE = "9999/12/31 23:59:59";

    String SYSTEM_DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";

    String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * 缓存ID
     */
    String CACHE_RESOURCE_URL = "resource_url";
    String CACHE_RESOURCE_ID = "resource_id";
    String CACHE_ROLE_CODE = "role";
    String CACHE_OAUTH_CLIENT = "oauth_client";
    String CACHE_PROMPT = "prompt";
    String CACHE_CODE = "code";
    String CACHE_FUNCTION = "function";
    String CACHE_DATA_PERMISSION_RULE = "data_permission_rule";

    /**
     * options 方式查询
     */
    String OPTIONS_DTO = "dto";
    String OPTIONS_CRITERIA = "criteria";


    String ROLE_RESOURCE_CACHE = "role_resource";

    /**
     * 默认语言.
     */
    String DEFAULT_LANG = "zh_CN";

    String PREFERENCE_TIME_ZONE = "timeZone";

    String PREFERENCE_LOCALE = "locale";

    String PREFERENCE_THEME = "theme";

    String PREFERENCE_AUTO_DELIVER = "autoDelegate";

    String PREFERENCE_START_DELIVER = "deliverStartDate";

    String PREFERENCE_END_DELIVER = "deliverEndDate";

    /**
     * SEQUENCE for oracle.<br>
     * JDBC for mysql<br>
     * IDENTITY for config
     */
    String GENERATOR_TYPE = "IDENTITY";

    String LIKE = "LIKE";

    /**
     * XMap 中属性值类型
     **/
    String XML_DATA_TYPE_FUNCTION = "fn:";
    String XML_DATA_TYPE_BOOLEAN = "boolean:";
    String XML_DATA_TYPE_INTEGER = "integer:";
    String XML_DATA_TYPE_LONG = "long:";
    String XML_DATA_TYPE_FLOAT = "float:";
    String XML_DATA_TYPE_DOUBLE = "double:";
    String XML_DATA_TYPE_DATE = "date:";


    String ERROR_CODE_SESSION_TIMEOUT = "sys_session_timeout";
    String ERROR_CODE_ACCESS_DENIED = "sys_access_denied";

    String PLACEHOLDER_LOCALE = "#{request.locale,jdbcType=VARCHAR,javaType=java.lang.String}";


    String HAP_CACHE = "hap:cache:";

    /**
     * 标签页
     */
    String PREFERENCE_NAV = "nav";

    String FORWARD_SLASH = "/";

    /**
     * 正则表达式-phone.
     */
    Pattern PATTERN_PHONE_REGEX = Pattern.compile("^1[3|4|5|8][0-9]\\d{4,8}");

    /**
     * 正则表达式-email.
     */
    Pattern PATTERN_EMAIL_REGEX = Pattern.compile("^([\\s\\S]*)+@([\\S\\s]*)+(\\.([\\S\\s]*)+)+$");

    /**
     * 正则表达式-loginName.
     */
    Pattern UESR_NAME_REGEX = Pattern.compile("^[A-Za-z0-9]{6,20}$");

    String UNDER_LINE = "_";

}
