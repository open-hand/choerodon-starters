package io.choerodon.core.excel;

import io.choerodon.core.exception.CommonException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * @author superlee
 */
public class ExcelReadHelper {

    private ExcelReadHelper() {}

    public static <T> List<T> read(File file, Class<T> clazz, Map<String, String> propertyMap)
            throws IOException, IllegalAccessException, InstantiationException, InvocationTargetException {
        String name = file.getName();
        Workbook workbook;
        if (ExcelUtil.isExcel2003(name)) {
            workbook = new HSSFWorkbook(new FileInputStream(file));
        } else if (ExcelUtil.isExcel2007(name)) {
            workbook = new XSSFWorkbook(new FileInputStream(file));
        } else {
            throw new CommonException("The file is not end with xls or xlsx");
        }
        return ExcelUtil.processExcel(workbook, clazz, propertyMap);
    }
}
