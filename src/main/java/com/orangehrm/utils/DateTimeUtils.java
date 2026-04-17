package com.orangehrm.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DateTimeUtils — common date/time formatting helpers used across the framework.
 *
 * <p>Usage:
 * <pre>
 *   String ts  = DateTimeUtils.getTimestamp();          // "2024-05-01_14-32-10"
 *   String date = DateTimeUtils.getCurrentDate();       // "2024-05-01"
 * </pre>
 */
public final class DateTimeUtils {

    private static final DateTimeFormatter TIMESTAMP_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter REPORT_FMT =
            DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss");

    private DateTimeUtils() {}

    /** Returns a filesystem-safe timestamp string. e.g. "2024-05-01_14-32-10" */
    public static String getTimestamp() {
        return LocalDateTime.now().format(TIMESTAMP_FMT);
    }

    /** Returns current date. e.g. "2024-05-01" */
    public static String getCurrentDate() {
        return LocalDateTime.now().format(DATE_FMT);
    }

    /** Returns current time. e.g. "14:32:10" */
    public static String getCurrentTime() {
        return LocalDateTime.now().format(TIME_FMT);
    }

    /** Returns a human-readable datetime for reports. e.g. "01 May 2024 14:32:10" */
    public static String getReadableDateTime() {
        return LocalDateTime.now().format(REPORT_FMT);
    }
}
