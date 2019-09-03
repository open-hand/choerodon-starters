package io.choerodon.core.excel.domain;

import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

/**
 * @author superlee
 * @since 2019-09-03
 */
public class DataSheet {

    /**
     * 生成excel工作薄的名字
     */
    private String sheetTitle;

    /**
     * excel头标题栏与javaBean的映射关系,key: javaBean的字段，value: 自定义显示的excel头名称
     */
    private Map<String, String> propertyMap;

    /**
     * 写入的对象集合
     */
    private List data;

    private Class clazz;

    public DataSheet(String sheetTitle,
                     List data,
                     Class clazz,
                     Map<String, String> propertyMap) {
        Assert.notNull(sheetTitle, "error.sheet.title.null");
        Assert.notEmpty(propertyMap, "error.sheet.propertyMap.empty");
        Assert.notEmpty(data, "error.sheet.data.empty");
        Assert.notNull(clazz, "error.sheet.clazz.empty");
        this.sheetTitle = sheetTitle;
        this.data = data;
        this.clazz = clazz;
        this.propertyMap = propertyMap;
    }

    public String getSheetTitle() {
        return sheetTitle;
    }

    public List getData() {
        return data;
    }

    public Class getClazz() {
        return clazz;
    }

    public Map<String, String> getPropertyMap() {
        return propertyMap;
    }
}
