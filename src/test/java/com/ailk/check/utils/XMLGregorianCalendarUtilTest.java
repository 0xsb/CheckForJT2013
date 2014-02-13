package com.ailk.check.utils;

import org.junit.Assert;
import org.junit.Test;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: wangpu
 * Date: 13-6-13
 * Time: 下午6:23
 */
public class XMLGregorianCalendarUtilTest {

    @Test
    public void testCalendarToXml() throws DatatypeConfigurationException {
        Calendar calendar = Calendar.getInstance();
        XMLGregorianCalendar xmlGregorianCalendar = XMLGregorianCalendarUtil.calendarToXml(calendar);
        Assert.assertTrue(xmlGregorianCalendar.toString().length() == 19);
    }
}
