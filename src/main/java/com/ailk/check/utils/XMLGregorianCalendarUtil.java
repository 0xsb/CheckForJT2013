package com.ailk.check.utils;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created with IntelliJ IDEA.
 * User: wangpu
 * Date: 13-5-23
 * Time: 上午9:39
 * <p/>
 * XML日期类的转换类
 */
public class XMLGregorianCalendarUtil {

    /**
     * XMLGregorianCalendar转换为Calendar
     *
     * @param xmlGregorianCalendar XML日期
     * @return Calendar
     */
    public static Calendar xmlToCalendar(XMLGregorianCalendar xmlGregorianCalendar) {
        return xmlGregorianCalendar.toGregorianCalendar();
    }

    /**
     * Calendar转换为XML日期
     * 去掉毫秒和时区
     *
     * @param calendar Calendar
     * @return XML日期
     */
    public static XMLGregorianCalendar calendarToXml(Calendar calendar) throws DatatypeConfigurationException {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(calendar.getTimeInMillis());
        final XMLGregorianCalendar xgc = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        return getXmlGregorianCalendarOnlyTime(xgc);
    }

    /**
     * Date转换为XML日期
     *
     * @param date Date
     * @return XML日期
     * @throws DatatypeConfigurationException
     */
    public static XMLGregorianCalendar dateToXml(Date date) throws DatatypeConfigurationException {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(date.getTime());
        final XMLGregorianCalendar xgc = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        return getXmlGregorianCalendarOnlyTime(xgc);
    }

    /**
     * java.util.Date转换为XML日期
     * 去掉毫秒和时区
     *
     * @param date java.util.Date
     * @return XML日期
     */
    private XMLGregorianCalendar convertToXMLGregorianCalendar(Date date) throws DatatypeConfigurationException {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        final XMLGregorianCalendar xgc = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        return getXmlGregorianCalendarOnlyTime(xgc);
    }

    // 去掉毫秒和时区
    private static XMLGregorianCalendar getXmlGregorianCalendarOnlyTime(XMLGregorianCalendar xgc) {
        // 为了去掉毫秒和时区，重置XMLGregorianCalendar
        int year = xgc.getYear();
        int month = xgc.getMonth();
        int day = xgc.getDay();
        int hour = xgc.getHour();
        int minute = xgc.getMinute();
        int second = xgc.getSecond();
        xgc.clear();
        xgc.setYear(year);
        xgc.setMonth(month);
        xgc.setDay(day);
        xgc.setHour(hour);
        xgc.setMinute(minute);
        xgc.setSecond(second);
        return xgc;
    }
}
