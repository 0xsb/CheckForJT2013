package com.ailk.jt.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ailk.uap.dbconn.ConnectionManager;
import com.ailk.uap.util.DatetimeServices;

/**
 * @ClassName: TimeAndOtherUtil
 * @Description: 时间转换和百分比转换工具栏
 * @author huangpumm@asiainfo-linkage.com
 * @date Jul 13, 2012 4:00:02 PM
 */

public class TimeAndOtherUtil {

	private static Log log = LogFactory.getLog(TimeAndOtherUtil.class);// 获取日志打印对象

	/**
	 * @Title: getLastDayStartTimeStr
	 * @Description:从数据库中查询昨天的日期
	 * @return String 返回昨天的日期字符串格式
	 */
	public static String getLastDayStartTimeStr() {
		try {
			Connection conn = DBUtil.getAiuap20Connection();
			return DatetimeServices.getLastDayStartTimeStr(conn);
		} catch (Exception e) {
			log.error("====get last day time from db error!======" + e.getMessage());
		}
		return null;
	}

	/**
	 * @Title: getToDayStartTimeStr
	 * @Description: 从数据库中查询今天的日期
	 * @return String 返回今天的日期字符串格式
	 */
	public static String getTodayStartTimeStr() {
		try {
			Connection conn = DBUtil.getAiuap20Connection();
			String timeString= DatetimeServices.getTodayStartTimeStr(conn);
			DBUtil.closeConnection(conn);
			return timeString;
		} catch (Exception e) {
			log.error("====get today time from db error!======" + e.getMessage());
		}
		return null;
	}
	public static String getLastDayStr(){
		try {
			Connection conn = DBUtil.getAiuap20Connection();
			return DatetimeServices.getLastDayStr(conn);
		} catch (Exception e) {
			log.error("====get today time from db error!======" + e.getMessage());
		}
		return null;
	}

	public static String getTodayStartTimeForTable() {
		try {
			Connection conn = DBUtil.getAiuap20Connection();
			return DatetimeServices.getTodayStartTimeForTable(conn);
		} catch (Exception e) {
			log.error("====get today time from db error!======" + e.getMessage());
		}
		return null;
	}

	/**
	 * @Title: getToDayStartTimeStr
	 * @Description: 从数据库中查询今天的日期
	 * @return String 返回今天的日期字符串格式
	 */
	public static String getCurrentDateTimeStr() {
		try {
			Connection conn = DBUtil.getAiuap20Connection();
			return DatetimeServices.getCurrentDateTimeStr(conn);
		} catch (Exception e) {
			log.error("====get today time from db error!======" + e.getMessage());
		}
		return null;
	}

	public static Date DateTimeStringFromXMLToDate(String dateTimeString) {
		String tempTime = dateTimeString.replace("T", " ");
		return stringTotimeFormater(tempTime);
	}

	/**
	 * @Title: getCurrentMonthStr
	 * @Description: 从数据库中查询当月的日期
	 * @return String 返回今天的日期字符串格式
	 */
	public static String getCurrentMonthStr() {
		try {
			Connection conn = DBUtil.getAiuap20Connection();
			System.out.println("" + DatetimeServices.getCurrentMonthFirstDayStr(conn));
			return DatetimeServices.getCurrentMonthFirstDayStr(conn);
		} catch (Exception e) {
			log.error("====get today time from db error!======" + e.getMessage());
		}
		return null;
	}

	/**
	 * @Title: getToDayStartTimeStr
	 * @Description: 从数据库中查询今天的日期
	 * @return String 返回今天的日期字符串格式
	 */
	public static String getCurrentMonth() {
		try {
			Connection conn = DBUtil.getAiuap20Connection();
			Date time = DatetimeServices.getCurrentMonthFirstDay(conn);
			//System.out.println("\t" + timeToStringYearAndMonthFormater(time));
			return timeToStringYearAndMonthFormater(time);
		} catch (Exception e) {
			log.error("====get today time from db error!======" + e.getMessage());
		}
		return null;
	}

	/**
	 * @Title: bFormater
	 * @Description: 将百分比显示成本地形式。比如0.2%
	 * @param object
	 *            需要转换的百分比
	 * @return String 返回本地形式的百分比
	 */
	public static String bFormater(double object) {
		NumberFormat f = NumberFormat.getPercentInstance(Locale.CHINA);
		f.setMaximumIntegerDigits(3);
		f.setMaximumFractionDigits(4);
		f.setMinimumFractionDigits(4);
		return f.format(object);
	}

	/**
	 * @Title: timeFormater
	 * @Description: 将日期格式转换成"yyyy-MM-dd HH:mm:ss"形式，比如"2012-07-09 00:00:00"
	 * @param date
	 *            需要转换的时间格式
	 * @return String 返回转换后的时间格式
	 */
	public static String timeToStringFormater(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}

	/**
	 * @Title: timeFormater
	 * @Description: 将日期格式转换成"yyyy-MM-dd HH:mm:ss"形式，比如"2012-07-09 00:00:00"
	 * @param date
	 *            需要转换的时间格式
	 * @return String 返回转换后的时间格式
	 */
	public static String timeToStringYearAndMonthFormater(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		return sdf.format(date);
	}

	/**
	 * @Title: timeFormater
	 * @Description: 将"yyyy-MM-dd HH:mm:ss"形式的字符串，比如"2012-07-09
	 *               00:00:00"，转换成日期格式，
	 * @param date
	 *            需要转换的时间格式
	 * @return String 返回转换后的时间格式
	 */
	public static Date stringTotimeFormater(String dateString) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return sdf.parse(dateString);
		} catch (Exception e) {
			log.error("======>when parse string to date occur an error!!!");
		}
		return null;
	}

	public static void main(String[] args) {
		TimeAndOtherUtil timeAndOtherUtil = new TimeAndOtherUtil();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1);
		Date date = calendar.getTime();
		String rightTime = new SimpleDateFormat("yyyy-MM-dd").format(date);
		System.out.println("11111111  "+rightTime);
	}
}