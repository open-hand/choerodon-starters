package io.choerodon.mybatis.util;

import io.choerodon.base.provider.CustomProvider;
import io.choerodon.mybatis.entity.BaseDTO;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.entity.EntityField;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
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


    /**
     * FOR INTERNAL USE ONLY
     *
     * @param parameter
     * @return
     */
    public static String getOrderByClause(Object parameter) {
        if (parameter == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(64);
        if (parameter instanceof BaseDTO) {
            String sortName = ((BaseDTO) parameter).getSortname();

            if (StringUtil.isNotEmpty(sortName)) {
                String order = ((BaseDTO) parameter).getSortorder();
                if (!("ASC".equalsIgnoreCase(order) || "DESC".equalsIgnoreCase(order) || order == null)) {
                    throw new RuntimeException("Invalid sortorder:" + order);
                }
                Set<EntityColumn> columns = EntityHelper.getColumns(parameter.getClass());
                String columnName = null;
                for (EntityColumn column: columns){
                    if(sortName.equals(column.getEntityField().getName())){
                        columnName = column.getColumn();
                    }
                }
                if (columnName == null){
                    throw new RuntimeException("Invalid sortName:" + sortName);
                }
                sb.append(columnName).append(" ");
                sb.append(order);
            } else {
                return EntityHelper.getOrderByClause(parameter.getClass());
            }
        }
        return sb.toString();
    }
}
