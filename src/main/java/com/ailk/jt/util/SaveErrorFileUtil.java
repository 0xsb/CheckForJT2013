package com.ailk.jt.util;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Logger;

public class SaveErrorFileUtil {
	private static Logger log = Logger.getLogger(DBUtil.class);
	private static Properties tran = PropertiesUtil.getProperties("/tran.properties");

	public static void main(String[] args) {
		Connection conn = DBUtil.getAiuap20Connection();
//		String insertSQL_temp = SQLUtil.getSql("save_error_file_reason");
//		System.out.println("=======================");
//		System.out.println(insertSQL_temp);

//		HashMap dateMap = new HashMap();
//		dateMap.put("file_begin_time", TimeAndOtherUtil.getCurrentDateTimeStr());
//		dateMap.put("file_end_time", TimeAndOtherUtil.getCurrentDateTimeStr());
//		dateMap.put("file_name", "SMMAL");
//		dateMap.put("file_sum", "12122");
//		dateMap.put("file_error_reason", "file sum not equal seq");
//		dateMap.put("file_upload_to_bomc", TimeAndOtherUtil.getCurrentDateTimeStr());
//		String insertSQL = SQLUtil.replaceParameter(insertSQL_temp, dateMap);
//
//		System.out.println("=======================");
//		System.out.println(insertSQL);
		
		

		HashMap<String,String> dateMap = new HashMap<String,String>();
		dateMap.put("file_begin_time", TimeAndOtherUtil.getLastDayStartTimeStr());
		dateMap.put("file_end_time", TimeAndOtherUtil.getTodayStartTimeStr());
		dateMap.put("file_name", "SMMAL");
		dateMap.put("file_sum", "12122");
		dateMap.put("file_error_reason","error");
		dateMap.put("file_upload_to_bomc", TimeAndOtherUtil.getCurrentDateTimeStr());
		saveErrorFile(dateMap);

	}

	public static void saveErrorFile(HashMap<String, String> dateMap) {
		Connection conn = DBUtil.getAiuap20Connection();
		saveErrorFile(conn, dateMap);
	}

	public static void saveErrorFile(Connection conn, HashMap<String, String> dateMap) {
		String insertSQL_temp = SQLUtil.getSql("save_error_file_reason");
		System.out.println(insertSQL_temp);
		String saveSQL = SQLUtil.replaceParameter(insertSQL_temp, dateMap);
		
		log.error(" save error file  SQL is:" + saveSQL);
		DBUtil.executeSQL(conn, saveSQL);
		log.error(" now begin close connection");
		DBUtil.closeConnection(conn);
		log.error(" end close connection");
	}
}
