package com.ailk.jt.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;

public class StringUtil {
	private static Logger log = Logger.getLogger(StringUtil.class);// 获取打印日志工具类对象

	/**
	 * @Title: main
	 * @Description:审计数据库中根据审计内容操作工具类
	 */

	public static void main(String[] args) {
		try {
			Connection connection = DBUtil.getAuditConnection();
			String singleAddSQL = SQLUtil.getSql("a4_select_app_acct_change");
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery(singleAddSQL);
			String acct_change_mode = "";
			String operate_content = "";
			while (rs.next()) {
				// t.operate_type_id,t.operate_type_name,t.operate_content,t.is_batch,t.acct_change_mode
				acct_change_mode = rs.getString("acct_change_mode");
				operate_content = rs.getString("operate_content");
				if ("add".equals(acct_change_mode)) {
                    String login_acct=  StringUtil.getSingleAcct(operate_content);
//                    A4BossAcctFromLogDao a=  (A4BossAcctFromLogDao) SpringUtil.getBean("a4BossAcctFromLogDao");
//                  Datas SpringUtil.getBean("iapdataSource");
//                    a.setDataSource(audit);
                    
                   log.debug("========="+login_acct);
				} else {

				}
			}

		} catch (Exception e) {
		}
	}

	private static void testContent() {
		// 单个增加应用帐号 1-AIUAP-20119 增加应用帐号
		String singleAdd = "创建应用帐号成功：应用系统=BOSS系统,帐号标识=2,000,054,129,帐号名称=G948958";
		log.debug("增加单个账号:" + getSingleAcct(singleAdd));

		// 批量增加的boss账号总数SQL 1-AIUAP-20134 批量增加应用帐号
		String batchAdd = "管理员[liyan80]于时间[2012-06-07 16:53:45.0]批量新增应用帐号，批量工单号：[256187]，详细信息可在批量明细中查询";
		log.debug("批量增加的工单号:" + getBatchSVNSN(batchAdd));

		// 单个删除的boss账号总数SQL 1-AIUAP-20121 删除应用帐号
		String singelDel = "停用(失效)应用帐号成功：帐号标识=21,662,帐号名称=N720125";
		log.debug("删除单个账号:" + getSingleAcct(singelDel));

		// 单个停用boss账号总数SQL 1-AIUAP-20132 应用帐号停用
		String singleLogicDel = "";

		// 批量删除停用 1-AIUAP-20136 批量删除(停用)应用帐号
		String batchLogicDel = "管理员[sunrui3]于时间[2012-06-08 11:24:38.0]批量删/停用除应用帐号，批量工单号：[256264]，详细信息可在批量明细中查询";
		log.debug("批量删除(停用)工单号：" + getBatchSVNSN(batchLogicDel));
	}

	/**
	 * @Title: getSingleAcctAdd
	 * @Description: 通过审计日志查找单个账号
	 * @param content
	 *            审计日志的操作内容字段内容
	 * @return String 返回BOSS工号
	 */
	public static String getSingleAcct(String content) {
		int begin = content.lastIndexOf("=");
		int end = content.length();
		String result = content.substring(begin + 1, end);
		return result;
	}

	/**
	 * @Title: getBatchSVNSN
	 * @Description:通过审计日志查找批量账号的工单
	 * @param 审计日志的操作内容字段内容
	 * @return String 工单号
	 */
	public static String getBatchSVNSN(String content) {
		int begin = content.lastIndexOf("[");
		int end = content.lastIndexOf("]");
		String result = content.substring(begin + 1, end);
		return result;
	}
}
