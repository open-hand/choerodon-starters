package io.choerodon.mybatis.util;

import io.choerodon.mybatis.common.CustomProvider;
import io.choerodon.mybatis.entity.CustomEntityColumn;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.entity.Example;

import java.util.Set;

public class OGNL {
    public static CustomProvider customProvider = null;
    public static boolean isMultiLanguageColumn(Object example, String column){
        if(example instanceof Example){
            Class<?> entityClass = ((Example) example).getEntityClass();
            for(EntityColumn entityColumn : ((Example) example).getPropertyMap().values()){
                if(entityColumn.getColumn().equals(column) && entityColumn instanceof CustomEntityColumn && ((CustomEntityColumn) entityColumn).isMultiLanguage()){
                    return true;
                }
            }
        }
        return false;
    }
    public static String language(){
        return customProvider.currentLanguage();
    }

    public static Long principal(){
        return customProvider.currentPrincipal();
    }

    public static Set<String> getSupportedLanguages(){
        return customProvider.getSupportedLanguages();
    }
}
