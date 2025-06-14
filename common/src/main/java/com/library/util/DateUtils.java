package com.library.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    private static final DateTimeFormatter YYYMMDD_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * String("20250101") to LocalDate
     */
    public static LocalDate parseYYYYMMDD(String date) {
        return LocalDate.parse(date, YYYMMDD_FORMATTER);
    }

    /**
     * String("2011-12-03T10:15:30+01:00") to LocalDate
     */
    public static LocalDateTime parseOffsetDateTime(String datetime) {
        return LocalDateTime.parse(datetime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

}
