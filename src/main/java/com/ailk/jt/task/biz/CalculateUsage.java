package com.ailk.jt.task.biz;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.jt.task.dao.A4MainAcctLogonDaysDao;
import com.ailk.jt.task.entity.A4MainAcctJt;
import com.ailk.jt.task.entity.A4MainAcctLogonDays;
import com.ailk.jt.task.entity.A4MainAcctSnap;
import com.ailk.jt.task.service.A4MainAcctJtService;
import com.ailk.jt.task.service.A4MainAcctSnapService;
import com.ailk.jt.util.DBUtil;
import com.ailk.jt.util.PropertiesUtil;
import com.ailk.jt.util.SQLUtil;
import com.ailk.jt.util.SpringUtil;
import com.ailk.jt.util.TimeAndOtherUtil;

/**
 * @ClassName: CalculateUsage
 * @Description: 【计算4A账号使用率】
 *               <h1>【主要人员使用率】</h1>
 *               <p>
 *               总部根据各省公司上报的营业员、客服、渠道、大客户经理、业务支撑内部员工（以下简称内部人员）、业务支撑部门合作伙伴（以下简称合作伙伴）
 *               六类人员分别计算4A平台使用率。
 *               </p>
 *               <h2>【使用率计算公式如下】 </h2>
 *               <p>
 *               x类人员4A平台使用率=（x类人员在用主账号使用率大于等于40%的总数/x类人员在用主账号总数），
 *               x类人员在用主账号是指类别为x类，且在该省当月提交主账号月全量文件中可用天数超过5天的所有主账号。
 *               其中，单个主账号的4A平台使用率=（主账号当月登录天数/主账号当月可用天数），
 *               其中主账号在一天内登录一次及以上，则计为拥有一个登录天；
 *               主账号可用天数是指当月主账号状态为可用的天数（若主账号当前有效且未被锁定状态，应计为可用状态）。
 *               单个主账号的登录天数与可用天数由省公司通过主账号月全量文件上报为准。
 *               </p>
 *               <h3>【要求】</h3>
 *               <p>
 *               对营业员、客服人员、与合作伙伴三类人员分别计算4A平台使用率，每类人员的使用率大于80%则不扣分，低于80%扣0.05分，
 *               在此基础上，使用率每下降10%则加扣0.05分。三类人员的使用率分别独立计算，扣分结果进行累计。
 *               </p>
 *               <h4>【备注】</h4>
 *               "主账号当月可用天数"这个数值需要单独创建一张表UAP_MAIN_ACCT_USABLEDAY,这个表需要每天更新
 *               "主账号使用率"这个数值需要单独创建一张表uap_main_acct_single_usage,这个表需要每天更新
 * @author huangpumm@asiainfo-linkage.com
 */
public class CalculateUsage {
	private static final Logger log = Logger.getLogger(CalculateUsage.class);// 获取日志打印对象
	private static A4MainAcctSnapService a4MainAcctSnapService;// 主账号快照表服务类
	private static A4MainAcctJtService a4MainAcctJtService;

	public static void main(String[] args) {
		/**
		 * 配置时间参数，计算昨天一天的账号使用率情况
		 */
		HashMap<String, String> parameterMap = new HashMap<String, String>();
		// String timeForTable = TimeAndOtherUtil.getTodayStartTimeForTable();
		 parameterMap.put("beginTime",
		 TimeAndOtherUtil.getLastDayStartTimeStr());
		 parameterMap.put("endTime", TimeAndOtherUtil.getTodayStartTimeStr());
		// parameterMap.put("monthBeginTime",
		// TimeAndOtherUtil.getCurrentMonthStr());
		// parameterMap.put("dataBasePart", "PART_APP_LOG_" +
		// TimeAndOtherUtil.getCurrentMonth());
		// parameterMap.put("timeForTable", timeForTable);

//		parameterMap.put("beginTime", "2011-08-01 00:00:00");
//		parameterMap.put("endTime", "2011-08-21 00:00:00");
		parameterMap.put("monthBeginTime", TimeAndOtherUtil.getCurrentMonthStr());
		parameterMap.put("dataBasePart", "PART_APP_LOG_" + TimeAndOtherUtil.getCurrentMonth());
//		parameterMap.put("dataBasePart", "PART_APP_LOG_201108" );
		String timeForTable = TimeAndOtherUtil.getTodayStartTimeForTable();
		parameterMap.put("timeForTable", timeForTable);

		Connection conn = DBUtil.getAiuap20Connection();
		String sqlFile = PropertiesUtil.getValue("calculateSQL_file_path");

		log.error("===================calculateSQL_file_path\t" + sqlFile);
	    log.error(" transfor parameters is :" + parameterMap);
		start(conn, sqlFile, parameterMap);
	}

	public static void start(Connection conn, String sqlFile, HashMap<String, String> parameterMap) {
		// 备份和更新snap表
		 backupAndUpdate(conn, sqlFile, parameterMap);
		 // 2、更新模拟集团表
		 updateJTtable(conn, sqlFile, parameterMap);
		// 1、通过日志表查找那些账号登陆过，将登陆过的记录保存到临时表中
		calculateLogonDays(conn, sqlFile, parameterMap);
		// 3、计算并保持使用率,分三类人员计算
		updateMainAcctUsageFromJT(conn);

	}

	private static void calculateLogonDays(Connection conn, String sqlFile, HashMap<String, String> parameterMap) {
		// 一、根据月初和当前时间查询该月账号的登录天数，将登录天数更新到一个临时表中
		// 1.1因为每天都要计算可用天数和登录天数，首先清空临时表
		String truncate_Logons_temp = SQLUtil.getSql(sqlFile, "truncate_Logons_temp");
		log.error("===================truncate_Logons_temp\t" + truncate_Logons_temp);
		DBUtil.executeSQL(conn, truncate_Logons_temp);

		// 1.2从日志表中查询出从月初到当前时间的登录天数，并更新到临时表中
		String select_a4_main_acct_login_days_temp = SQLUtil.getSql(sqlFile, "select_a4_main_acct_login_days");
		String select_a4_main_acct_login_days = SQLUtil.replaceParameter(select_a4_main_acct_login_days_temp,
				parameterMap);
		log.error("===================select_a4_main_acct_login_days:" + select_a4_main_acct_login_days);
		DBUtil.executeSQL(conn, select_a4_main_acct_login_days);
		String truncate_logon = " truncate table  a4_mainacct_logon_count ";
	    DBUtil.executeSQL(conn, truncate_logon);

	    String insert_a4mainacctlogoncount = "insert into a4_mainacct_logon_count ( select t.main_acct_name, count(t.login_day) as login_day from A4_MAIN_ACCT_LOGONS_TEMP t  where t.login_day >=to_date('" + 
	      (String)parameterMap.get("monthBeginTime") + 
	      "', 'yyyy-MM-dd hh24:Mi:ss') " + 
	      " and t.login_day <to_date('" + 
	      (String)parameterMap.get("endTime") + 
	      "', 'yyyy-MM-dd hh24:Mi:ss') group by t.main_acct_name) ";
	    DBUtil.executeSQL(conn, insert_a4mainacctlogoncount);
	    log.error("insert_a4mainacctlogoncount sql= " + insert_a4mainacctlogoncount);

	    String update_jt_logondays = "update a4_main_acct_jt t set t.logondays = (select b.login_day from a4_mainacct_logon_count b  where b.main_acct_name = t.login_name) where exists (select 1 from a4_mainacct_logon_count b where b.main_acct_name = t.login_name)";

	    DBUtil.executeSQL(conn, update_jt_logondays);
	    log.error("update_jt_logondays sql= " + update_jt_logondays);

	    String update_jt_acctusage = "  update a4_main_acct_jt t set t.acct_usage=t.logondays/t.opendays where t.opendays<>0 and t.logondays<=t.opendays ";
	    DBUtil.executeSQL(conn, update_jt_acctusage);
	    log.error("update_jt_acctusage sql= " + update_jt_acctusage);

	    String update_jt_acctusage_opendays = " update a4_main_acct_jt t set t.acct_usage=0.0000 where t.opendays=0 ";
	    DBUtil.executeSQL(conn, update_jt_acctusage_opendays);
	    log.error("update_jt_acctusage_opendays sql= " + update_jt_acctusage_opendays);

	    String update_jt_acctusage_opendays_2 = " update a4_main_acct_jt t set t.acct_usage=1 where t.logondays>t.opendays ";
	    DBUtil.executeSQL(conn, update_jt_acctusage_opendays_2);
	    log.error("update_jt_acctusage_opendays_2 sql= " + update_jt_acctusage_opendays_2);
	  }

	private static void updateJTtable(Connection conn, String sqlFile, HashMap<String, String> parameterMap) {
		//在更新今天的集团表之前先进行备份
		String nowdatetime = TimeAndOtherUtil.getTodayStartTimeForTable();
		String bak_a4_main_acct_jt_nowdate = "create table a4_main_acct_jt_" + nowdatetime +" as select * from a4_main_acct_jt";
		log.info("bak_a4_main_acct_jt_nowdate:sql=" + bak_a4_main_acct_jt_nowdate);
		DBUtil.executeSQL(conn, bak_a4_main_acct_jt_nowdate);
		
		// 一、依据临时表更新主账号集团表
		// 1.1、如果是月初，首先将所有主账号集团表中的可用天数和登录天数重置为0
		String monthBeginTime = parameterMap.get("monthBeginTime");
		String endTime = parameterMap.get("endTime");
		if (monthBeginTime.equals(endTime)) {
			String update_main_acct_jt_init = SQLUtil.getSql(sqlFile, "update_main_acct_jt_init");
			log.error("===================update_main_acct_jt_init:\t" + update_main_acct_jt_init);
			DBUtil.executeSQL(conn, update_main_acct_jt_init);
		}
		// 1.2将集团表中所有有效账号的可用天数加1
		String add_main_acct_jt_aviliable = SQLUtil.getSql(sqlFile, "add_main_acct_jt_aviliable");
		log.error("===================add_main_acct_jt_aviliable\t" + add_main_acct_jt_aviliable);
		DBUtil.executeSQL(conn, add_main_acct_jt_aviliable);

		// 二 从快照表中查找所有昨天一天的主账号，更新到集团表中
		a4MainAcctSnapService = (A4MainAcctSnapService) SpringUtil.getBean("a4MainAcctSnapService");
		a4MainAcctJtService = (A4MainAcctJtService) SpringUtil.getBean("a4MainAcctJtService");

		List<A4MainAcctSnap> uapMainAcct2 = a4MainAcctSnapService.selectMainAcctByTime(parameterMap);//snap表中数据
		for (A4MainAcctSnap uapMainAcct3 : uapMainAcct2) {
			// 2、依据主账号表中的主账号登陆名称查找主账号集团表
			HashMap<String, Object> parMap = new HashMap<String, Object>();
			parMap.put("login_name", uapMainAcct3.getLoginName());
			List<A4MainAcctJt> jtAcctList = a4MainAcctJtService.getMainAcctJTByName(parMap);//jt表中数据
			// 如果能查到主账号，说明主账号发生过变更，包括更改和删除
			if (jtAcctList.size() > 0) {
				for (int i = 0; i < jtAcctList.size(); i++) {
					A4MainAcctJt jtAcct = jtAcctList.get(i);
					A4MainAcctJt copy = copyFromLocalToJT(uapMainAcct3, jtAcct);

					if ("del".equals(copy.getModifyMode())) {
						// 1、因为主账号被删除，然后再将主账号添加到主账号集团表中
						a4MainAcctJtService.delMainAcctJT(copy);//此处删除了uap_main_acct_jt表中数据
						System.out.println("=main acct :" + copy.getLoginName() + "  has deleted================");
						continue;
					}
					long openDays = 0;
					// 更新该账号的使用天数
					if (uapMainAcct3.getValid().equals("1")) {
						// 如果账号有效才查询账号的登录天数
						openDays = jtAcct.getOpendays() + 1;
						copy.setOpendays(openDays);
					}
					// 从数据库中查询出该账号的登录天数，更新到集团表中
					copy.setModifyMode("upd");

					// 将信息更新到数据库中
					a4MainAcctJtService.updateMainAcctJT(copy);
				}
			} else {
				// 如果查询不到就直接添加到集团表中
				A4MainAcctJt copy = copyFromLocalToJT(uapMainAcct3, new A4MainAcctJt());
				copy.setModifyMode("add");
				copy.setOpendays(new Long(1));
				copy.setLogondays(new Long(0));
				copy.setAcctUsage(new Double(0.0000));
				a4MainAcctJtService.insertMainAcctJT(copy);
			}
		}
		// 三、根据临时表更新主账号快照表，主要是更新集团表的登录天数
		A4MainAcctLogonDaysDao a4MainAcctLogonDaysDao = (A4MainAcctLogonDaysDao) SpringUtil
				.getBean("a4MainAcctLogonDaysDao");
		List<A4MainAcctLogonDays> daysList = a4MainAcctLogonDaysDao.getLogOnDays(parameterMap);
		// 2.3.1、依据临时表，更新集体表中的登录天数
		for (A4MainAcctLogonDays uapMainAcctLogon : daysList) {
			// 2、依据主账号登录表中的主账号登陆名称查找主账号集团表
			HashMap<String, Object> parMap = new HashMap<String, Object>();
			parMap.put("login_name", uapMainAcctLogon.getMainAcctName());
			List<A4MainAcctJt> jtAcctList = a4MainAcctJtService.getMainAcctJTByName(parMap);
			// 如果能查到主账号，说明主账号存在登录记录，需要更新集团表中对应的登录记录
			if (jtAcctList.size() > 0) {
				for (int i = 0; i < jtAcctList.size(); i++) {
					A4MainAcctJt jtAcct = jtAcctList.get(i);
					jtAcct.setOpendays(uapMainAcctLogon.getLoginDay());
					// 将信息更新到数据库中
					long logon_days = 0;
					double usage = 0.0000;
					// 更新该账号的使用天数
					// 如果账号有效才查询账号的登录天数
					logon_days = uapMainAcctLogon.getLoginDay();
					long openDays = jtAcct.getOpendays() + 1;
					usage = (double) (logon_days - openDays > 0 ? openDays : logon_days) / openDays;
					jtAcct.setLogondays(logon_days);
					jtAcct.setAcctUsage(usage);
					a4MainAcctJtService.updateMainAcctJT(jtAcct);
				}
			}
		}
	}

	/**
	 * @Title: updateMainAcctUsageFromJT
	 * @Description: 从集团表中获取账号的使用率情况，并分账号类型计算使用率
	 */
	private static void updateMainAcctUsageFromJT(Connection conn) {
		String[] acctType = PropertiesUtil.getValue("acct_type").split(",");// 计算营业员1、客服坐席2、合作伙伴使用率6
		StringBuffer message = new StringBuffer("【4A】截止到" + TimeAndOtherUtil.getCurrentDateTimeStr() + "");
		for (int i = 0; i < acctType.length; i++) {
			double usage = calculateMainAcctUsage(conn, acctType[i]);// 计算某一种账号类型的使用率
			if (acctType[i].equals("1")) {
				message.append(",营业员使用率为:" + TimeAndOtherUtil.bFormater(usage));
			}
			if (acctType[i].equals("2")) {
				message.append(",客服坐席使用率为:" + TimeAndOtherUtil.bFormater(usage));
			}
			if (acctType[i].equals("6")) {
				message.append(",合作伙伴使用率为:" + TimeAndOtherUtil.bFormater(usage));
			}
		}
		DBUtil.notice(message.toString());
	}

	private static double calculateMainAcctUsage(Connection conn, String acctType) {
		HashMap<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("acct_type", acctType);

		// 查询费登录天数超过5天的账号总数
		String overFiveDaysSQL_temp = SQLUtil.getSql("acct_type_over_five_days");
		String overFiveDaysSQL = SQLUtil.replaceParameter(overFiveDaysSQL_temp, parameterMap);
		int overFiveDaysNumber = DBUtil.getCountValue(conn, overFiveDaysSQL);

		// 查询使用率超过40%的账号总数
		String aboveSQL_temp = SQLUtil.getSql("acct_type_valid");
		String aboveSQL = SQLUtil.replaceParameter(aboveSQL_temp, parameterMap);
		int aboveNumber = DBUtil.getCountValue(conn, aboveSQL);

		// 查询有效的账号总数
		String belowSQL_temp = SQLUtil.getSql("acct_type_valid_count");
		String belowSQL = SQLUtil.replaceParameter(belowSQL_temp, parameterMap);
		int belowNumber = DBUtil.getCountValue(conn, belowSQL);

		double result = (double) aboveNumber / belowNumber;
		// 保存使用率
		String insertSQL_temp = SQLUtil.getSql("save_usage");
		HashMap<String, String> dateMap = new HashMap<String, String>();
		dateMap.put("caculate_time", TimeAndOtherUtil.getCurrentDateTimeStr());
		dateMap.put("over_five_count", overFiveDaysNumber + "");
		dateMap.put("over_forty_count", aboveNumber + "");
		dateMap.put("acct_type", acctType);
		dateMap.put("acct_usage", result + "");
		dateMap.put("all_count", belowNumber + "");
		String insertSQL = SQLUtil.replaceParameter(insertSQL_temp, dateMap);
		DBUtil.executeSQL(conn, insertSQL);

		return result;

	}

	private static A4MainAcctJt copyFromLocalToJT(A4MainAcctSnap uapMainAcct3, A4MainAcctJt jtAcct) {
		if (jtAcct == null) {
			jtAcct = new A4MainAcctJt();
		}
		jtAcct.setAcctType(uapMainAcct3.getAcctType());
		jtAcct.setAcctUsage(uapMainAcct3.getAcctUsage());
		jtAcct.setAreaId(uapMainAcct3.getAreaId());
		jtAcct.setCreateTime(uapMainAcct3.getCreateTime());
		jtAcct.setEffectTime(uapMainAcct3.getEffectTime());
		jtAcct.setExpireTime(uapMainAcct3.getExpireTime());
		jtAcct.setLockStatus(uapMainAcct3.getLockStatus());
		jtAcct.setLoginName(uapMainAcct3.getLoginName());
		jtAcct.setMainAcctId(uapMainAcct3.getMainAcctId());
		jtAcct.setModifyMode(uapMainAcct3.getModifyMode());
		jtAcct.setOrgId(uapMainAcct3.getOrgId());
		jtAcct.setOrgname(uapMainAcct3.getOrgname());
		jtAcct.setRolelist(uapMainAcct3.getRolelist());
		jtAcct.setSuperuser(uapMainAcct3.getSuperuser());
		jtAcct.setUpdateTime(uapMainAcct3.getUpdateTime());
		jtAcct.setUserName(uapMainAcct3.getUserName());
		jtAcct.setValid(uapMainAcct3.getValid());
		// jtAcct.setOpendays(uapMainAcct3.getOpendays());
		// jtAcct.setLogondays(uapMainAcct3.getLogondays());
		return jtAcct;
	}

	private static void backupAndUpdate(Connection conn, String sqlFile, HashMap<String, String> parameterMap) {
		// 1、 备份快照表(a4_main_acct_snap)
		String backup_a4_main_acct_snap_temp = SQLUtil.getSql(sqlFile, "back_a4_main_acct_snap");
		String backup_a4_main_acct_snap = SQLUtil.replaceParameter(backup_a4_main_acct_snap_temp, parameterMap);
		log.error("<!--the sql backup_a4_main_acct_snap is :--> " + backup_a4_main_acct_snap);
		DBUtil.executeSQL(conn, backup_a4_main_acct_snap);
		log.error("<!-- backup a4_main_acct_snap end--> ");

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
	}
}
