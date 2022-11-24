package io.choerodon.core.excel;

import java.util.List;
import java.util.Map;

import org.springframework.util.Assert;


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
        Assert.notNull(sheetTitle, ExceptionConstants.ExcelErrorCode.EXCEL_SHEET_TITLE_NULL);
        Assert.notEmpty(propertyMap, ExceptionConstants.ExcelErrorCode.EXCEL_SHEET_PROPERTYMAP_EMPTY);
        Assert.notEmpty(data, ExceptionConstants.ExcelErrorCode.EXCEL_SHEET_DATA_EMPTY);
        Assert.notNull(clazz, ExceptionConstants.ExcelErrorCode.EXCEL_SHEET_CLAZZ_EMPTY);
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
