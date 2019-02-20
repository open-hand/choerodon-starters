package io.choerodon.liquibase.utils;

import liquibase.util.StringUtils;

import java.sql.JDBCType;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author superlee
 * @since 0.9.2
 */
public class CellDataConverter {

    /**
     * iso date formatter is yyyy-mm-dd
     * iso dateTime formatter is yyyy-mm-ddThh:mm:ss.ffffff
     */
    private static final int ISO_DATE_FORMATTER_LENGTH = 10;

    private static final String DECIMAL_POINT = ".";

    private CellDataConverter() {}

    /**
     * 根据jdbc type转为对应的java类型数据
     * @param value
     * @param type jdbc type
     * @return
     */
    public static Object covert(String value, String type) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        if (JDBCType.DATE.getName().equalsIgnoreCase(type)) {
            if (value.length() <= ISO_DATE_FORMATTER_LENGTH) {
                return LocalDate.parse(value);
            }
            return LocalDateTime.parse(value);
        }
        if (JDBCType.DECIMAL.getName().equalsIgnoreCase(type)
                || JDBCType.NUMERIC.getName().equalsIgnoreCase(type)
                || JDBCType.BIGINT.getName().equalsIgnoreCase(type)) {
            if (value.length() == 0) {
                return null;
            }
            if (value.contains(DECIMAL_POINT)) {
                return Double.parseDouble(value);
            }
            return Long.parseLong(value);
        }
        return value;

    }

}
