package com.pinkproject._core.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Formatter {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("dd");
    private static final DateTimeFormatter YEAR_FORMATTER = DateTimeFormatter.ofPattern("yyyy");
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("M"); // 월 앞의 0 제거
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm"); // 시간과 분만 포함

    // LocalDateTime을 yyyy-MM-dd 형식의 문자열로 변환
    public static String formatDate(LocalDateTime date) {
        return date.format(DATE_FORMATTER);
    }

    // Timestamp를 yyyy-MM-dd HH:mm:ss 형식의 문자열로 변환
    public static String formatTimestamp(Timestamp timestamp) {
        LocalDateTime localDateTime = timestamp.toLocalDateTime();
        return localDateTime.format(TIMESTAMP_FORMATTER);
    }

    // 세 자리마다 콤마를 찍고 원 단위를 붙인 문자열로 변환
    public static String number(int number) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###원");
        return decimalFormat.format(number);
    }

    // 문자열을 정수로 변환
    public static int parseNumber(String numberStr) {
        try {
            return Integer.parseInt(numberStr.replace(",", "").replace("원", "").trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format: " + numberStr, e);
        }
    }

    // 문자열을 실수로 변환
    public static double parseDouble(String numberStr) {
        try {
            return Double.parseDouble(numberStr.replace(",", "").replace("원", "").trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format: " + numberStr, e);
        }
    }

    // 값을 소수점 한 자리까지 반올림
    public static double roundToOneDecimalPlace(double value) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(1, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    // LocalDateTime에서 날짜를 yyyy-MM-dd 형식의 문자열로 반환
    public static String formatCreatedAtDate(LocalDateTime createdAt) {
        return createdAt != null ? createdAt.toLocalDate().toString() : null;
    }

    // LocalDateTime에서 시간을 오전/오후 형식으로 반환
    public static String formatCreatedAtPeriodWithTime(LocalDateTime createdAt) {
        if (createdAt != null) {
            LocalTime time = createdAt.toLocalTime();
            String period = time.isBefore(LocalTime.NOON) ? "오전" : "오후";
            return period + " " + time.format(TIME_FORMATTER);
        }
        return null;
    }

    // LocalDate에서 "일" 형식만 반환
    public static String formatDayOnly(LocalDate date) {
        return date != null ? date.format(DAY_FORMATTER) + "일" : null;
    }

    // LocalDate에서 연도 형식 반환 (년 추가)
    public static String formatYearWithSuffix(LocalDate date) {
        return date.format(YEAR_FORMATTER) + "년";
    }

    // LocalDate에서 월 형식 반환 (월 추가)
    public static String formatMonthWithSuffix(LocalDate date) {
        return date.format(MONTH_FORMATTER) + "월";
    }

    // 숫자를 세 자리마다 콤마로 구분하여 문자열로 반환
    public static String formatNumberWithComma(int number) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(number) + "원";
    }

    // 밀리초를 제거한 LocalDateTime 반환
    public static LocalDateTime truncateToSeconds(LocalDateTime dateTime) {
        return dateTime.truncatedTo(ChronoUnit.SECONDS);
    }
}
