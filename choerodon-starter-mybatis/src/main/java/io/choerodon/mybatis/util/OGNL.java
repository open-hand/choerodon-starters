package io.choerodon.mybatis.util;

import io.choerodon.mybatis.common.CustomProvider;
import io.choerodon.mybatis.entity.CustomEntityColumn;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.entity.Example;

import java.util.Set;

public class OGNL {
    public static CustomProvider customProvider = null;
    public static String language(){
        if (customProvider == null){
            return "zh_CN";
        }
        return customProvider.currentLanguage();
    }
    public static Long principal(){
        if (customProvider == null){
            return -1L;
        }
        return customProvider.currentPrincipal();
    }
    public static Set<String> getSupportedLanguages(){
        return customProvider.getSupportedLanguages();
    }
}
