package com.ailk.jt.util;

import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;

public class Insert4AIOr4ARFileUtil {

	private static Connection conn = DBUtil.getAiuap20Connection();
	public static void main(String[] args) {
		String errorString = "There has difference between SM4AI and SMAAI!!!";

//		HashMap<String, String> dateMap = new HashMap<String, String>();
//		dateMap.put("errdesc", errorString);
//		dateMap.put("createTime", TimeAndOtherUtil.getCurrentDateTimeStr());
//		Insert4AI(dateMap);
		
		double dPercent = ((double) (Integer.valueOf("22") - Integer.valueOf("34")) / Integer
				.valueOf("22"));
		System.out.println("dpercent=="+dPercent);
	}

	public static void Insert4AI(HashMap<String, String> dateMap) {
		
		String insert4AISQL = "insert into A4_WATCH_4AI values(#fileName#,#errdesc#,#createTime#,#count#)";
		System.out.println(insert4AISQL);
		String saveSQL = SQLUtil.replaceParameter(insert4AISQL, dateMap);
		System.out.println(saveSQL);
		DBUtil.executeSQL(conn, saveSQL);
		DBUtil.closeConnection(conn);
	}
	
public static void Insert4AR(HashMap<String, String> dateMap) {
		
		String insert4ARSQL = "insert into A4_WATCH_4AR values(#fileName#,#errdesc#,#createTime#,#count#)";
		System.out.println(insert4ARSQL);
		String saveSQL = SQLUtil.replaceParameter(insert4ARSQL, dateMap);
		System.out.println(saveSQL);
		DBUtil.executeSQL(conn, saveSQL);
		DBUtil.closeConnection(conn);
	}
	
}
