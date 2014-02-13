package com.ailk.check.safeguard.safe.random.shuffle;

import com.ailk.check.utils.XMLGregorianCalendarUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Calendar;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: wangpu
 * Date: 13-5-23
 * Time: 上午9:24
 * <p/>
 * 对时间进行修改
 * 范围是正负100秒
 */
public class ShuffleTime {
    private static Logger logger = LoggerFactory.getLogger(ShuffleTime.class);

    /**
     * 对xml日期进行修改
     *
     * @param xmlCalendar xml日期
     * @param changeValue 变化值
     * @return 修改后的xml日期
     * @throws DatatypeConfigurationException
     */
    public static XMLGregorianCalendar shuffle(XMLGregorianCalendar xmlCalendar, long changeValue)
            throws DatatypeConfigurationException {
        Calendar calendar = shuffle(XMLGregorianCalendarUtil.xmlToCalendar(xmlCalendar), changeValue);
        return XMLGregorianCalendarUtil.calendarToXml(calendar);
    }

    /**
     * 对日期进行修改
     *
     * @param calendar    日期
     * @param changeValue 变化值
     * @return 修改后的日期
     */
    public static Calendar shuffle(Calendar calendar, long changeValue) {
        //logger.debug("time change is : " + changeValue);
        long time = calendar.getTimeInMillis();
        calendar.setTimeInMillis(time + changeValue);
        return calendar;
    }

    /**
     * 获取变化值
     * 范围是正负100秒之间
     *
     * @return 变化值
     */
    public static long getChangeValue() {
        // 正负100秒之间
        int num = new Random().nextInt(100000) + 1000;
        int mark = new Random().nextInt(2);
        if (mark == 0) {
            num = -num;
        }
        return num;
    }

    /**
     * 重置年月日
     *
     * @param xmlCalendar 要修改的日期
     * @param year        年
     * @param month       月
     * @param day         日
     * @return 修改后的日期
     * @throws DatatypeConfigurationException
     */
    public static XMLGregorianCalendar resetYearMonthDay(XMLGregorianCalendar xmlCalendar, Integer year, Integer month, Integer day)
            throws DatatypeConfigurationException {
        Calendar calendar = resetYearMonthDay(XMLGregorianCalendarUtil.xmlToCalendar(xmlCalendar), year, month, day);
        return XMLGregorianCalendarUtil.calendarToXml(calendar);
    }

    /**
     * 重置年月日
     *
     * @param calendar 要修改的日期
     * @param year     年
     * @param month    月
     * @param day      日
     * @return 修改后的日期
     */
    public static Calendar resetYearMonthDay(Calendar calendar, Integer year, Integer month, Integer day) {
        if (year != null) {
            calendar.set(Calendar.YEAR, year);
        }
        if (month != null) {
            calendar.set(Calendar.MONTH, month);
        }
        if (day != null) {
            calendar.set(Calendar.DAY_OF_MONTH, day);
        }
        return calendar;
    }
}
