package io.choerodon.core.excel;

import io.choerodon.core.exception.CommonException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author superlee
 */
public class ExcelUtil {

//    private static Logger logger = LoggerFactory.getLogger(ExcelUtil.class);

    public static boolean isExcel2003(String name) {
        return name.matches("^.+\\.(?i)(xls)$");
    }

    public static boolean isExcel2007(String name) {
        return name.matches("^.+\\.(?i)(xlsx)$");
    }

    /**
     *
     * @param workbook      excel工作薄
     * @param clazz         读取数据返回的类型
     * @param propertyMap   excel列与dataObject字段的对应关系
     * @param <T>       返回类型
     * @return
     */
    public static <T> List<T> processExcel(Workbook workbook, Class<T> clazz, Map<String, String> propertyMap)
            throws InstantiationException, IllegalAccessException, InvocationTargetException {
        List<T> list = new ArrayList<>();
        Map<String, Field> fieldMap = getObjectField(clazz);
        Map<String, Method> setterMethodMap = getObjectSetterMethod(clazz);
        int sheetNum = workbook.getNumberOfSheets();
        for (int i = 0; i < sheetNum; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            if (sheet == null) {
                continue;
            }
            int lastRowNum = sheet.getLastRowNum();
            //默认一个Sheet的第一个非空行为标题行
            boolean processTitleRow = false;
            //key为excel的列，如果title为空，则改列下的所有数据跳过
            Map<Integer, String> titleRow = new HashMap<>();
            for (int rowNum = 0; rowNum < lastRowNum + 1; rowNum++) {
                Row row = sheet.getRow(rowNum);
                if(row == null) {
                    continue;
                }
                if (!processTitleRow) {
                    titleRow = getTitleRow(row);
                    processTitleRow = true;
                } else {
                    list.add(getObject(row, clazz, titleRow, fieldMap, setterMethodMap));
                }
            }
        }
        return list;
    }

    private static Map<Integer, String> getTitleRow(Row row) {
        Map<Integer, String> map = new HashMap<>();
        int lastCellNum = row.getLastCellNum();
        for (int cellNum = 0; cellNum < lastCellNum; cellNum++) {
            Cell cell = row.getCell(cellNum);
            if (cell == null) {
                continue;
            }
            int column = cell.getAddress().getColumn();
            map.put(column, cell.getStringCellValue());
        }
        return map;
    }

    public static <T> T getObject(Row row, Class<T> clazz, Map<Integer, String> titleRow,
                                  Map<String, Field> fieldMap, Map<String, Method> setterMethodMap)
            throws IllegalAccessException, InstantiationException, InvocationTargetException {
        T t = clazz.newInstance();
        int lastCellNum = row.getLastCellNum();
        for (int cellNum = 0; cellNum < lastCellNum; cellNum++) {
            Cell cell = row.getCell(cellNum);
            if (cell == null) {
                continue;
            }
            int column = cell.getAddress().getColumn();
            String cellValue = getValue(cell);
            String property = titleRow.get(column);
            if (property == null) {
                continue;
            }
            Field field = fieldMap.get(property);
            Method method = setterMethodMap.get(property);
            if (field == null) {
                throw new CommonException("There are no fields match the column : {}", column);
            }
            if (method == null) {
                throw new CommonException("There are no setter methods match the column : {}", column);
            }
            setObjectPropertyValue(t,field,method,cellValue);
        }
        return t;

    }

    /**
     * 获取object对象的所有属性，并构建map对象，对象结果为Map<'field','field'>
     * @autor:chenssy
     * @date:2014年8月10日
     *
     * @param clazz
     * 				object对象
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static Map<String, Field> getObjectField(Class clazz) {
        Field[] fields = clazz.getDeclaredFields();       //获取object对象的所有属性
        Map<String, Field> fieldMap = new HashMap<>();
        for(Field field : fields){
            String fieldName = field.getName();
            fieldMap.put(fieldName, field);
        }
        return fieldMap;
    }

    /**
     * 获取object对象所有属性的Setter方法，并构建map对象，结构为Map<'field','method'>
     * @autor:chenssy
     * @date:2014年8月9日
     *
     * @param clazz
     * 				object对象
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static Map<String, Method> getObjectSetterMethod(Class clazz) {
        Field[] fields = clazz.getDeclaredFields();       //获取object对象的所有属性
        Method[] methods = clazz.getDeclaredMethods();    //获取object对象的所有方法
        Map<String, Method> methodMap = new HashMap<>();
        for(Field field : fields){
            String fieldName = field.getName();
            for(Method method : methods){
                String methodName = method.getName();
                //匹配set方法
                if(methodName != null && "set".equals(methodName.substring(0, 3)) &&
                        Modifier.isPublic(method.getModifiers()) &&
                        ("set"+Character.toUpperCase(fieldName.charAt(0))+ fieldName.substring(1)).equals(methodName)){
                    methodMap.put(fieldName, method);       //将匹配的setter方法加入map对象中
                    break;
                }
            }
        }
        return methodMap;
    }

    @SuppressWarnings("static-access")
    public static String getValue(Cell cell) {
        if (cell.getCellType() == cell.CELL_TYPE_BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        } else if (cell.getCellType() == cell.CELL_TYPE_NUMERIC) {
            return NumberToTextConverter.toText(cell.getNumericCellValue());
        } else {
            return String.valueOf(cell.getStringCellValue());
        }
    }

    /**
     * 根据指定属性的的setter方法给object对象设置值
     * @autor:chenssy
     * @date:2014年8月10日
     *
     * @param obj
     * 			object对象
     * @param field
     * 				object对象的属性
     * @param method
     * 				object对象属性的相对应的方法
     * @param value
     * 				需要设置的值
     * @throws Exception
     */
    public static void setObjectPropertyValue(Object obj, Field field,
                                               Method method, String value) throws InvocationTargetException, IllegalAccessException {
//        Object[] oo = new Object[1];
        Object object = new Object();
        String type = field.getType().getName();
        if ("java.lang.String".equals(type) || "String".equals(type)) {
            object = value;
        } else if ("java.lang.Integer".equals(type) || "java.lang.int".equals(type) || "Integer".equals(type) || "int".equals(type)) {
            if (value.length() > 0)
                object = Integer.valueOf(value);
        } else if ("java.lang.Float".equals(type) || "java.lang.float".equals(type)  || "Float".equals(type) || "float".equals(type)) {
            if (value.length() > 0)
                object = Float.valueOf(value);
        } else if ("java.lang.Double".equals(type)  || "java.lang.double".equals(type) || "Double".equals(type) || "double".equals(type)) {
            if (value.length() > 0)
                object = Double.valueOf(value);
        } else if ("java.math.BigDecimal".equals(type)  || "BigDecimal".equals(type)) {
            if (value.length() > 0)
                object = new BigDecimal(value);
        } else if ("java.util.Date".equals(type)  || "Date".equals(type)) {
            if (value.length() > 0){
                //当长度为19(yyyy-MM-dd HH24:mm:ss)或者为14(yyyyMMddHH24mmss)时Date格式转换为yyyyMMddHH24mmss
                if(value.length() == 19 || value.length() == 14){
                    object = DateUtil.string2Date(value, "yyyyMMddHH24mmss");
                }
                else{     //其余全部转换为yyyyMMdd格式
                    object = DateUtil.string2Date(value, "yyyyMMdd");
                }
            }
        } else if ("java.sql.Timestamp".equals(type)) {
            if (value.length() > 0)
                object = DateUtil.formatDate(value, "yyyyMMddHH24mmss");
        } else if ("java.lang.Boolean".equals(type)  || "Boolean".equals(type)) {
            if (value.length() > 0)
                object = Boolean.valueOf(value);
        } else if ("java.lang.Long".equals(type) || "java.lang.long".equals(type)  || "Long".equals(type) || "long".equals(type)) {
            if (value.length() > 0)
                object = Long.valueOf(value);
        }
        method.invoke(obj, object);
    }
}
