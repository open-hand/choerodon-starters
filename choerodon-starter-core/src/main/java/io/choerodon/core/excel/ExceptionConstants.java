package io.choerodon.core.excel;

/**
 * @author scp
 * @since 2022/9/22
 * 异常code
 */
public class ExceptionConstants {

    /**
     * 公用异常code
     */
    public static class CommonErrorCode {
        private CommonErrorCode() {
        }
        public static final String USER_NOT_LOGIN = "user.not.login";

    }


    /**
     * excel异常code
     */
    public static class ExcelErrorCode {
        private ExcelErrorCode() {
        }
        public static final String EXCEL_FILE_NOT_XLS_OR_XLSX = "file.not.xls.or.xlsx";
        public static final String EXCEL_WORKBOOK_WRITE_OUTPUT_STREAM = "workbook.write.output.stream";
        public static final String EXCEL_PROPERTYMAP_NOT_CONTAIN_CELL = "propertyMap.not.contain.cell";

        public static final String EXCEL_SHEET_TITLE_NULL = "sheet.title.null";
        public static final String EXCEL_SHEET_PROPERTYMAP_EMPTY = "sheet.propertyMap.empty";
        public static final String EXCEL_SHEET_DATA_EMPTY = "sheet.data.empty";
        public static final String EXCEL_SHEET_CLAZZ_EMPTY = "sheet.clazz.empty";
        public static final String EXCEL_EXCEL_HEADERS_EMPTY = "excel.headers.empty";
    }

}
