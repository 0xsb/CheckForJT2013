package com.ailk.jt.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.ailk.uap.dbconn.ConnectionManager;

public class DateUtil {
	
	public static String formatDate(Date date){
		return formatDate(date,"yyyy-MM-dd HH:mm:ss");
	}
	//文件名后缀用
	public static String dateToStr(){
		Date date = new Date();
		return formatDate(date,"yyyyMMddHHmmss");
	}
	public static String ddToStr(){//查询是 几号了。
		Date date = new Date();
		return formatDate(date,"dd");
	}
	public static String ymdToStr(){
		Date date = new Date();
		return formatDate(date,"yyyy-MM-dd");
	}
	//获取当前天的前一天
	public static String getLastDayStartTimeStr(Connection conn) throws Exception {
		String query_sql = "SELECT (sysdate-1) as lastdaystarttime FROM DUAL";
		PreparedStatement prepStmt = conn.prepareStatement(query_sql);
		ResultSet rs = prepStmt.executeQuery();
		String lastdaystarttime = null;
		if (rs.next()) {
			Date lastDayStartTime = rs.getDate("lastdaystarttime");
			SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
			lastdaystarttime = dateformat.format(lastDayStartTime);
		}
		ConnectionManager.closeResultSet(rs);
		ConnectionManager.closePrepStmt(prepStmt);
		return lastdaystarttime;
	}
	//获取上个月的第一天
	public static String getLastMonthFirstDay(Connection conn) throws Exception {
		String query_sql = "select   add_months(last_day(add_months(sysdate,-1))+1,-1) as last_month_first_day  from   dual";
		PreparedStatement prepStmt = conn.prepareStatement(query_sql);
		ResultSet rs = prepStmt.executeQuery();
		String lastMonthFirstDay = null;
		if (rs.next()) {
			Date lastMonthFirstDay1 = rs.getDate("last_month_first_day");
			SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
			lastMonthFirstDay = dateformat.format(lastMonthFirstDay1);

		}
		ConnectionManager.closeResultSet(rs);
		ConnectionManager.closePrepStmt(prepStmt);

		return lastMonthFirstDay;
	}
	//获取本月的第一天的日期
	public static String getCurrentMonthFirstDay(Connection conn) throws Exception {
		String query_sql = "select to_date(to_char(sysdate,'yyyy-MM')||'01','yyyy-MM-dd') as currentmonthfirstday  from dual";
		PreparedStatement prepStmt = conn.prepareStatement(query_sql);
		ResultSet rs = prepStmt.executeQuery();
		String currentMonthFirstDay = null;
		if (rs.next()) {
		Date currentMonthFirstDay1 = rs.getDate("currentmonthfirstday");
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
		currentMonthFirstDay = dateformat.format(currentMonthFirstDay1);

		}
		ConnectionManager.closeResultSet(rs);
		ConnectionManager.closePrepStmt(prepStmt);

		return currentMonthFirstDay;
	}
	
	public static String cldToStr(Calendar cld){
		Date date = cld.getTime();
		return formatDate(date,"yyyyMMdd");
	}
	public static String formatDate(Date date,String format){
		SimpleDateFormat df = new SimpleDateFormat(format);
		String yyyymmdd= df.format(date);
		return yyyymmdd;
	}	
	
	public static Date strToDate(String date)throws Exception{
		return strToDate(date,"yyyy-MM-dd HH:mm:ss");
	}
	public static Date strToDateShort(String date)throws Exception{
		date = date+" 00:00:00";
		return strToDate(date,"yyyy-MM-dd HH:mm:ss");
	}
	public static Date strToDate(String date,String format)throws Exception{
		DateFormat df = new SimpleDateFormat(format); 
		Date result=null;
		try {
			result= df.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}
	
	//返回date时间过month之后的时间。
	public static Date getAfterMonth(Date date,int month){
		Calendar cal=Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, month);
		return cal.getTime();
	}
	
//~~~~~~~~duanxp
	public static String formatDateyyyy(Date date){
		return formatDate(date,"yyyy");
	}
	
	public static String formatDateyyyyMM(Date date){
		return formatDate(date,"yyyyMM");
	}
	public static String formatDateyyyyMMDD(Date date){
		return formatDate(date,"yyyyMMdd");
	}
	
	//给定时间 前几年的 时间
	public static Date getBeforeYear(Date date,int year)throws Exception{
		year = 1-year;
		Calendar cal=Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.YEAR, year);
		int newYear = cal.get(Calendar.YEAR);
		
		//"yyyy-MM-dd HH:mm:ss"
		return DateUtil.strToDate(newYear+"-1-1 0:0:0");
	}
	//给定时间 前几个月的 时间
	public static Date getBeforeMonth(Date date,int month)throws Exception{
		month = 1-month;
		Calendar cal=Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, month);
		int newYear = cal.get(Calendar.YEAR);
		int newMonth = cal.get(Calendar.MONTH)+1; //一月 为 0 
		
		//"yyyy-MM-dd HH:mm:ss"
		return DateUtil.strToDate(newYear+"-"+newMonth+"-1 0:0:0");
	}
	//给定时间 前几个天的 时间
	public static Date getBeforeDay(Date date,int day)throws Exception{
		day = 1-day;
		Calendar cal=Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, day);
		int newYear = cal.get(Calendar.YEAR);
		int newMonth = cal.get(Calendar.MONTH)+1;
		int newDay = cal.get(Calendar.DAY_OF_MONTH);
		
		//"yyyy-MM-dd HH:mm:ss"
		return DateUtil.strToDate(newYear+"-"+newMonth+"-"+newDay+" 0:0:0");
	}
	
	//日期之间 间隔几个月
	private static int getMonthSize(Date date1, Date date2){
		int iMonth = 0; 
		Calendar objCalendarDate1 = Calendar.getInstance(); 
		objCalendarDate1.setTime(date1);
		Calendar objCalendarDate2 = Calendar.getInstance(); 
		objCalendarDate2.setTime(date2);
		if (objCalendarDate2.equals(objCalendarDate1)){
			return 0;
		} 
		if (objCalendarDate1.after(objCalendarDate2)){ 
			Calendar temp = objCalendarDate1; 
			objCalendarDate1 = objCalendarDate2; 
			objCalendarDate2 = temp; 
		}
		if (objCalendarDate2.get(Calendar.YEAR) > objCalendarDate1.get(Calendar.YEAR)){ 
			iMonth = (objCalendarDate2.get(Calendar.YEAR)- objCalendarDate1.get(Calendar.YEAR))* 12 
					+ objCalendarDate2.get(Calendar.MONTH)- objCalendarDate1.get(Calendar.MONTH); 
		}else{ 
			iMonth = objCalendarDate2.get(Calendar.MONTH)- objCalendarDate1.get(Calendar.MONTH);
		}
		return iMonth;
	}
	
	//给定时间之间 所包括的所有月份集合
	public static List<String> getBetweenMs(Date date1, Date date2){
		int iMonth = DateUtil.getMonthSize(date1, date2);
		if(iMonth <= 0){
			return null;
		}
		List<String> rtnList = new ArrayList<String>();
		rtnList.add(DateUtil.formatDateyyyyMM(date1));
		
		Calendar cld = Calendar.getInstance(); 
		cld.setTime(date1);
		for (int i = 1; i < iMonth; i++) {
			cld.add(Calendar.MONTH, 1);
			rtnList.add(DateUtil.formatDateyyyyMM(cld.getTime()));
		}
		return rtnList;
	}
	
	//给定时间之间 所包括的所有年份集合
	public static List<String> getBetweenYs(Date date1, Date date2){
		List<String> rtnList = new ArrayList<String>();
		Calendar cld1 = Calendar.getInstance();
		cld1.setTime(date1);
		Calendar cld2 = Calendar.getInstance();
		cld2.setTime(date2);
		if(cld1.after(cld2)){ 
			Calendar temp = cld1; 
			cld1 = cld2; 
			cld2 = temp; 
		}
		int startY = cld1.get(Calendar.YEAR);
		int endY = cld2.get(Calendar.YEAR);
		while(startY <= endY){
			rtnList.add(String.valueOf(startY));
			startY++;
		}
		return rtnList;
	}
	
	//给定时间之间 所包括的所有日期（天）集合
	public static List<String> getBetweenDs(Date date1, Date date2){
		long iDay = Math.abs((date1.getTime()-date2.getTime())/(24*60*60*1000));
		List<String> rtnList = new ArrayList<String>();
		rtnList.add(DateUtil.formatDateyyyyMMDD(date1));
		
		Calendar cld = Calendar.getInstance(); 
		cld.setTime(date1);
		for (int i = 1; i < iDay; i++) {
			cld.add(Calendar.DAY_OF_YEAR, 1);
			rtnList.add(DateUtil.formatDateyyyyMMDD(cld.getTime()));
		}
		return rtnList;
	}
	
}
