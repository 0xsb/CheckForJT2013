package com.ailk.jt.task.biz;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.ailk.jt.util.DBUtil;
import com.ailk.jt.util.SQLUtil;
import com.ailk.jt.util.TimeAndOtherUtil;

/**
 * @ClassName: CalculateGoldManage
 * @Description: 计算金库模式管理使用情况
 *               <p>
 *               总部根据各省每日上报的金库模式分场景分时段统计相关数据（按每半小时进行统计），包括请求量，授权允许量、拒绝量和超时量，以及操作量。
 *               a.某类场景实际在该省应用，但当月内请求量或授权允许次数为0，则该场景计为使用不合格；
 *               b.前台授权允许量不应小于金库模式操作量，若二者误差超过2%，则该场景计为使用不合格。
 *               每个场景使用不合格扣0.05分，各场景扣分累计。
 *               
 *               本程序的作用就是每天监控月初至今天为止本地场景中请求量和允许量为0的场景，并发短信告知责任人，以便于在月末修改金库场景文件（SMJKR）以确保
 *               本月所有金库请求量和允许量至少有一次不为0
 *               </p>
 * @author huangpumm@asiainfo-linkage.com
 * @date Jul 13, 2012 12:30:38 PM
 */
public class CalculateGoldManage {

	private static final Logger log = Logger.getLogger(CalculateGoldManage.class);
	private static Connection conn;
	private int[] result = new int[] { 0, 0, 0, 0, 0 };
	private int contextid;
	private String zzString;
	private String yxString;
	private TreeMap<Integer, String> changjingid = new TreeMap<Integer, String>();
	private TreeMap<Integer, String> changjingyxid = new TreeMap<Integer, String>();
	private String changjing;
	private String changjingyx;

	public static void main(String[] args) {

		HashMap<String, String> parameterMap = new HashMap<String, String>();
		String timeForTable = TimeAndOtherUtil.getTodayStartTimeForTable();
		parameterMap.put("beginTime", TimeAndOtherUtil.getLastDayStartTimeStr());
		// 测试用
		// parameterMap.put("endTime", "2012-08-31");
		// parameterMap.put("monthBeginTime", "2012-08-01");
		// parameterMap.put("dataBasePart", "dataBasePart201208");

		// 开始时间为本月第一天，结束时间为当前时间，待到月末最后一天时，统计出来的即为本月所有场景，如若有场景请求量和允许量为0，则应将最后一天的金库日增量文件中将该场景某一时间请求量设为非零
		// 正式时间
		parameterMap.put("endTime", TimeAndOtherUtil.getTodayStartTimeStr());
		parameterMap.put("monthBeginTime", TimeAndOtherUtil.getCurrentMonthStr());
		parameterMap.put("timeForTable", timeForTable);
		parameterMap.put("dataBasePart", "dataBasePart" + TimeAndOtherUtil.getCurrentMonth());

		start(parameterMap);
	}

	/**
	 * @Title: main
	 * @Description: 金库模式分场景分时段统计相关数据（按每半小时进行统计），包括请求量，授权允许量、拒绝量和超时量，以及操作量。
	 *               并根据统计值按照标准进行判定是否合格。
	 * 
	 * @param args
	 * @return void 返回类型
	 */
	public static void start(HashMap<String, String> parameterMap) {
		try {
			conn = DBUtil.getAiuap20Connection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		CalculateGoldManage cg = new CalculateGoldManage();
		// 1、计算请求量，授权允许量、拒绝量和超时量，以及操作量
		try {
			int[] a = cg.calculateNumber(parameterMap);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// 2、判定是否合格，如果合格，只发短信通知该月情况。不合格，告警
		cg.panding();
	}

	/**
	 * @Title:calculateNumber
	 * @throws SQLException
	 * @@Description:计算请求量，授权允许量、拒绝量和超时量，以及操作量
	 */
	private int[] calculateNumber(HashMap<String, String> parameterMap) throws SQLException {
		// 查询金库操作请求量，授权允许量、拒绝量和超时量SQL
		String sqlData_temp1 = SQLUtil.getSql("gold_sence_operate");
		String sqlData1 = SQLUtil.replaceParameter(sqlData_temp1, parameterMap);
		log.error("gold_sence_operate=" + sqlData1);
		PreparedStatement prepStmt1 = conn.prepareStatement(sqlData1);
		ResultSet rs = prepStmt1.executeQuery();

		// 将所有场景添加到changjingid中，共22个场景
		
		
		changjingid.put(2, "2");
		changjingid.put(3, "3");
		changjingid.put(4, "4");
		changjingid.put(5, "5");
		changjingid.put(6, "6");
		changjingid.put(7, "7");
		changjingid.put(8, "8");
		changjingid.put(9, "9");
		changjingid.put(10, "10");
		changjingid.put(11, "11");
		changjingid.put(12, "12");
		changjingid.put(13, "13");
		changjingid.put(14, "14");
		changjingid.put(15, "15");
		changjingid.put(16, "16");
		changjingid.put(17, "17");
		changjingid.put(18, "18");
		changjingid.put(19, "19");
		changjingid.put(20, "20");
		changjingid.put(21, "21");
		changjingid.put(99, "99");
		changjingid.put(1, "1");
		
	
		changjingyxid.put(1, "1");
		changjingyxid.put(2, "2");
		changjingyxid.put(3, "3");
		changjingyxid.put(4, "4");
		changjingyxid.put(5, "5");
		changjingyxid.put(6, "6");
		changjingyxid.put(7, "7");
		changjingyxid.put(8, "8");
		changjingyxid.put(9, "9");
		changjingyxid.put(10, "10");
		changjingyxid.put(11, "11");
		changjingyxid.put(12, "12");
		changjingyxid.put(13, "13");
		changjingyxid.put(14, "14");
		changjingyxid.put(15, "15");
		changjingyxid.put(16, "16");
		changjingyxid.put(17, "17");
		changjingyxid.put(18, "18");
		changjingyxid.put(19, "19");
		changjingyxid.put(20, "20");
		changjingyxid.put(21, "21");
		changjingyxid.put(99, "99");
		while (rs.next()) {
			// 请求量
			if (rs.getString(4) != null) {
				result[0] = Integer.valueOf(rs.getString(4).trim());
				if (result[0] != 0) {
					// 测试环境因contextid为空值，所以取的uapcontextid，正式环境应取contextid
					contextid = rs.getInt("contextid");
					log.info(contextid);
					if (result[0] != 0) {
						zzString = contextid + "," + zzString;
					}
				}
			} else {
				result[0] = 0;
			}

			// 授权允许量、
			if (rs.getString(5) != null) {
				result[1] = Integer.valueOf(rs.getString(5).trim());
				contextid = rs.getInt("contextid");
				if(result[1]!=0){
					yxString = contextid + "," + yxString;
				}
			} else {
				result[1] = 0;
			}
			// 拒绝量
			if (rs.getString(6) != null) {
				result[2] = Integer.valueOf(rs.getString(6).trim());
			} else {
				result[2] = 0;
			}
			// 超时量
			if (rs.getString(7) != null) {
				result[3] = Integer.valueOf(rs.getString(7).trim());
			} else {
				result[3] = 0;
			}
		}
		// 关闭资源
		DBUtil.closePrepStmt(prepStmt1);
		DBUtil.closeResultSet(rs);

		return result;
	}

	/**
	 * @Title:panding
	 * @@Description:判定是否合格，如果合格，只发短信通知该月情况。不合格，告警
	 */
	private void panding() {
		StringBuffer message = new StringBuffer("【4A】截止到" + TimeAndOtherUtil.getCurrentDateTimeStr() + "");
		// 删除changjingid中本月已经存在的场景
		String ef[] = zzString.substring(0, zzString.length() - 5).split(",");
		int[] ab= new  int[ef.length];
		for (int i = 0; i < ef.length; i++) {
	       ab[i] = Integer.parseInt(ef[i]);
		}
		
		String yx[] = yxString.substring(0, yxString.length() - 5).split(",");
		int[] cd= new int[yx.length];
		for (int i = 0; i < yx.length; i++) {
	       cd[i] = Integer.parseInt(yx[i]);
		}
		
		Iterator<Integer> dayin1 = changjingid.keySet().iterator();
		//将map中请求量不为0的场景删除
		for (int i = 1; i <= ab.length; i++) {
			changjingid.remove(ab[i-1]);
		}
		Iterator<Integer> dayinyx = changjingyxid.keySet().iterator();
		//将map中允许量不为0的场景删除
		for (int i = 1; i <= cd.length; i++) {
			changjingyxid.remove(cd[i-1]);
		}
		// 如果changjingid为空，说明所有场景在本月均不为零，发送短信通知合格，如果有一个场景请求量为零，则发短信通知说明该场景本月请求量为零，通知不合格。
		if (changjingid.isEmpty()&&changjingyxid.isEmpty()) {
			message.append("金库模式管理使用情况合格,所有场景请求量和运行量均不为0！");
		} else {
			Iterator<Integer> zuizhong = changjingid.keySet().iterator();
//			while (zuizhong.hasNext()) {
//				changjing = zuizhong.next().toString() + "," + changjing;
//			}
			while (zuizhong.hasNext()) {
				changjing = changjing +","+ zuizhong.next().toString();
			}
			System.out.println("222222222222  "+changjing);
			Iterator<Integer> zuizhongyx = changjingyxid.keySet().iterator();
//			while (zuizhongyx.hasNext()) {
//				changjingyx = zuizhongyx.next().toString() + "," + changjingyx;
//			}
			while (zuizhongyx.hasNext()) {
				changjingyx = changjingyx + "," + zuizhongyx.next().toString();
			}
			System.out.println("111111111111111  "+changjingyx);
			message.append("金库模式管理使用情况不合格！请求量为0的场景：" + changjing.substring(5, changjing.length())
					+ ";允许量为0的场景："+ changjingyx.substring(5, changjingyx.length())+"。请查看！");
		}
		DBUtil.notice(message.toString());
	}
}
