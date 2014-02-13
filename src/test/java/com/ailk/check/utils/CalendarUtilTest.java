package com.ailk.check.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: wangpu
 * Date: 13-6-21
 * Time: 上午11:40
 */
public class CalendarUtilTest {

    @Test
    public void testGetNextDayFromNumber() {
        Calendar calendar = CalendarUtil.getNextDayFromNumber(2013, 5, 21);
        int year = calendar.get(Calendar.YEAR);
        Assert.assertEquals(2013, year);
        int month = calendar.get(Calendar.MONTH);
        Assert.assertEquals(5, month);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        Assert.assertEquals(22, day);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        Assert.assertEquals(0, hour);
        int minute = calendar.get(Calendar.MINUTE);
        Assert.assertEquals(0, minute);
        int second = calendar.get(Calendar.SECOND);
        Assert.assertEquals(0, second);
        System.out.println(calendar.getTime());
    }

    @Test
    public void getLastDayToMonthFromNumberTest() {
        Calendar calendar = CalendarUtil.getLastDayToMonthFromNumber(2013, 1, 3);
        int year = calendar.get(Calendar.YEAR);
        Assert.assertEquals(2013, year);
        int month = calendar.get(Calendar.MONTH);
        Assert.assertEquals(1, month);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        Assert.assertEquals(28, day);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        Assert.assertEquals(0, hour);
        int minute = calendar.get(Calendar.MINUTE);
        Assert.assertEquals(0, minute);
        int second = calendar.get(Calendar.SECOND);
        Assert.assertEquals(0, second);
        System.out.println(calendar.getTime());
    }
}
