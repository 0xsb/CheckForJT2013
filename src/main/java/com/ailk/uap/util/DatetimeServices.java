package com.ailk.uap.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ailk.uap.dbconn.ConnectionManager;

/**
 * @author zhoulei2@asiainfo-linkage.com
 * 		   liujie_09_24@163.com
 * @version 1.0
 * @since 2011.05.10
 */

public class DatetimeServices {

	private static Log logger = LogFactory.getLog(DatetimeServices.class);
	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_TIME_FORMAT_DAY = "yyyy-MM-dd";
	public static final String DATE_TIME_FORMAT_SEPERATOR = "T";
	public static final String DATE_TIME_FORMAT_SEC = "HH:mm:ss";
	public static final String DATE_FORMAT = "yyyyMMdd";
	public static final String YEAR_MONTH_FORMAT = "yyyyMM";
	private static Connection conn;

	public static void main(String[] args) throws Exception {
		conn = ConnectionManager.getUapAcctConnection();
		String todayString = getTodayStartTimeStr(conn);
		System.out.println(todayString);
		ConnectionManager.closeConnection(conn);
	}

	public static String getNowString(Connection conn) throws Exception {
		return "" + getDBSysdate(conn);
	}

	public static String getNowDateTimeStr(Connection conn) throws Exception {
		SimpleDateFormat dateformat = new SimpleDateFormat(DATE_TIME_FORMAT);
		return dateformat.format(getDBSysdate(conn));
	}

	public static String getNowDateTimeStrWithT(Connection conn) throws Exception {
		SimpleDateFormat dateformatS = new SimpleDateFormat(DATE_TIME_FORMAT_DAY);
		SimpleDateFormat dateformatE = new SimpleDateFormat(DATE_TIME_FORMAT_SEC);
		Date date = getDBSysdate(conn);
		return dateformatS.format(date) + DATE_TIME_FORMAT_SEPERATOR + dateformatE.format(date);
	}

	public static String getNowDate(Connection conn, String format) throws Exception {
		SimpleDateFormat dateformat = new SimpleDateFormat(format);
		return dateformat.format(getDBSysdate(conn));
	}

	public static String getCurrentMonthFirstDayStr(Connection conn) throws Exception {
		SimpleDateFormat dateformat = new SimpleDateFormat(DATE_TIME_FORMAT);
		return dateformat.format(getCurrentMonthFirstDay(conn));

	}

	public static String getCurrentMonthFirstDayStrWithT(Connection conn) throws Exception {
		SimpleDateFormat dateformatS = new SimpleDateFormat(DATE_TIME_FORMAT_DAY);
		SimpleDateFormat dateformatE = new SimpleDateFormat(DATE_TIME_FORMAT_SEC);
		Date date = getCurrentMonthFirstDay(conn);
		return dateformatS.format(date) + DATE_TIME_FORMAT_SEPERATOR + dateformatE.format(date);

	}

	public static String getLastMonthFirstDayDateStr(Connection conn) throws Exception {
		SimpleDateFormat dateformat = new SimpleDateFormat(DATE_FORMAT);
		return dateformat.format(getLastMonthFirstDay(conn));

	}

	public static String getLastMonthFirstDayDateStrWithT(Connection conn) throws Exception {
		SimpleDateFormat dateformatS = new SimpleDateFormat(DATE_TIME_FORMAT_DAY);
		SimpleDateFormat dateformatE = new SimpleDateFormat(DATE_TIME_FORMAT_SEC);
		Date date = getLastMonthFirstDay(conn);
		return dateformatS.format(date) + DATE_TIME_FORMAT_SEPERATOR + dateformatE.format(date);
	}

	public static String getLastMonthFirstDayStr(Connection conn) throws Exception {
		SimpleDateFormat dateformat = new SimpleDateFormat(DATE_TIME_FORMAT);
		return dateformat.format(getLastMonthFirstDay(conn));

	}

	public static String getLastMonthFirstDayStrWithT(Connection conn) throws Exception {
		SimpleDateFormat dayformat = new SimpleDateFormat(DATE_TIME_FORMAT_DAY);
		SimpleDateFormat secDateformat = new SimpleDateFormat(DATE_TIME_FORMAT_SEC);
		return dayformat.format(getLastMonthFirstDay(conn)) + DATE_TIME_FORMAT_SEPERATOR
				+ secDateformat.format(getLastMonthFirstDay(conn));

	}

	public static String getLastDayStartTimeStr(Connection conn) throws Exception {
		String querySql = "SELECT (sysdate-1) as lastdaystarttime FROM DUAL";
		return getDateFromConnection(conn,querySql,"lastdaystarttime", DATE_TIME_FORMAT);
	}

	/**
	 * SMJKR静态文件需要
	 * 
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public static String getLastDayStartTimeStr7(Connection conn) throws Exception {
		String querySql = "SELECT (sysdate-8) as lastdaystarttime FROM DUAL";
		return getDateFromConnection(conn,querySql,"lastdaystarttime", DATE_TIME_FORMAT);
	}

	/**
	 * 静态文件
	 * 
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public static String getTodayStartTimeStr7(Connection conn) throws Exception {
		String querySql = "SELECT (sysdate-7) as sysdate7  FROM DUAL";
		return getDateFromConnection(conn,querySql,"sysdate7", DATE_TIME_FORMAT);
	}

	public static String getLastDayStartTimeStrWithT(Connection conn) throws Exception {
		String querySql = "SELECT (sysdate-1) as lastdaystarttime FROM DUAL";
		SimpleDateFormat dateformatS = new SimpleDateFormat(DATE_TIME_FORMAT_DAY);
		SimpleDateFormat dateformatE = new SimpleDateFormat(DATE_TIME_FORMAT_SEC);
		Date date = getDateFromConnectionReturnDate(conn,querySql);
		return dateformatS.format(date) + DATE_TIME_FORMAT_SEPERATOR + dateformatE.format(date);
	}

	public static String getLastDayStr(Connection conn) throws Exception {
		String querySql = "SELECT (sysdate-1) as lastday FROM DUAL";
		return getDateFromConnection(conn,querySql,"lastday", DATE_FORMAT);
	}

	public static String getTodayDayStr(Connection conn) throws Exception {
		String querySql =  "SELECT (sysdate) as today FROM DUAL";
		return getDateFromConnection(conn,querySql,"today", DATE_FORMAT);
	}

	public static String getTodayStartTimeStr(Connection conn) throws Exception {
		String querySql =  "SELECT sysdate  FROM DUAL";
		return getDateFromConnection(conn,querySql,"sysdate", DATE_TIME_FORMAT);
	}

	public static String getTodayStartTimeForTable(Connection conn) throws Exception {
		String querySql =   "SELECT sysdate  FROM DUAL";
		return getDateFromConnection(conn,querySql,"sysdate", DATE_FORMAT);
	}

	public static String getTodayStartTimeStrWithT(Connection conn) throws Exception {
		String querySql = "SELECT sysdate  FROM DUAL";
		SimpleDateFormat dateformatS = new SimpleDateFormat(DATE_TIME_FORMAT_DAY);
		SimpleDateFormat dateformatE = new SimpleDateFormat(DATE_TIME_FORMAT_SEC);
		Date date = getDateFromConnectionReturnDate(conn,querySql);
		return dateformatS.format(date) + DATE_TIME_FORMAT_SEPERATOR + dateformatE.format(date);
	}

	public static Date getDBSysdate(Connection conn) throws Exception {
		String query_sql = "SELECT sysdate FROM DUAL";
		PreparedStatement prepStmt = conn.prepareStatement(query_sql);
		ResultSet rs = prepStmt.executeQuery();
		Date sysdate = null;
		if (rs.next()) {
			sysdate = rs.getTimestamp("sysdate");
		}

		ConnectionManager.closeResultSet(rs);
		ConnectionManager.closePrepStmt(prepStmt);
		return sysdate;
	}

	public static Date getLastMonthFirstDay(Connection conn) throws Exception {
		String querySql = "select   add_months(last_day(add_months(sysdate,-1))+1,-1) as last_month_first_day  from   dual";
		return getDateFromConnectionReturnDate(conn,querySql);
	}

	public static Date getCurrentMonthFirstDay(Connection conn) throws Exception {
		String querySql = "select to_date(to_char(sysdate,'yyyy-MM')||'01','yyyy-MM-dd')  from dual";
		return getDateFromConnectionReturnDate(conn,querySql);
	}

	public static String converterToDateTime(Timestamp ts) {
		SimpleDateFormat dateformat = new SimpleDateFormat(DATE_TIME_FORMAT);
		return dateformat.format(ts);
	}

	public static String converterToDateTimeWithT(Date ts) {
		SimpleDateFormat dateformatS = new SimpleDateFormat(DATE_TIME_FORMAT_DAY);
		SimpleDateFormat dateformatE = new SimpleDateFormat(DATE_TIME_FORMAT_SEC);
		return dateformatS.format(ts) + DATE_TIME_FORMAT_SEPERATOR + dateformatE.format(ts);
	}

	public static Date converterToDate(String dateStr) throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = dateFormat.parse(dateStr);
		return date;
	}

	public static String convertToDateStr(Date date, String dateFormat) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
		return simpleDateFormat.format(date);

	}

	public static String converterDateToStr(Date date) throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(date);
	}

	public static String getLastHourTime(Connection conn) throws Exception {
		String querySql = "select to_char(sysdate, 'yyyy-MM-dd') || ' ' || TO_CHAR(SYSDATE-1/24, 'HH24') ||':00:00' from dual";
		return getDateFromConnection(conn,querySql);
	}

	public static String getLastHourTimeWithT(Connection conn) throws Exception {
		String querySql = "select to_char(sysdate, 'yyyy-MM-dd') || 'T' || TO_CHAR(SYSDATE-1/24, 'HH24') ||':00:00' from dual";
		return getDateFromConnection(conn,querySql);
	}

	public static String getThisHourTime(Connection conn) throws Exception {
		String querySql = "select to_char(sysdate, 'yyyy-MM-dd') || ' ' || TO_CHAR(SYSDATE, 'HH24') ||':00:00' from dual";
		return getDateFromConnection(conn,querySql);
	}

	public static String getThisHourTimeWithT(Connection conn) throws Exception {
		String querySql = "select to_char(sysdate, 'yyyy-MM-dd') || 'T' || TO_CHAR(SYSDATE, 'HH24') ||':00:00' from dual";
		return getDateFromConnection(conn,querySql);
	}

	public static String getHourFileSeq(Connection conn) throws Exception {
		String query_sql = "select  '0'|| TO_CHAR(SYSDATE, 'HH24') from dual";
		PreparedStatement prepStmt = conn.prepareStatement(query_sql);
		ResultSet rs = prepStmt.executeQuery();
		String hourFileSeq = null;
		if (rs.next()) {
			hourFileSeq = rs.getString(1);
			if (hourFileSeq.equals("000")) {
				hourFileSeq = "024";
			}
		}
		ConnectionManager.closeResultSet(rs);
		ConnectionManager.closePrepStmt(prepStmt);

		return hourFileSeq;
	}

	public static Date getDateByStr(String iDateStr) throws Exception {
		SimpleDateFormat dateformat = new SimpleDateFormat(DATE_FORMAT);
		return dateformat.parse(iDateStr);
	}

	public static String getNowDateTimeStr(Date iDate) throws Exception {
		SimpleDateFormat dateformat = new SimpleDateFormat(DATE_TIME_FORMAT);
		return dateformat.format(iDate);
	}

	public static String getLastHourStr(Connection conn) throws SQLException {
		String querySql = "select to_char(sysdate-1/24,'yyyy-MM-dd HH24:')||'00:00' from dual";
		return getDateFromConnection(conn,querySql);
	}

	public static String getThisHourStr(Connection conn) throws SQLException {
		String querySql = "select to_char(sysdate,'yyyy-MM-dd HH24:')||'00:00' from dual";
		return getDateFromConnection(conn,querySql);
	}

	public static String getCurrentDateTimeStr(Connection conn) {
		String querySql = "select to_char(sysdate,'yyyy-MM-dd HH24:Mi:ss')from dual";
		try{
			return getDateFromConnection(conn,querySql);
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("=======>get current date time error!!");
		}
		return "";
	}
	
	private static String getDateFromConnection(Connection conn, String querySql,String columnName,String dateFormat) throws SQLException {
		PreparedStatement prepStmt = conn.prepareStatement(querySql);
		ResultSet rs = prepStmt.executeQuery();
		String result = null;
		if (rs.next()) {
			Date date = rs.getDate(columnName);
			SimpleDateFormat dateformat = new SimpleDateFormat(dateFormat);
			result = dateformat.format(date);
		}
		ConnectionManager.closeResultSet(rs);
		ConnectionManager.closePrepStmt(prepStmt);
		return result;
	}
	
	private static String getDateFromConnection(Connection conn, String querySql) throws SQLException {
		PreparedStatement prepStmt = conn.prepareStatement(querySql);
		ResultSet rs = prepStmt.executeQuery();
		String result = null;
		if (rs.next()) {
			result = rs.getString(1);
		}
		ConnectionManager.closeResultSet(rs);
		ConnectionManager.closePrepStmt(prepStmt);
		return result;
	}
	
	private static Date getDateFromConnectionReturnDate(Connection conn, String querySql) throws SQLException {
		PreparedStatement prepStmt = conn.prepareStatement(querySql);
		ResultSet rs = prepStmt.executeQuery();
		Date date = null;
		if (rs.next()) {
			date = rs.getDate(1);
		}
		ConnectionManager.closeResultSet(rs);
		ConnectionManager.closePrepStmt(prepStmt);
		return date;
	}
}