package io.choerodon.mybatis.util;

import io.choerodon.mybatis.common.CustomProvider;
import io.choerodon.mybatis.entity.MultiLanguageEntityColumn;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.entity.Example;

public class OGNL {
    public static CustomProvider customProvider = null;
    public static boolean isMultiLanguageColumn(Object example, String column){
        if(example instanceof Example){
            Class<?> entityClass = ((Example) example).getEntityClass();
            for(EntityColumn entityColumn : ((Example) example).getPropertyMap().values()){
                if(entityColumn.getColumn().equals(column) && entityColumn instanceof MultiLanguageEntityColumn && ((MultiLanguageEntityColumn) entityColumn).isMultiLanguage()){
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
}
