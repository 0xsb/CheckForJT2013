package com.ailk.jt.task.biz;

import java.sql.Connection;
import java.util.HashMap;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import com.ailk.jt.util.DBUtil;
import com.ailk.jt.util.SQLUtil;
import com.ailk.jt.util.TimeAndOtherUtil;

public class CalculateUsageTest {

	private static final Logger log = Logger.getLogger(CalculateUsageTest.class);// 获取日志打印对象
	private 	HashMap<String, String> parameterMap = new HashMap<String, String>();
	private 	Connection conn = DBUtil.getAiuap20Connection();
	private 	String sqlFilePath = "classpath:com/ailk/jt/task/entity/CalculateUsageTest.xml";
	private String sqlFile = sqlFilePath;
	
	@Before
	public void setUp() {
		// String timeForTable = TimeAndOtherUtil.getTodayStartTimeForTable();
//		 parameterMap.put("beginTime",
//		 TimeAndOtherUtil.getLastDayStartTimeStr());
//		 parameterMap.put("endTime", TimeAndOtherUtil.getTodayStartTimeStr());
		// parameterMap.put("monthBeginTime",
		// TimeAndOtherUtil.getCurrentMonthStr());
		// parameterMap.put("dataBasePart", "PART_APP_LOG_" +
		// TimeAndOtherUtil.getCurrentMonth());
		// parameterMap.put("timeForTable", timeForTable);

		parameterMap.put("beginTime", "2011-11-01 00:00:00");
		parameterMap.put("endTime", "2011-11-02 00:00:00");
		parameterMap.put("monthBeginTime", TimeAndOtherUtil.getCurrentMonthStr());
		parameterMap.put("dataBasePart", "PART_APP_LOG_" + TimeAndOtherUtil.getCurrentMonth());
//		parameterMap.put("dataBasePart", "PART_APP_LOG_201108" );
		String timeForTable = TimeAndOtherUtil.getTodayStartTimeForTable();
		parameterMap.put("timeForTable", timeForTable);
	}
	
	@Test
	/**添加 新用户测试
	 * 2013年11月01日,新增user1 - user20账号，测试账号信息.
	 * uap_main_acct	= 20
	 * uap_main_acct_his	= 0
	 * uap_main_acct_his_today_test = 0
	 * uap_main_acct_his_yestoday_T = 0
	 * a4_main_acct_snap_test = 20 
	 * a4_main_acct_jt_test = 20
	 * snap - jt	= 
	 * jt - snap 	=
	 * (a4_main_acct_jt_test) - (jt - snap )=
	 * avg =
	 */
	public void addUserTest() {
		Assert.assertEquals(20, DBUtil.getCountValue(conn, "select count(1) from uap_main_acct_test"));
		Assert.assertEquals(0, DBUtil.getCountValue(conn,  "select count(1) from uap_main_acct_his_test"));
		Assert.assertEquals(0, DBUtil.getCountValue(conn,  "select count(1) from uap_main_acct_his_today_test"));
		Assert.assertEquals(0, DBUtil.getCountValue(conn, "select count(1) from uap_main_acct_his_yestoday_T"));
		Assert.assertEquals(0, DBUtil.getCountValue(conn, "select count(1) from a4_main_acct_snap_test"));
		Assert.assertEquals(0, DBUtil.getCountValue(conn, "select count(1) from a4_main_acct_jt_test"));
		
		// 1、 备份快照表(a4_main_acct_snap)
		String backup_a4_main_acct_snap_temp = SQLUtil.getSql(sqlFile, "back_a4_main_acct_snap");
		String backup_a4_main_acct_snap = SQLUtil.replaceParameter(backup_a4_main_acct_snap_temp, parameterMap);
		log.error("<!--the sql backup_a4_main_acct_snap is :--> " + backup_a4_main_acct_snap);
		DBUtil.executeSQL(conn, backup_a4_main_acct_snap);
		log.error("<!-- backup a4_main_acct_snap end--> ");
		
		Assert.assertEquals(20, DBUtil.getCountValue(conn, "select count(1) from a4_main_acct_snap"));

		// 准备两个历史表，目的时候发现历史表中发生变更的数据
		// 2.1、清空昨天的历史表
		String truncate_a4_main_acct_his_yestory = SQLUtil.getSql(sqlFile, "truncate_a4_main_acct_his_yestory");
		log.error("<!--the sql backup_a4_main_acct_his_yestory is :--> " + truncate_a4_main_acct_his_yestory);
		DBUtil.executeSQL(conn, truncate_a4_main_acct_his_yestory);
		log.error("<!-- backup truncate_a4_main_acct_his_yestory end--> ");

		// 2.1.2、将今天的历史表数据导入到昨天的历史表中去
		String insert_into_a4_main_acct_his_yestory = SQLUtil.getSql(sqlFile, "insert_into_a4_main_acct_his_yestory");
		log.error("<!--the sql insert_into_a4_main_acct_his_yestory is :--> " + insert_into_a4_main_acct_his_yestory);
		DBUtil.executeSQL(conn, insert_into_a4_main_acct_his_yestory);
		log.error("<!--  insert_into_a4_main_acct_his_yestory end--> ");

		// 2.2、清空今天的历史表
		String truncate_a4_main_acct_his_today = SQLUtil.getSql(sqlFile, "truncate_a4_main_acct_his_today");
		DBUtil.executeSQL(conn, truncate_a4_main_acct_his_today);
		log.error("<!-- backup backup_a4_main_acct_his_yestory end--> ");

		// 2.2.2、将历史表数据导入到今天的历史表中去
		String insert_into_a4_main_acct_his_today = SQLUtil.getSql(sqlFile, "insert_into_a4_main_acct_his_today");
		log.error("<!--the sql insert_into_a4_main_acct_his_today is :--> " + insert_into_a4_main_acct_his_today);
		DBUtil.executeSQL(conn, insert_into_a4_main_acct_his_today);
		log.error("<!--  insert_into_a4_main_acct_his_today end--> ");

		// 3、删除快照表数据
		String truncate_uap_main_acct_snap = SQLUtil.getSql(sqlFile, "truncate_a4_main_acct_snap");
		log.error("<!--the sql backup_uap_main_acct_snap is :--> " + truncate_uap_main_acct_snap);
		DBUtil.executeSQL(conn, truncate_uap_main_acct_snap);
		log.error("<!--truncate a4_main_acct_snap  end--> ");

		// 4、 更新数据源(uap_main_acct,uap_main_acct_his表中的数据更新到a4_main_acct_snap表中)
		String insert_uap_main_acct_snap_temp = SQLUtil.getSql(sqlFile, "insert_a4_main_acct_snap");
		String insert_uap_main_acct_snap = SQLUtil.replaceParameter(insert_uap_main_acct_snap_temp, parameterMap);
		log.error("<!--the sql insert_uap_main_acct_snap is :--> " + insert_uap_main_acct_snap);
		DBUtil.executeSQL(conn, insert_uap_main_acct_snap);
		log.error("<!--  insert_uap_main_acct_snap  end--> ");
		
		
		Assert.assertEquals(20, DBUtil.getCountValue(conn, "select count(1) from uap_main_acct_test"));
		Assert.assertEquals(0, DBUtil.getCountValue(conn,  "select count(1) from uap_main_acct_his_test"));
		Assert.assertEquals(0, DBUtil.getCountValue(conn, "select count(1) from uap_main_acct_his_today_test"));
		Assert.assertEquals(0, DBUtil.getCountValue(conn, "select count(1) from uap_main_acct_his_yestoday_T"));
		Assert.assertEquals(20, DBUtil.getCountValue(conn, "select count(1) from a4_main_acct_snap_test"));
		Assert.assertEquals(0, DBUtil.getCountValue(conn, "select count(1) from a4_main_acct_jt_test"));
	}
	
	@Test
	/**
	 * 删除用户 测试
	 * 2013年11月02日，删除 user1-user5用户 测试
	  * uap_main_acct	= 20
	 * uap_main_acct_his	= 0
	 * uap_main_acct_his_today_test = 0
	 * uap_main_acct_his_yestoday_T = 0
	 * a4_main_acct_snap_test = 20 
	 * a4_main_acct_jt_test = 20
	 * snap - jt	= 
	 * jt - snap 	=
	 * (a4_main_acct_jt_test) - (jt - snap )=
	 * avg =
	 */
	public void delUserTest() {
		
	}
	
	@Test
	/**
	 * 更新用户 测试
	 * 2013年11月02日， 更新user6-user10用户 测试
	  * uap_main_acct	= 20
	 * uap_main_acct_his	= 0
	 * uap_main_acct_his_today_test = 0
	 * uap_main_acct_his_yestoday_T = 0
	 * a4_main_acct_snap_test = 20 
	 * a4_main_acct_jt_test = 20
	 * snap - jt	= 
	 * jt - snap 	=
	 * (a4_main_acct_jt_test) - (jt - snap )=
	 * avg =
	 */
	public void updateUserTest() {
		
	}
	
}
