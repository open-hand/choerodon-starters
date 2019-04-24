package io.choerodon.mybatis.util;

import io.choerodon.base.provider.CustomProvider;
import tk.mybatis.mapper.util.StringUtil;

import java.util.Set;

public class OGNL {
    public static CustomProvider customProvider = null;

    public static String language() {
        if (customProvider == null) {
            return "zh_CN";
        }
        return customProvider.currentLanguage();
    }

    public static Long principal() {
        if (customProvider == null) {
            return null;
        }
        return customProvider.currentPrincipal();
    }

    public static Set<String> getSupportedLanguages() {
        return customProvider.getSupportedLanguages();
    }

    /**
     * convert camel hump to under line case.
     */
    public static String camelHumpToUnderline(String str) {
        return StringUtil.camelhumpToUnderline(str);
    }
}
