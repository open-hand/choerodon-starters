package io.choerodon.base.util;

/**
 * 常量基类.
 *
 * @author qiang.zeng
 * @since 2019/3/27.
 */
public interface BaseConstants {
    /**
     * 基本常量 - 是 标记.
     */
    String YES = "Y";
    /**
     * 基本常量 - 否 标记.
     */
    String NO = "N";
    /**
     * mybatis当前语言占位符.
     */
    String PLACEHOLDER_LOCALE = "#{request.locale,jdbcType=VARCHAR,javaType=java.lang.String}";
    /**
     * 重定向前缀.
     */
    String VIEW_REDIRECT = "redirect:";
    /**
     * 日期格式化（年-月-日 时:分:秒） .
     */
    String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /**
     * options 方式查询
     */
    String OPTIONS_DTO = "dto";
    String OPTIONS_CRITERIA = "criteria";
    /**
     * 系统时区.
     */
    String PREFERENCE_TIME_ZONE = "timeZone";
    /**
     * 系统语言.
     */
    String PREFERENCE_LOCALE = "locale";
    /**
     * 系统主题.
     */
    String PREFERENCE_THEME = "theme";
    /**
     * 标签页.
     */
    String PREFERENCE_NAV = "nav";
    /**
     * redis缓存前缀.
     */
    String HAP_CACHE = "hap:cache:";
    /**
     * 下划线.
     */
    String UNDER_LINE = "_";
    /**
     * 正斜杠.
     */
    String FORWARD_SLASH = "/";
}
