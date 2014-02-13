package com.ailk.uap.util;

import java.sql.Connection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.ailk.jt.util.DBUtil;

public class DatetimeServicesTest {
	private Connection conn = null;
	private static Log logger = LogFactory.getLog(DatetimeServicesTest.class);
	
	@Before
	public void setUp() throws Exception {
		conn =  DBUtil.getAiuap20Connection();
	}
	
	@Test
	public void getNowDateTimeStrWithTTest() throws Exception{
		logger.debug("getNowDateTimeStrWithT value = " + DatetimeServices.getNowDateTimeStrWithT(conn));
	}
	
	@Test
	public void getLastMonthFirstDayStrWithTTest() throws Exception {
		logger.debug("getLastMonthFirstDayStrWithT value = " + DatetimeServices.getLastMonthFirstDayStrWithT(conn));
	}
	
	@Test
	public void getCurrentMonthFirstDayStrTest() throws Exception {
		logger.debug("getCurrentMonthFirstDayStr value = " + DatetimeServices.getCurrentMonthFirstDayStr(conn));
	}
	
	@Test
	public void getCurrentMonthFirstDayStrWithTTest() throws Exception {
		logger.debug("getCurrentMonthFirstDayStrWithT value = " +  DatetimeServices.getCurrentMonthFirstDayStrWithT(conn));
	}
	
	@Test
	public void getLastDayStartTimeStrTest() throws Exception {
		logger.debug("getLastDayStartTimeStr value = " +  DatetimeServices.getLastDayStartTimeStr(conn));
	}
	
	@Test
	public void getLastDayStartTimeStr7Test() throws Exception {
		logger.debug("getLastDayStartTimeStr7 value = " +  DatetimeServices.getLastDayStartTimeStr7(conn));
	}
	
	@Test
	public void getTodayStartTimeStr7Test() throws Exception {
		logger.debug("getTodayStartTimeStr7 value = " +  DatetimeServices.getTodayStartTimeStr7(conn));
	}
	
	@Test
	public void getLastDayStartTimeStrWithTTest() throws Exception {
		logger.debug("getLastDayStartTimeStrWithT value = " +  DatetimeServices.getLastDayStartTimeStrWithT(conn));
	}
	
	
	@Test
	public void getLastDayStrTest() throws Exception {
		logger.debug("getLastDayStr value = " +  DatetimeServices.getLastDayStr(conn));
	}
	
	@Test
	public void getTodayDayStrTest() throws Exception {
		logger.debug("getTodayDayStr value = " +  DatetimeServices.getTodayDayStr(conn));
	}
	
	@Test
	public void getTodayStartTimeStrTest() throws Exception {
		logger.debug("getTodayStartTimeStr value = " +  DatetimeServices.getTodayStartTimeStr(conn));
	}
	
	@Test
	public void getTodayStartTimeForTableTest() throws Exception {
		logger.debug("getTodayStartTimeForTable value = " +  DatetimeServices.getTodayStartTimeForTable(conn));
	}
	
	@Test
	public void getTodayStartTimeStrWithTTest() throws Exception {
		logger.debug("getTodayStartTimeStrWithT value = " +  DatetimeServices.getTodayStartTimeStrWithT(conn));
	}
	
	@Test
	@Ignore("to do")
	public void getDBSysdateTest() throws Exception {
		logger.debug("getDBSysdate value = " +  DatetimeServices.getDBSysdate(conn));
	}
	
	@Test
	public void getLastMonthFirstDayTest() throws Exception {
		logger.debug("getLastMonthFirstDay value = " +  DatetimeServices.getLastMonthFirstDay(conn));
	}
	
	@Test
	public void getCurrentMonthFirstDayTest() throws Exception {
		logger.debug("getCurrentMonthFirstDay value = " +  DatetimeServices.getCurrentMonthFirstDay(conn));
	}
	
	@Test
	public void getLastHourTimeTest() throws Exception {
		logger.debug("getLastHourTimeWithT value = " +  DatetimeServices.getLastHourTimeWithT(conn));
	}
	
	
	@Test
	public void getLastHourTimeWithTTest() throws Exception {
		logger.debug("getLastHourTimeWithT value = " +  DatetimeServices.getLastHourTimeWithT(conn));
	}
	
	@Test
	public void getThisHourTimeTest() throws Exception {
		logger.debug("getThisHourTime value = " +  DatetimeServices.getThisHourTime(conn));
	}
	
	@Test
	public void getThisHourTimeWithTTest() throws Exception {
		logger.debug("getThisHourTimeWithT value = " +  DatetimeServices.getThisHourTimeWithT(conn));
	}
	
	@Test
	public void getLastHourStrTest() throws Exception {
		logger.debug("getLastHourStr value = " +  DatetimeServices.getLastHourStr(conn));
	}
	
	@Test
	public void getThisHourStrTest() throws Exception {
		logger.debug("getThisHourStr value = " +  DatetimeServices.getThisHourStr(conn));
	}
	
	@Test
	public void getCurrentDateTimeStrTest() throws Exception {
		logger.debug("getCurrentDateTimeStrTest value = " +  DatetimeServices.getCurrentDateTimeStr(conn));
	}
	
}
