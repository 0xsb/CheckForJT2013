package com.ailk.jt.task.biz;

import java.sql.Connection;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.ailk.jt.util.DBUtil;
import com.ailk.jt.util.PropertiesUtil;
import com.ailk.jt.util.TimeAndOtherUtil;

/**
 * @ClassName: CalculateAll
 * @Description: 计算所有指标
 * @author huangpumm@asiainfo-linkage.com
 * @date Aug 17, 2012 12:52:50 PM
 */
public class CalculateAll {
	private static Logger log = Logger.getLogger(CalculateAll.class); // 获取打印日志工具类对象
	private static String sqlFile = PropertiesUtil.getValue("calculateSQL_file_path");
	private static Connection conn = null;

	/**
	 * @Title: main
	 * @Description: 计算所有考核指标，包括主账号使用率、变动率、合作伙伴良好管理率、应用账号覆盖率、金库使用情况
	 * @param
	 * @param args
	 *            设定文件
	 */
	public static void main(String[] args) {
		conn = DBUtil.getAiuap20Connection();
		/**
		 * 配置时间参数，计算昨天一天的账号使用率情况
		 */
		HashMap<String, String> parameterMap = new HashMap<String, String>();
		String timeForTable = TimeAndOtherUtil.getTodayStartTimeForTable();
		parameterMap.put("beginTime", TimeAndOtherUtil.getLastDayStartTimeStr());
		parameterMap.put("endTime", TimeAndOtherUtil.getTodayStartTimeStr());
		parameterMap.put("monthBeginTime", TimeAndOtherUtil.getCurrentMonthStr());
		parameterMap.put("timeForTable", timeForTable);
		parameterMap.put("dataBasePart", "PART_APP_LOG_" + TimeAndOtherUtil.getCurrentMonth());

		/**
		 * 【开始计算指标】<br>
		 * 1、计算主账号使用率（客服、坐席、合作伙伴）<br>
		 * 2、计算主账号变动率（所有）<br>
		 * 3、计算主账号良好管理率（合作伙伴） <br>
		 * 4、计算从账号登录和操作情况（从账号登录情况、从账号操作情况）<br>
		 * 5、计算从账号覆盖率（4A库和BOSS库从账号）<br>
		 * 6、计算金库使用情况<br>
		 */
		log.error("==begin calculate usage==");
		CalculateUsage.start(conn, sqlFile, parameterMap);
		log.error("==end calculate usage==");

		log.error("==begin calculate change==");
		CalculateChange.start(parameterMap);
		log.error("==end calculate change==");

		log.error("==begin calculate manage==");
		parameterMap.put("acct_type", "6");
		CalculateVIPManage.start(conn, parameterMap);
		log.error("==end calculate manage==");

		// log.error("==begin calculate coverd==");
		// CalculateCoverd.start(parameterMap);//有问题
		// log.error("==end calculate coverd==");

		log.error("==begin calculate goldManage==");
		CalculateGoldManage.start(parameterMap);
		log.error("==end calculate goldManage==");

		/**
		 * 关闭数据库连接
		 */
		DBUtil.closeConnection(conn);
	}

}
