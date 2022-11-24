package io.choerodon.core.excel;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author superlee
 */
public class DateUtil {

    private DateUtil() {
    }

    private static Logger logger = LoggerFactory.getLogger(DateUtil.class);

    /**
     * 将字符串(格式符合规范)转换成Date
     *
     * @param value  需要转换的字符串
     * @param format 日期格式
     * @return Date
     */
    public static Date string2Date(String value, String format) {
        if (value == null || "".equals(value)) {
            return null;
        }
        SimpleDateFormat sdf = getFormat(format);
        Date date = null;
        value = formatDate(value, format);
        try {
            date = sdf.parse(value);
        } catch (ParseException e) {
            logger.info("SimpleDateFormat parse exception: {}", e.getMessage());
        }
        return date;
    }

    /**
     * 获取日期显示格式，为空默认为yyyy-mm-dd HH:mm:ss
     *
     * @param format 日期格式
     * @return SimpleDateFormat
     */
    protected static SimpleDateFormat getFormat(String format) {
        if (format == null || "".equals(format)) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        return new SimpleDateFormat(format);
    }

    public static String formatDate(String date, String format) {
        if (date == null || "".equals(date)) {
            return "";
        }
        Date dt;
        SimpleDateFormat inFmt;
        SimpleDateFormat outFmt;
        ParsePosition pos = new ParsePosition(0);
        date = date.replace("-", "").replace(":", "");
        if (date == null || "".equals(date.trim()))
            return "";
        try {
            if (Long.parseLong(date) == 0L)
                return "";
        } catch (Exception nume) {
            return date;
        }
        try {
            switch (date.trim().length()) {
                case 14:
                    inFmt = new SimpleDateFormat("yyyyMMddHHmmss");
                    break;
                case 12:
                    inFmt = new SimpleDateFormat("yyyyMMddHHmm");
                    break;
                case 10:
                    inFmt = new SimpleDateFormat("yyyyMMddHH");
                    break;
                case 8:
                    inFmt = new SimpleDateFormat("yyyyMMdd");
                    break;
                case 6:
                    inFmt = new SimpleDateFormat("yyyyMM");
                    break;
                case 7:
                case 9:
                case 11:
                case 13:
                default:
                    return date;
            }
            if ((dt = inFmt.parse(date, pos)) == null)
                return date;
            if (format == null || "".equals(format.trim())) {
                outFmt = new SimpleDateFormat("yyyy年MM月dd日");
            } else {
                outFmt = new SimpleDateFormat(format);
            }
            return outFmt.format(dt);
        } catch (Exception ex) {
            logger.info(ex.getMessage());
        }
        return date;
    }
}
