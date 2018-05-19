package io.choerodon.mybatis.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

/**
 * Created by xausky on 4/7/17.
 */
public class LanguageHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(LanguageHelper.class);
    private static ThreadLocal<String> languages = new ThreadLocal<>();

    private LanguageHelper() {

    }

    /**
     * 根据当前登陆用户获取语言信息
     *
     * @return String
     */
    public static String language() {
        CustomUserDetails details = DetailsHelper.getUserDetails();
        if (details != null) {
            language(details.getLanguage());
        } else {
            if (languages.get() == null) {
                language("zh_CN");
                LOGGER.warn("principal not instanceof CustomUserDetails language is zh_CN");
            }
        }
        return languages.get();
    }

    public static void language(String lang) {
        languages.set(lang);
    }
}
