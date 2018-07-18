package io.choerodon.core.excel;

import io.choerodon.core.exception.CommonException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

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

    /**
     *
     * @param file                          excel文件
     * @param clazz                         excel数据要转换的类型
     * @param propertyMap                   excel列名与类字段的映射关系，传null则excel列名要与对象字段名一致
     * @param <T>                           类型
     * @throws IOException                  IOException
     * @throws IllegalAccessException       IllegalAccessException
     * @throws InstantiationException       InstantiationException
     * @throws InvocationTargetException    InvocationTargetException
     * @return list
     */
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

    /**
     *
     * @param multipartFile                 excel文件
     * @param clazz                         excel数据要转换的类型
     * @param propertyMap                   excel列名与类字段的映射关系，传null则excel列名要与对象字段名一致
     * @param <T>                           类型
     * @throws IOException                  IOException
     * @throws IllegalAccessException       IllegalAccessException
     * @throws InstantiationException       InstantiationException
     * @throws InvocationTargetException    InvocationTargetException
     * @return list
     */
    public static <T> List<T> read(MultipartFile multipartFile, Class<T> clazz, Map<String, String> propertyMap)
            throws IOException, IllegalAccessException, InstantiationException, InvocationTargetException {
        String name = multipartFile.getOriginalFilename();
        Workbook workbook;
        if (ExcelUtil.isExcel2003(name)) {
            workbook = new HSSFWorkbook(multipartFile.getInputStream());
        } else if (ExcelUtil.isExcel2007(name)) {
            workbook = new XSSFWorkbook(multipartFile.getInputStream());
        } else {
            throw new CommonException("The file is not end with xls or xlsx");
        }
        return ExcelUtil.processExcel(workbook, clazz, propertyMap);
    }
}
