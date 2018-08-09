package io.choerodon.core.excel;

import io.choerodon.core.exception.CommonException;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author superlee
 */
public class ExcelExportHelper {

    /**
     * @param headers    excel的头标题栏，必须与JavaBean的字段对应，支持驼峰和下划线两种格式
     * @param list       写入的对象集合
     * @param sheetTitle 生成excel工作薄的名字
     * @param <T>        类型
     * @return
     */
    public static <T> HSSFWorkbook exportExcel2003(String[] headers, List<T> list, String sheetTitle, Class<T> clazz)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (headers == null || headers.length == 0) {
            throw new CommonException("excel headers are empty, please set headers!");
        }
        Map<String, String> propertyMap = new HashMap<>();
        for (String header : headers) {
            propertyMap.put(header, header);
        }
        HSSFWorkbook book = new HSSFWorkbook();
        sheetTitle = ExcelUtil.getSheetTitle(sheetTitle);
        HSSFSheet sheet = book.createSheet(sheetTitle);
        ExcelUtil.fillInExcel(propertyMap, list, book, sheet, clazz);
        return book;
    }

    /**
     * @param propertyMap excel头标题栏与javaBean的映射关系,key: javaBean的字段，value: 自定义显示的excel头名称
     * @param list        写入的对象集合
     * @param sheetTitle  生成excel工作薄的名字
     * @param <T>         类型
     * @return
     */
    public static <T> HSSFWorkbook exportExcel2003(Map<String, String> propertyMap, List<T> list, String sheetTitle, Class<T> clazz)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if(propertyMap == null || propertyMap.isEmpty()) {
            throw new CommonException("excel headers are empty, please set headers!");
        }
        HSSFWorkbook book = new HSSFWorkbook();
        sheetTitle = ExcelUtil.getSheetTitle(sheetTitle);
        HSSFSheet sheet = book.createSheet(sheetTitle);
        ExcelUtil.fillInExcel(propertyMap, list, book, sheet, clazz);
        return book;
    }

    /**
     * excel2003一个sheet最多65536行，大数据量的话进行分sheet处理，sheet命名规则是sheet1,sheet2依次递增
     *
     * @param headers
     * @param list
     * @param <T>
     * @return
     */
    public static <T> HSSFWorkbook exportExcel2003ForBigData(String[] headers, List<T> list) {
        return null;
    }

    /**
     * @param headers    excel的头标题栏
     * @param list       写入的对象集合
     * @param sheetTitle 生成excel工作薄的名字
     * @param <T>        类型
     * @return
     */
    public static <T> XSSFWorkbook exportExcel2007(String[] headers, List<T> list, String sheetTitle) {
        return null;
    }

    /**
     * excel2007一个sheet最多1048576行
     *
     * @param map        excel头标题栏与javaBean的映射关系
     * @param list       写入的对象集合
     * @param sheetTitle 生成excel工作薄的名字
     * @param <T>        类型
     * @return
     */
    public static <T> XSSFWorkbook exportExcel2007(Map<String, String> map, List<T> list, String sheetTitle) {
        return null;
    }
}
