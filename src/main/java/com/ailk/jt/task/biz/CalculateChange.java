package com.ailk.jt.task.biz;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.jt.task.entity.A4MainAcctJt;
import com.ailk.jt.task.entity.A4MainAcctSnap;
import com.ailk.jt.task.service.A4MainAcctJtService;
import com.ailk.jt.task.service.A4MainAcctSnapService;
import com.ailk.jt.util.DBUtil;
import com.ailk.jt.util.SQLUtil;
import com.ailk.jt.util.TimeAndOtherUtil;

/**
 * 【重要提醒】该类得注释部分为更新，还是以前的注释，注释部分需要重新写一遍！
 * 
 * @ClassName: CalculateChange
 * @Description:【计算主账号变动率】
 * @author huangpumm@asiainfo-linkage.com
 * @date Jul 21, 2012 11:24:10 AM
 */
public class CalculateChange {
	private static final Logger log = Logger.getLogger(CalculateChange.class);// 获得日志打印对象
	private static A4MainAcctSnapService a4MainAcctSnapService;
	private static A4MainAcctJtService a4MainAcctJtService;

	/**
	 * @Title: main
	 * @author huangpumm@asiainfo-linkage.com
	 * @Description: 【计算主账号变动率】 <算法>
	 * 
	 * <p>
	 * 一、模拟集团表主账号月快照（A）
	 * 主账号当前表的数据是由9月1日正式考核后省上传的SMMAF文件入库后，然后由SMMAI日增量文件更新的最新主账号记录信息表。
	 * 集团每月主账号当前表做快照保存，供使用率和变动率使用。 <br>
	 * #A=SMMAF+SMMAI（这个工作是集合程序来做的）
	 * </p>
	 * <p>
	 * 二、省上传主账号月文件（B） 每天0点省公司对主账号当前信息做快照，以便生成SMMAF文件。<br>
	 * #B=SMMAF(这是工作是省端来做的)
	 * </p>
	 * <p>
	 * 三、变动率说明 使用率 =  ( |A中loginname minus B中loginname 的记录数| +
	 * |B中loginname minus A中loginname 的记录数| +
	 * AB按照loginname检查状态和类型不一致记录数 ) / A的记录数 * 100
	 * </p>
	 * <p>
	 * 四、使用率若发生较大变动（超过5%），扣0.20分。
	 * </p>
	 * <p>
	 * <思路>
	 * </p>
	 * 一、对A数据的模拟：该模拟方法在稽核程序中实现{@link}UpdateJTSource <br>
	 * 二、对B数据的模拟：该模拟方法在稽核程序中实现{@link}UpdateJTSource<br>
	 * 三、变动率的计算： 1、查询集团表23日(动态改变)之前所有的有效主账号全量(简称全量)构成A<br>
	 * 2、查询本地表24日(动态改变)之前所有的有效主账号全量(简称全量)构成B <br>
	 * 3、查询在前一天之前在A中但不在B中的主账号 <br>
	 * 4、查询在前一天之前在B中但不在A中的主账号 <br>
	 * 5、查询在前一天AB中都有但是状态和账号类型不一致得<br>
	 * 四、将集体表和日快照表中差异的账号保存到差异表中
	 * <p>
	 * <涉及到的表结构> <br>
	 * 1、A4_MAIN_ACCT_JT 模拟集团表<br>
	 * 2、A4_MAIN_ACCT_SNAP 模拟集团快照表 <br>
	 * 3、A4_MAIN_ACCT_CHANGE_DO 变动率结果汇总表<br>
	 * 4、A4_MAIN_ACCT_JT_DIFFER 将不同的保存到差异表中
	 * </p>
	 */
	public static void main(String[] args) {
		try {
			log.error(" now begin calculate main_acct_change.......");
			String beginTime = TimeAndOtherUtil.getLastDayStartTimeStr();
			String endTime = TimeAndOtherUtil.getCurrentDateTimeStr();
			HashMap<String, String> betweenHashMap = new HashMap<String, String>();
			betweenHashMap.put("beginTime", beginTime);
			betweenHashMap.put("endTime", endTime);
			log.error("<!--beginTime=" + beginTime + " endTime=" + endTime + "-->");
			// updateJTSource(betweenHashMap);
			start(betweenHashMap);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}

	public static void start(HashMap<String, String> parameterMap) {
		try {
			double jtChange = changeDu(parameterMap);
			// 保存详细差异主账号
			log.error("save different acct .....");
			saveDiffer();
			// 6、短信提醒
			DBUtil.notice("【4A】请注意，截止到" + TimeAndOtherUtil.timeToStringFormater(new Date(System.currentTimeMillis()))
					+ "，主账号变动率为：" + TimeAndOtherUtil.bFormater(jtChange));
		} catch (Exception e) {
			log.error(" when calculate main acct change ,occor error~~~~~");
		}
	}

	/**
	 * @Title: saveDiffer
	 * @author huangpumm@asiainfo-linkage.com
	 * @Description: 保存差异主账号信息
	 * @return void
	 * @param conn
	 *            数据库连接对象
	 * @throws Exception
	 */
	private static void saveDiffer() throws Exception {
		Connection conn = DBUtil.getAiuap20Connection();
		String truncateABDiffer = SQLUtil.getSql("truncate_different_acct");
		PreparedStatement prepStmtTrun = conn.prepareStatement(truncateABDiffer);
		ResultSet rsTrun = prepStmtTrun.executeQuery();
		DBUtil.closeResultSet(rsTrun);
		DBUtil.closePrepStmt(prepStmtTrun);

		String abDiffer = SQLUtil.getSql("insert_different_acct");
		PreparedStatement prepStmt = conn.prepareStatement(abDiffer.toString());
		ResultSet rs = prepStmt.executeQuery();

		DBUtil.closeResultSet(rs);
		DBUtil.closePrepStmt(prepStmt);
	}

	/**
	 * @Title: changeDu
	 * @author huangpumm@asiainfo-linkage.com
	 * @Description: 计算主账号变动率方法(杜总版本)
	 * @return double 主账号变动率
	 * @param timeMap
	 *            计算主账号变动率的时间区间,beginTime ,endTime
	 * @throws Exception
	 */
	public static double changeDu(HashMap<String, String> timeMap) throws Exception {
		String beginTime = timeMap.get("beginTime");
		String endTime = timeMap.get("endTime");
		long dayacctchangejt = selectDayAdd(beginTime, endTime);

		// 查询集团表23日之前所有的有效主账号全量(简称全量)
		long fullmainacctjt = selectSumA(beginTime);

		// 查询本地表24日之前所有的有效主账号全量(简称全量)构成B
		long fullmainacctlocal = selectFullMainAcctLocal(endTime);

		// A-B || B-A ||AB账号差异，构成主账号变动率分子(简称分子)
		// 1、查询在前一天之前在A中但不在B中的主账号
		Long inanotinb = checkA(endTime);
		// 2、查询在前一天之前在B中但不在A中的主账号
		Long inbnotina = checkB(endTime);
		// 3、查询在前一天AB中都有但是状态和账号类型不一致得
		Long abdiffer = compareAB(endTime);
		// 计算变动率C=(分子)/分母
		long up = inanotinb + inbnotina + abdiffer;
		double down = fullmainacctjt;
		double jtchange = up / down;
		System.out.println("inanotinb:" + inanotinb + "  inbnotina:" + inbnotina + " abdiffer:" + abdiffer);
		System.out.println("fullmainacctjt:" + fullmainacctjt);
		System.out.println("jtchange:" + jtchange);

		// 保存到数据库A4_MAIN_ACCT_CHANGE_DO表中
		String temp = SQLUtil.getSql("a4_main_acct_change_do");
		HashMap<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("jtchange", String.valueOf(jtchange));
		parameterMap.put("fullmainacctjt", String.valueOf(fullmainacctjt));
		parameterMap.put("dayacctchangejt", String.valueOf(dayacctchangejt));
		parameterMap.put("fullmainacctlocal", String.valueOf(fullmainacctlocal));
		parameterMap.put("inanotinb", String.valueOf(inanotinb));
		parameterMap.put("inbnotina", String.valueOf(inbnotina));
		parameterMap.put("fullmainacctjt", String.valueOf(fullmainacctjt));
		String mainChange = SQLUtil.replaceParameter(temp, parameterMap);
		SaveChange(mainChange);
		return jtchange;
	}

	/**
	 * @Title: selectDayAdd
	 * @author huangpumm@asiainfo-linkage.com
	 * @Description: 查询日增量主账号
	 * @return long
	 * @param beginTime
	 *            计算日增量开始时间
	 * @param endTime
	 *            计算日增量结束时间
	 */
	private static long selectDayAdd(String beginTime, String endTime) {
		try {
			Connection uapConnection = DBUtil.getAiuap20Connection();
			StringBuffer fullAcct = new StringBuffer(300);
			String tempSql = SQLUtil.getSql("main_acct_day_add");
			HashMap<String, String> parameterMap = new HashMap<String, String>();
			parameterMap.put("beginTime", beginTime);
			parameterMap.put("endTime", endTime);
			String mainAcctDayAddFileCountSql = SQLUtil.replaceParameter(tempSql, parameterMap, true);
			fullAcct.append(mainAcctDayAddFileCountSql);
			PreparedStatement prepStmt = uapConnection.prepareStatement(fullAcct.toString());
			ResultSet rs = prepStmt.executeQuery();
			Long count = 0L;
			while (rs.next()) {
				count = rs.getLong(1);
			}
			DBUtil.closeResultSet(rs);
			DBUtil.closePrepStmt(prepStmt);
			return count;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0L;
	}

	/**
	 * @Title: selectFullMainAcctLocal
	 * @author huangpumm@asiainfo-linkage.com
	 * @Description: 查询本地主账号全量总数
	 * @return long 主账号全量总数
	 * @param endTime
	 *            查询本地主账号截止时间
	 */
	private static long selectFullMainAcctLocal(String endTime) {
		try {
			Connection uapConnection = DBUtil.getAiuap20Connection();
			String fullAcct = SQLUtil.getSql("main_acct_local_count");
			PreparedStatement prepStmt = uapConnection.prepareStatement(fullAcct);
			ResultSet rs = prepStmt.executeQuery();
			Long count = 0L;
			while (rs.next()) {
				count = rs.getLong(1);
			}
			DBUtil.closeResultSet(rs);
			DBUtil.closePrepStmt(prepStmt);
			return count;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0L;
	}

	/**
	 * @Title: SaveChange
	 * @author huangpumm@asiainfo-linkage.com
	 * @Description: 保存计算结果
	 * @return void
	 * @param conn
	 *            数据库连接对象
	 * @param stringBuffer
	 *            数据库执行的sql语句
	 */
	public static void SaveChange(String string) {
		try {
			Connection conn = DBUtil.getAiuap20Connection();
			PreparedStatement prepStmt = conn.prepareStatement(string);
			prepStmt.executeUpdate();
			DBUtil.closePrepStmt(prepStmt);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Title: checkB
	 * @author huangpumm@asiainfo-linkage.com
	 * @Description: 查询在本地快照表中但不在模拟集团表中数据
	 * @return long
	 * @param endTime
	 */
	private static long checkB(String endTime) {
		try {
			Connection uapConnection = DBUtil.getAiuap20Connection();
			String inBNotInA = SQLUtil.getSql("acct_in_local_not_in_jt");
			PreparedStatement prepStmt = uapConnection.prepareStatement(inBNotInA);
			ResultSet rs = prepStmt.executeQuery();
			Long count = 0L;
			while (rs.next()) {
				count = rs.getLong(1);
			}
			DBUtil.closeResultSet(rs);
			DBUtil.closePrepStmt(prepStmt);
			return count;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * @Title: checkA
	 * @author huangpumm@asiainfo-linkage.com
	 * @Description: 查询在模拟集团表中但不在本地快照表中数据
	 * @return long
	 * @param
	 * @param endTime
	 * @param
	 * @return
	 * @param
	 * @throws Exception
	 */
	private static long checkA(String endTime) throws Exception {
		Connection uapConnection = DBUtil.getAiuap20Connection();
		String inANotInB = SQLUtil.getSql("acct_in_JT_not_in_local");
		PreparedStatement prepStmt = uapConnection.prepareStatement(inANotInB);
		ResultSet rs = prepStmt.executeQuery();
		Long count = 0L;
		while (rs.next()) {
			count = rs.getLong(1);
		}
		DBUtil.closeResultSet(rs);
		DBUtil.closePrepStmt(prepStmt);
		return count;
	}

	/**
	 * @Title: compareAB
	 * @author huangpumm@asiainfo-linkage.com
	 * @Description: 对比模拟集团表主账号和本地日快照主账号数据
	 * @return long
	 * @param endTime
	 * @throws Exception
	 */
	private static long compareAB(String endTime) throws Exception {
		Connection uapConnection = DBUtil.getAiuap20Connection();
		String inANotInB = SQLUtil.getSql("compare_local_and_jt_acct");
		PreparedStatement prepStmt = uapConnection.prepareStatement(inANotInB.toString());
		ResultSet rs = prepStmt.executeQuery();
		Long count = 0L;
		while (rs.next()) {
			count = rs.getLong(1);
		}
		DBUtil.closeResultSet(rs);
		DBUtil.closePrepStmt(prepStmt);
		return count;
	}

	/**
	 * @Title: selectSumA
	 * @author huangpumm@asiainfo-linkage.com
	 * @Description: 计算模拟主账号总数
	 * @return Long
	 * @param endTime
	 */
	private static Long selectSumA(String endTime) throws Exception {
		Connection uapConnection = DBUtil.getAiuap20Connection();
		String inBNotInA = SQLUtil.getSql("main_acct_jt_count");
		PreparedStatement prepStmt = uapConnection.prepareStatement(inBNotInA);
		ResultSet rs = prepStmt.executeQuery();
		Long count = 0L;
		while (rs.next()) {
			count = rs.getLong(1);
		}
		DBUtil.closeResultSet(rs);
		DBUtil.closePrepStmt(prepStmt);
		return count;
	}

	/**
	 * @Title: calculateChange
	 * @author huangpumm@asiainfo-linkage.com
	 * @Description: 计算主账号变动率
	 * @return Double
	 * @param sumA
	 * @param inANotInB
	 *            在模拟集团表中但不在本地日快照表中
	 * @param inBNotInA
	 *            在本地日快照表中但不在模拟集团表中
	 */
	private static Double calculateChange(long sumA, long inANotInB, long inBNotInA) {
		Double inADouble = Double.valueOf(inANotInB);
		Double inBDouble = Double.valueOf(inBNotInA);
		return (inADouble + inBDouble) / sumA;
	}

	/**
	 * 【更新数据源思路】 1、两个时间点确定一个取值区间，将这个取值区间内的日快照表中数据取出，放到一个链表中。
	 * 2、顺次从该链表中提取主账号信息(比如简称M)，依据M的标签信息(M的标签取值空间为{add,del,upd})与模拟集团表中主账号比对。
	 * 3、比对结果可能出现三中情况 ▼ 如果M不在模拟集团表中，直接将M添加到模拟集团表中。 ▼ 如果M在模拟集团表中,在依据M的标签信息判定
	 * ▶如果M标签为del，则从模拟集团表中将该主账号删除。 ▶ 如果M标签为upd，则依据M的login_name属性，更新M的其他所有属性。
	 */
	private static void updateJTSource(HashMap<String, String> timeMap) {
		try {
			//
			// 需要添加三个日期,解释如下
			// 1、create_time 主账号开始时间,create_time_after
			// 主账号创建结束时间，对应从主账号表中查询当天新加的主账号 。
			// 2、last_update_time 主账号更改开始时间,last_update_time_after 主账号更改结束时间，
			// 对应从主账号表中查询当天更改的主账号 。
			// 3、last_update_time 主账号删除开始时间,last_update_time_after 主账号删除结束时间，
			// 对应从主账号历史表中查询当天删除的主账号 。
			//
			// 1、从生产数据库中查找所有在creat_time区间得主账号
			List<A4MainAcctSnap> uapMainAcct2 = a4MainAcctSnapService.selectMainAcctByTime(timeMap);
			for (A4MainAcctSnap uapMainAcct3 : uapMainAcct2) {
				// 2、依据主账号表中的主账号登陆名称查找主账号集团表
				HashMap<String, Object> parMap = new HashMap<String, Object>();
				parMap.put("login_name", uapMainAcct3.getLoginName());
				List<A4MainAcctJt> jtAcctList = a4MainAcctJtService.getMainAcctJTByName(parMap);
				// 如果能查到主账号，说明主账号发生过变更，包括更改和删除
				if (jtAcctList.size() > 0) {
					for (int i = 0; i < jtAcctList.size(); i++) {
						A4MainAcctJt jtAcct = jtAcctList.get(i);
						A4MainAcctJt copy = copyFromLocalToJT(uapMainAcct3, jtAcct);

						if ("del".equals(uapMainAcct3.getModifyMode())) {
							// 1、因为主账号被删除，然后再将主账号添加到主账号集团表中
							a4MainAcctJtService.delMainAcctJT(copy);
							System.out.println("================qq");
							continue;
						}
						copy.setModifyMode("upd");
						a4MainAcctJtService.updateMainAcctJT(copy);
					}
				} else {
					// 如果查询不到就直接添加到集团表中
					A4MainAcctJt copy = copyFromLocalToJT(uapMainAcct3, new A4MainAcctJt());
					copy.setModifyMode("add");
					a4MainAcctJtService.insertMainAcctJT(copy);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * @Title: copyFromLocalToJT
	 * @author huangpumm@asiainfo-linkage.com
	 * @Description: 将本地主账号对象转化成模拟集团表对象
	 * @return A4MainAcctJt
	 * @param uapMainAcct3
	 *            本地主账号对象
	 * @param jtAcct
	 *            模拟集团表对象
	 * @return A4MainAcctJt 集团表对象
	 */
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
		jtAcct.setLogondays(uapMainAcct3.getLogondays());
		jtAcct.setMainAcctId(uapMainAcct3.getMainAcctId());
		jtAcct.setModifyMode(uapMainAcct3.getModifyMode());
		jtAcct.setOpendays(uapMainAcct3.getOpendays());
		jtAcct.setOrgId(uapMainAcct3.getOrgId());
		jtAcct.setOrgname(uapMainAcct3.getOrgname());
		jtAcct.setRolelist(uapMainAcct3.getRolelist());
		jtAcct.setSuperuser(uapMainAcct3.getSuperuser());
		jtAcct.setUpdateTime(uapMainAcct3.getUpdateTime());
		jtAcct.setUserName(uapMainAcct3.getUserName());
		jtAcct.setValid(uapMainAcct3.getValid());
		return jtAcct;
	}

}