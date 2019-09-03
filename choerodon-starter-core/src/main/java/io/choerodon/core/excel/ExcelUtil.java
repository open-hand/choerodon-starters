package io.choerodon.core.excel;

import io.choerodon.core.excel.domain.DataSheet;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author superlee
 */
public class ExcelUtil {

    private ExcelUtil() {
    }

    private static Logger logger = LoggerFactory.getLogger(ExcelUtil.class);

    public static boolean isExcel2003(String name) {
        return name.matches("^.+\\.(?i)(xls)$");
    }

    public static boolean isExcel2007(String name) {
        return name.matches("^.+\\.(?i)(xlsx)$");
    }

    private static final String DEFAULT_SHEET_NAME = "sheet";

    private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH24mmss";

    /**
     * @param workbook        excel工作薄
     * @param clazz           读取数据返回的类型
     * @param excelReadConfig excel读取的配置项
     * @param <T>             类型
     * @return list
     * @throws InstantiationException    InstantiationException
     * @throws IllegalAccessException    IllegalAccessException
     * @throws InvocationTargetException InvocationTargetException
     */
    public static <T> List<T> processExcel(Workbook workbook, Class<T> clazz, ExcelReadConfig excelReadConfig)
            throws InstantiationException, IllegalAccessException, InvocationTargetException {
        List<T> list = new ArrayList<>();
        Map<String, Field> fieldMap = getObjectField(clazz);
        Map<String, Method> setterMethodMap = getObjectSetterMethod(clazz);
        int sheetNum = workbook.getNumberOfSheets();
        Map<String, String> propertyMap = excelReadConfig.getPropertyMap();
        String[] skipSheetNames = excelReadConfig.getSkipSheetNames();
        for (int i = 0; i < sheetNum; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            if (sheet == null
                    || containSkipSheetName(sheet.getSheetName(), skipSheetNames)) {
                continue;
            }
            int lastRowNum = sheet.getLastRowNum();
            //默认一个Sheet的第一个非空行为标题行
            boolean processTitleRow = false;
            //key为excel的列，如果title为空，则改列下的所有数据跳过
            Map<Integer, String> titleRow = new HashMap<>();
            for (int rowNum = 0; rowNum < lastRowNum + 1; rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null) {
                    continue;
                }
                if (!processTitleRow) {
                    titleRow = getTitleRow(row, propertyMap);
                    processTitleRow = true;
                } else {
                    Optional.ofNullable(getObject(row, clazz, titleRow, fieldMap, setterMethodMap)).ifPresent(t -> list.add(t));
                }
            }
        }
        return list;
    }

    private static boolean containSkipSheetName(String sheetName, String[] skipSheetNames) {
        for (String skip : skipSheetNames) {
            if (sheetName.equalsIgnoreCase(skip)) {
                return true;
            }
        }
        return false;
    }

    /**
     * propertyMap为空用默认策略
     *
     * @param row         excel的行
     * @param propertyMap excel列名与dataObject字段的对应关系
     * @return
     */
    private static Map<Integer, String> getTitleRow(Row row, Map<String, String> propertyMap) {
        Map<Integer, String> map = new HashMap<>();
        int lastCellNum = row.getLastCellNum();
        for (int cellNum = 0; cellNum < lastCellNum; cellNum++) {
            Cell cell = row.getCell(cellNum);
            if (cell == null) {
                continue;
            }
            int column = cell.getAddress().getColumn();
            String cellValue = cell.getStringCellValue();
            //propertyMap为空，使用默认策略，即标题行要与对象字段名相对应，驼峰风格
            if (propertyMap.isEmpty()) {
                map.put(column, cellValue);
            } else {
                //遍历map，找到excel的标题行与对象字段的映射关系，如果propertyMap里面不包含excel里面的列名，抛异常
                if (!propertyMap.keySet().contains(cellValue)) {
                    throw new IllegalArgumentException("propertyMap does not contain the cell value : " + cellValue);
                }
                propertyMap.forEach((k, v) -> {
                    if (k.equals(cellValue)) {
                        //如果是下划线，则转为驼峰
                        map.put(column, underlineToCamelhump(v));
                    }
                });
            }
        }
        return map;
    }

    public static <T> T getObject(Row row, Class<T> clazz, Map<Integer, String> titleRow,
                                  Map<String, Field> fieldMap, Map<String, Method> setterMethodMap)
            throws IllegalAccessException, InstantiationException, InvocationTargetException {
        T t = clazz.newInstance();
        int lastCellNum = row.getLastCellNum();
        boolean cellAllEmpty = true;
        Set<Integer> headerColumnSet = titleRow.keySet();
        for (int cellNum = 0; cellNum < lastCellNum; cellNum++) {
            Cell cell = row.getCell(cellNum);
            if (cell == null) {
                continue;
            }
            int column = cell.getAddress().getColumn();
            String cellValue = getValue(cell);
            //这里反射的性能消耗大还是另外起一个for循环性能消耗大，代研究
            if (!StringUtils.isEmpty(cellValue) && headerColumnSet.contains(column)) {
                cellAllEmpty = false;
            }
            String property = titleRow.get(column);
            if (property == null) {
                continue;
            }
            Field field = fieldMap.get(property);
            if (field == null) {
                throw new IllegalArgumentException("excel column name can not match the fields of object, column : "
                        + column + ", please make sure the excel title and JavaBean field is mapped or set the custom propertyMap");
            }
            Method method = setterMethodMap.get(property);
            if (method == null) {
                throw new IllegalArgumentException("excel column name can not match the setter methods of object, column : "
                        + column + ", please make sure the field has a setter method");
            }
            setObjectPropertyValue(t, field, method, cellValue);
        }
        if (cellAllEmpty) {
            return null;
        } else {
            return t;
        }
    }

    /**
     * 获取object对象的所有属性，并构建map对象，对象结果为Map
     *
     * @param clazz object对象
     * @return map
     */
    @SuppressWarnings("rawtypes")
    public static Map<String, Field> getObjectField(Class clazz) {
        Field[] fields = clazz.getDeclaredFields();       //获取object对象的所有属性
        Map<String, Field> fieldMap = new HashMap<>();
        for (Field field : fields) {
            String fieldName = field.getName();
            fieldMap.put(fieldName, field);
        }
        return fieldMap;
    }

    /**
     * 获取object对象所有属性的Setter方法，并构建map对象，结构为Map
     *
     * @param clazz object对象
     * @return map
     */
    @SuppressWarnings("rawtypes")
    public static Map<String, Method> getObjectSetterMethod(Class clazz) {
        Field[] fields = clazz.getDeclaredFields();       //获取object对象的所有属性
        Method[] methods = clazz.getDeclaredMethods();    //获取object对象的所有方法
        Map<String, Method> methodMap = new HashMap<>();
        for (Field field : fields) {
            String fieldName = field.getName();
            for (Method method : methods) {
                String methodName = method.getName();
                //匹配set方法
                if (methodName != null && "set".equals(methodName.substring(0, 3)) &&
                        Modifier.isPublic(method.getModifiers()) &&
                        ("set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1)).equals(methodName)) {
                    methodMap.put(fieldName, method);       //将匹配的setter方法加入map对象中
                    break;
                }
            }
        }
        return methodMap;
    }

    /**
     * @param cell excel单元格
     * @return 单元格的值
     */
    @SuppressWarnings("static-access")
    public static String getValue(Cell cell) {
        if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            return NumberToTextConverter.toText(cell.getNumericCellValue());
        } else {
            return String.valueOf(cell.getStringCellValue());
        }
    }

    /**
     * 根据指定属性的的setter方法给object对象设置值
     *
     * @param obj    object对象
     * @param field  object对象的属性
     * @param method object对象属性的相对应的方法
     * @param value  需要设置的值
     * @throws InvocationTargetException InvocationTargetException
     * @throws IllegalAccessException    IllegalAccessException
     */
    public static void setObjectPropertyValue(Object obj, Field field,
                                              Method method, String value) throws InvocationTargetException, IllegalAccessException {
        Object object = new Object();
        String type = field.getType().getName();
        if ("java.lang.String".equals(type) || "String".equals(type)) {
            object = value;
        } else if ("java.lang.Integer".equals(type) || "java.lang.int".equals(type) || "Integer".equals(type) || "int".equals(type)) {
            if (value.length() > 0)
                object = Integer.valueOf(value);
        } else if ("java.lang.Float".equals(type) || "java.lang.float".equals(type) || "Float".equals(type) || "float".equals(type)) {
            if (value.length() > 0)
                object = Float.valueOf(value);
        } else if ("java.lang.Double".equals(type) || "java.lang.double".equals(type) || "Double".equals(type) || "double".equals(type)) {
            if (value.length() > 0)
                object = Double.valueOf(value);
        } else if ("java.math.BigDecimal".equals(type) || "BigDecimal".equals(type)) {
            if (value.length() > 0)
                object = new BigDecimal(value);
        } else if ("java.util.Date".equals(type) || "Date".equals(type)) {
            if (value.length() > 0) {
                //当长度为19(yyyy-MM-dd HH24:mm:ss)或者为14(yyyyMMddHH24mmss)时Date格式转换为yyyyMMddHH24mmss
                if (value.length() == 19 || value.length() == 14) {
                    object = DateUtil.string2Date(value, "yyyyMMddHH24mmss");
                } else {     //其余全部转换为yyyyMMdd格式
                    object = DateUtil.string2Date(value, "yyyyMMdd");
                }
            }
        } else if ("java.sql.Timestamp".equals(type)) {
            if (value.length() > 0)
                object = DateUtil.formatDate(value, "yyyyMMddHH24mmss");
        } else if ("java.lang.Boolean".equals(type) || "Boolean".equals(type)) {
            //布尔类型
            if (value.length() > 0)
                object = Boolean.valueOf(value);
        } else if ("java.lang.Long".equals(type) || "java.lang.long".equals(type) || "Long".equals(type) || "long".equals(type)) {
            if (value.length() > 0)
                object = Long.valueOf(value);
        }
        method.invoke(obj, object);
    }

    /**
     * 返回sheet名字，sheetTitle为空则返回sheet
     *
     * @param sheetTitle sheet标题名
     * @return
     */
    public static String getSheetTitle(String sheetTitle) {
        return StringUtils.isEmpty(sheetTitle) ? DEFAULT_SHEET_NAME : sheetTitle;
    }

    /**
     * list中数据填充到excel中，headers字段要和javaBean的字段对应，否则写不进去值，headers是驼峰或下划线
     *
     * @param propertyMap key:JavaBean的字段名， value: 自定义的excel标题头
     * @param list
     * @param book
     * @param sheet
     * @param <T>
     */
    public static <T> void fillInExcel2003(Map<String, String> propertyMap, List<T> list, HSSFWorkbook book, HSSFSheet sheet, Class<T> clazz)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        //设置列头样式(居中、变粗、蓝色)
        HSSFCellStyle headerStyle = book.createCellStyle();
        setHeaderStyle(headerStyle, book);
        // 设置单元格样式
        HSSFCellStyle cellStyle = book.createCellStyle();
        setCellStyle(cellStyle, book);
        // 创建头部
        Map<String, Integer> headerRow = createHeader(sheet, headerStyle, propertyMap);
        processCell(list, sheet, clazz, cellStyle, headerRow);
    }

    public static void fillInExcel2007(XSSFWorkbook workbook, List<DataSheet> dataSheets) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        CellStyle headerStyle = workbook.createCellStyle();
        setHeaderStyle(headerStyle, workbook);
        CellStyle cellStyle = workbook.createCellStyle();
        setCellStyle(cellStyle, workbook);
        for (DataSheet ds : dataSheets) {
            String title = ds.getSheetTitle();
            Sheet sheet = workbook.createSheet(title);
            Map<String, Integer> headerRow = createHeader(sheet, headerStyle, ds.getPropertyMap());
            processCell(ds.getData(), sheet, ds.getClazz(), cellStyle, headerRow);
        }
    }


    private static <T> void processCell(List<T> list, Sheet sheet, Class<T> clazz, CellStyle cellStyle, Map<String, Integer> headerRow) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Map<String, Field> fieldMap = getObjectField(clazz);
        //第0行已初始化header，从第一行开始
        int rowNum = 1;
        Row row;
        String title;
        Integer column;
        Field field;
        String fieldName;
        String getterMethodName;
        Method getterMethod;
        Object value;
        Cell cell;
        for (T t : list) {
            //创建一行
            row = sheet.createRow(rowNum++);
            for (Map.Entry<String, Integer> entry : headerRow.entrySet()) {
                //下划线转驼峰，如果是驼峰，无影响
                title = underlineToCamelhump(entry.getKey());
                column = entry.getValue();
                cell = row.createCell(column);
                cell.setCellStyle(cellStyle);
                field = fieldMap.get(title);
                if (field == null) {
                    throw new IllegalArgumentException("export excel headers are wrong, error one: " + title);
                }
                fieldName = field.getName();
                getterMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                try {
                    getterMethod = clazz.getMethod(getterMethodName, new Class[]{});
                    value = getterMethod.invoke(t, new Object[]{});
                    fillInCell(row, column, value, sheet, cell);
                } catch (NoSuchMethodException e) {
                    logger.info("can not get the method {} by reflection", getterMethodName);
                    throw e;
                } catch (IllegalAccessException e) {
                    logger.info("illegal access for the method {}", getterMethodName);
                    throw e;
                } catch (InvocationTargetException e) {
                    logger.info("invoke failed for the method {}", getterMethodName);
                    throw e;
                }
            }
        }
    }

    private static void fillInCell(Row row, Integer column, Object value, Sheet sheet, Cell cell) {
        String cellValue = "";
        if (value instanceof Date) {
            //处理日期格式
            Date date = (Date) value;
            SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_PATTERN);
            cellValue = sdf.format(date);
        } else if (value instanceof byte[]) {
            //处理图片

        } else if (value != null) {
            //其他的按字符串处理
            cellValue = String.valueOf(value);
        }
        Pattern p = Pattern.compile("^//d+(//.//d+)?$");
        Matcher matcher = p.matcher(cellValue);

        //设置单元格宽度，是文字能够全部显示
        sheet.setColumnWidth(column, (cellValue.length() + 6) * 256);
        row.setHeightInPoints((short) (20));   //设置单元格高度
        if (matcher.matches()) {
            // 是数字当作double处理
            cell.setCellValue(Double.parseDouble(cellValue));
        } else {
            cell.setCellValue(cellValue);
        }
    }

    /**
     * 将下划线风格替换为驼峰风格
     *
     * @param str str
     * @return String string
     */
    public static String underlineToCamelhump(String str) {
        Matcher matcher = Pattern.compile("_[a-z]").matcher(str);
        StringBuilder builder = new StringBuilder(str);
        for (int i = 0; matcher.find(); i++) {
            builder.replace(matcher.start() - i, matcher.end() - i, matcher.group().substring(1).toUpperCase());
        }
        if (Character.isUpperCase(builder.charAt(0))) {
            builder.replace(0, 1, String.valueOf(Character.toLowerCase(builder.charAt(0))));
        }
        return builder.toString();
    }

    /**
     * 根据头部样式、头部数据创建Excel头部
     *
     * @param sheet       sheet
     * @param headerStyle 头部样式
     * @param propertyMap key:JavaBean的字段名， value: 自定义的excel标题头
     * @return 设置完成的头部Row
     * @author chenssy
     * @date 2014年6月17日 上午11:37:28
     * @version 1.0
     */
    private static Map<String, Integer> createHeader(Sheet sheet, CellStyle headerStyle,
                                                     Map<String, String> propertyMap) {
        Map<String, Integer> headerMap = new HashMap<>();
        Row headRow = sheet.createRow(0);
        headRow.setHeightInPoints((short) (20));   //设置头部高度
        //添加数据
        Cell cell;
        int i = 0;
        for (Map.Entry<String, String> entry : propertyMap.entrySet()) {
            String column = entry.getKey();
            String title = entry.getValue();
            cell = headRow.createCell(i);
            cell.setCellStyle(headerStyle);
            RichTextString text;
            if (cell instanceof HSSFCell) {
                text = new HSSFRichTextString(title);
            } else {
                text = new XSSFRichTextString(title);
            }
            cell.setCellValue(text);
            headerMap.put(column, i++);
        }
        return headerMap;
    }

    /**
     * 设置单元格样式
     *
     * @param cellStyle 单元格样式
     * @param book      book HSSFWorkbook对象
     * @author chenssy
     * @date 2014年6月17日 上午11:00:53
     * @version 1.0
     */
    private static void setCellStyle(CellStyle cellStyle, Workbook book) {
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);   //水平居中
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直居中

        Font font = book.createFont();
        font.setFontHeightInPoints((short) 12);

        cellStyle.setFont(font);
    }

    /**
     * 设置Excel图片的格式：字体居中、变粗、蓝色、12号
     *
     * @param headerStyle 头部样式
     * @param book        生产的excel book 	 HSSFWorkbook对象
     * @author chenssy
     * @date 2014年6月16日 下午8:46:49
     * @version 1.0
     */
    private static void setHeaderStyle(CellStyle headerStyle, Workbook book) {
        headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);   //水平居中
        headerStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直居中
        //设置字体
        Font font = book.createFont();
        font.setFontHeightInPoints((short) 12);     //字号：12号
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);   //变粗
        font.setColor(HSSFColor.BLUE.index);   //蓝色

        headerStyle.setFont(font);
    }
}
