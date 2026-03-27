package com.ramis.progresstracker.util;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DateUtils {

    /**
     * Вычисли количество дней между двумя датами
     */
    public static long daysBetween(LocalDate start, LocalDate end) {
        return ChronoUnit.DAYS.between(start, end);
    }

    /**
     * Проверь, прошла ли неделя с определённой даты
     */
    public static boolean isMoreThanWeekAgo(LocalDate date) {
        return ChronoUnit.DAYS.between(date, LocalDate.now()) > 7;
    }

    /**
     * Получи начало недели (понедельник)
     */
    public static LocalDate getWeekStart(LocalDate date) {
        return date.minusDays(date.getDayOfWeek().getValue() - 1);
    }

    /**
     * Получи конец недели (воскресенье)
     */
    public static LocalDate getWeekEnd(LocalDate date) {
        return date.plusDays(7 - date.getDayOfWeek().getValue());
    }

}
