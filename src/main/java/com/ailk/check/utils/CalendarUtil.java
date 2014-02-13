package com.ailk.check.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: wangpu
 * Date: 13-6-4
 * Time: 下午3:40
 *
 * 日期工具类
 * 注意：以下方法均参考Calendar设置，月从零开始，不用加一
 */
public class CalendarUtil {

    /**
     * 获取给定数字的字符串日期 yyyy-MM-dd
     *
     * @param year  年
     * @param month 月
     * @param day   日
     * @return 字符串日期
     */
    public static String getStringDateFromNumber(int year, int month, int day) {
        return getStringFromNumber(year, month, day, 0, "yyyy-MM-dd");
    }

    /**
     * 获取给定数字的下一天的字符串日期 yyyy-MM-dd
     *
     * @param year  年
     * @param month 月
     * @param day   日
     * @return 下一天的字符串日期
     */
    public static String getStringNextDateFromNumber(int year, int month, int day) {
        return getStringFromNumber(year, month, day + 1, 0, "yyyy-MM-dd");
    }

    /**
     * 获取给定数字的字符串日期 yyyyMMdd
     *
     * @param year  年
     * @param month 月
     * @param day   日
     * @return 字符串日期
     */
    public static String getAtStringDateFromNumber(int year, int month, int day) {
        return getStringFromNumber(year, month, day, 0, "yyyyMMdd");
    }

    /**
     * 获取给定数字的下一天
     *
     * @param year  年
     * @param month 月
     * @param day   日
     * @return 下一天
     */
    public static Calendar getNextDayFromNumber(int year, int month, int day) {
        return getCalendarFromNumber(year, month, day + 1, 0);
    }

    /**
     * 获取给定数字的当月最后一天
     *
     * @param year  年
     * @param month 月
     * @param day   日
     * @return 当月最后一天
     */
    public static Calendar getLastDayToMonthFromNumber(int year, int month, int day) {
        return getCalendarFromNumber(year, month + 1, 0, 0);
    }

    /**
     * 获取给定数字的下月第一天
     *
     * @param year  年
     * @param month 月
     * @param day   日
     * @return 下月第一天
     */
    public static Calendar getFirstDayNextMonthFromNumber(int year, int month, int day) {
        return getCalendarFromNumber(year, month + 1, 1, 0);
    }

    // 获取给定数字的日期
    public static Calendar getCalendarFromNumber(int year, int month, int day, int hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    // 获取给定数字的字符串日期
    public static String getStringFromNumber(int year, int month, int day, Integer hour, String format) {
        return formatDate(getCalendarFromNumber(year, month, day, hour).getTime(), format);
    }

    // 格式化日期为字符串
    public static String formatDate(Date date,String format){
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }
}
