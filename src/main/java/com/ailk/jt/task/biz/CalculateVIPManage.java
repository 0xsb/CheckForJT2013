package com.ailk.jt.task.biz;

import java.sql.Connection;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.ailk.jt.task.service.A4MainAcctJtService;
import com.ailk.jt.util.DBUtil;
import com.ailk.jt.util.SQLUtil;
import com.ailk.jt.util.SpringUtil;
import com.ailk.jt.util.TimeAndOtherUtil;

/**
 * @ClassName: CalculateVIP
 * @Description: 计算合作伙伴账号管理良好率:主要为推动省公司及时清理、冻结或锁定较长时间不使用业务支撑系统的合作伙伴账号。
 *               【合作伙伴主账号管理良好率】
 *               合作伙伴主账号管理良好率=（当前非常用合作伙伴主账号的状态为无效或被锁定的总数/当月所有非常用合作伙伴主账号总数）
 *               其中非常用主账号是指当月登录天数少于4天的主账号。
 *               若合作伙伴主账号管理良好率低于80%，则扣0.10分，在此基础上每下降10%，则加扣0.10分。
 * @author huangpumm@asiainfo-linkage.com
 * @date Jul 13, 2012 12:33:38 PM
 */
public class CalculateVIPManage {
	private static final Logger log = Logger.getLogger(CalculateVIPManage.class);// 获取日志打印对象
	private static A4MainAcctJtService a4MainAcctJtService;

	/**
	 * @Title: main
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param args
	 */
	public static void main(String[] args) {
		HashMap<String, String> dateMapAndAcctType = new HashMap<String, String>();
		// dateMapAndAcctType.put("beginTime",
		// TimeAndOtherUtil.getLastDayStartTimeStr());
		// dateMapAndAcctType.put("endTime",
		// TimeAndOtherUtil.getTodayStartTimeStr());
		// dateMapAndAcctType.put("monthBeginTime",
		// TimeAndOtherUtil.getCurrentMonthStr());
		// dateMapAndAcctType.put("acct_type", "6");
		Connection conn = DBUtil.getAiuap20Connection();
		start(conn, dateMapAndAcctType);
		DBUtil.closeConnection(conn);

	}

	public static void start(Connection conn, HashMap<String, String> parameterMap) {
//		a4MainAcctJtService = (A4MainAcctJtService) SpringUtil.getBean("a4MainAcctJtService");
		// 1、计算当前非常用合作伙伴主账号的状态为无效或被锁定的总数
		int hzInvalidCount = getInvalidCount();
		// 2、计算当月所有非常用合作伙伴主账号总数
		int hzAllCount = getHZAllCount();
		// 计算合作伙伴主账号
		double result = calculateManagerd(hzInvalidCount, hzAllCount);
		// 保存计算结果
		String insertSQL_temp = SQLUtil.getSql("vip_manage");
		HashMap<String, String> dateMap = new HashMap<String, String>();
		dateMap.put("caculate_time", TimeAndOtherUtil.getCurrentDateTimeStr());
		dateMap.put("login_days", hzInvalidCount + "");
		dateMap.put("all_acct", hzAllCount + "");
		dateMap.put("acct_manage", result + "");
		String insertSQL = SQLUtil.replaceParameter(insertSQL_temp, dateMap);
		log.error("===========" + insertSQL);
		DBUtil.executeSQL(conn, insertSQL);
	}

	/**
	 * @Title: calculateManagerd
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param hzInvalidCount
	 * @param hzAllCount
	 */
	private static double calculateManagerd(int hzInvalidCount, int hzAllCount) {
		if (hzInvalidCount == 0 || hzAllCount == 0) {// 如果非常用无效或被锁定的总数合作伙伴值为0或者所有非常用合作伙伴主账号总数为0，说明系统出错
			DBUtil.notice("【4A】合作伙伴总数计算出错，请核对");
			log.error("send message over~~~");
			return 0.0;
		} else {
			double isHeGe = (double) hzInvalidCount / hzAllCount;
			DBUtil.notice("【4A】截止到" + TimeAndOtherUtil.getCurrentDateTimeStr() + ",合作伙伴管理良好率为："
					+ TimeAndOtherUtil.bFormater(isHeGe));
			log.error("send message over~~~");
			return isHeGe;
		}
	}

	/**
	 * @Title: getHZAllCount
	 * @Description: 计算当月所有非常用合作伙伴主账号总数
	 * @param dateMap
	 * @return int 返回类型
	 */
	private static int getHZAllCount() {
		return a4MainAcctJtService.getHZAllCount();
	}

	/**
	 * @Title: getInvalidCount
	 * @Description:计算当前非常用合作伙伴主账号的状态为无效或被锁定的总数 其中非常用主账号是指当月登录天数少于4天的主账号。
	 * @param dateMap
	 * @return int 返回类型
	 */
	private static int getInvalidCount() {
		log.error("========" + a4MainAcctJtService);
		return a4MainAcctJtService.getInvalidCount();
	}

}
