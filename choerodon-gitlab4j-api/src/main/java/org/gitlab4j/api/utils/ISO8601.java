package org.gitlab4j.api.utils;

/*
 *   The MIT License (MIT)
 *   
 *   Copyright (c) 2017 Greg Messner <greg@messners.com>
 *   
 *   Permission is hereby granted, free of charge, to any person obtaining a copy of
 *   this software and associated documentation files (the "Software"), to deal in
 *   the Software without restriction, including without limitation the rights to
 *   use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 *   the Software, and to permit persons to whom the Software is furnished to do so,
 *   subject to the following conditions:
 *   
 *   The above copyright notice and this permission notice shall be included in all
 *   copies or substantial portions of the Software.
 *   
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 *   FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 *   COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 *   IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 *   CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.bind.DatatypeConverter;

/**
 * This class provides utility methods for parsing and formatting ISO8601 formatted dates.
 */
public class ISO8601 {
    public static final String PATTERN = "yyyy-MM-dd'T'HH:mm:ssZ";
    public static final String PATTERN_MSEC = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final String OUTPUT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String OUTPUT_MSEC_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String UTC_PATTERN = "yyyy-MM-dd HH:mm:ss 'UTC'";

    private static final SimpleDateFormat iso8601Format;
    private static final SimpleDateFormat iso8601MsecFormat;
    private static final SimpleDateFormat iso8601OutputFormat;
    private static final SimpleDateFormat iso8601OutputMsecFormat;
    private static final SimpleDateFormat iso8601UtcFormat;
    static {
        iso8601Format = new SimpleDateFormat(PATTERN);
        iso8601Format.setLenient(true);
        iso8601Format.setTimeZone(TimeZone.getTimeZone("UTC"));
        iso8601MsecFormat = new SimpleDateFormat(PATTERN_MSEC);
        iso8601MsecFormat.setLenient(true);
        iso8601MsecFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        iso8601OutputFormat = new SimpleDateFormat(OUTPUT_PATTERN);
        iso8601OutputFormat.setLenient(true);
        iso8601OutputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        iso8601OutputMsecFormat = new SimpleDateFormat(OUTPUT_MSEC_PATTERN);
        iso8601OutputMsecFormat.setLenient(true);
        iso8601OutputMsecFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        iso8601UtcFormat = new SimpleDateFormat(UTC_PATTERN);
        iso8601UtcFormat.setLenient(true);
        iso8601UtcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    /**
     * Get a ISO8601 formatted string for the current date and time.
     *
     * @return a ISO8601 formatted string for the current date and time
     */
    public static String getTimestamp() {
        return (iso8601Format.format(new Date()));
    }

    /**
     * Get a ISO8601formatted string for the current date and time.
     *
     * @param withMsec flag indicating whether to include milliseconds
     * @return a ISO8601 formatted string for the current date and time
     */
    public static String getTimestamp(boolean withMsec) {
        return (withMsec ? iso8601MsecFormat.format(new Date()) : iso8601Format.format(new Date()));
    }

    /**
     * Get a ISO8601 formatted string for the provided Calendar instance.
     * 
     * @param cal the Calendar instance to get the ISO8601 formatted string for
     * @return a ISO8601 formatted string for the provided Calendar instance, or null if call is null
     */
    public static String toString(Calendar cal) {

        if (cal == null) {
            return (null);
        }

        return (toString(cal.getTime()));
    }

    /**
     * Get a ISO8601 formatted string for the provided Date instance.
     * 
     * @param date the Date instance to get the ISO8601 formatted string for
     * @param withMsec flag indicating whether to include milliseconds
     * @return a ISO8601 formatted string for the provided Date instance, or null if date is null
     */
    public static synchronized String toString(Date date, boolean withMsec) {

        if (date == null) {
            return (null);
        }

        long time = date.getTime();
        return (withMsec && time % 1000 != 0 ? iso8601OutputMsecFormat.format(date) : iso8601OutputFormat.format(date));
    }

    /**
     * Get a ISO8601 formatted string for the provided Date instance.
     * 
     * @param date the Date instance to get the ISO8601 formatted string for
     * @return a ISO8601 formatted string for the provided Date instance, or null if date is null
     */
    public static synchronized String toString(Date date) {
        return (toString(date, true));
    }

    /**
     * Parses an ISO8601 formatted string a returns a Date instance.
     * 
     * @param dateTimeString the ISO8601 formatted string
     * @return a Date instance for the ISO8601 formatted string
     * @throws ParseException if the provided string is not in the proper format
     */
    public static Date toDate(String dateTimeString) throws ParseException {

        if (dateTimeString == null) {
            return (null);
        }

        dateTimeString = dateTimeString.trim();
        if (dateTimeString.endsWith("UTC")) {
            synchronized (iso8601UtcFormat) {
                return (iso8601UtcFormat.parse(dateTimeString));
            }
        } else {
            Calendar cal = DatatypeConverter.parseDateTime(dateTimeString);
            return (cal.getTime());
        }
    }

    /**
     * Parses an ISO8601 formatted string a returns a Calendar instance.
     * 
     * @param dateTimeString the ISO8601 formatted string
     * @return a Calendar instance for the ISO8601 formatted string
     * @throws ParseException if the provided string is not in the proper format
     */
    public static Calendar toCalendar(String dateTimeString) throws ParseException {

        Date date = toDate(dateTimeString);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return (cal);
    }
}
