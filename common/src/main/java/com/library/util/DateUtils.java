package com.library.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    private static final DateTimeFormatter YYYMMDD_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * String("20250101") to LocalDate
     */
    public static LocalDate parseYYYYMMDD(String date) {
        return LocalDate.parse(date, YYYMMDD_FORMATTER);
    }

}
