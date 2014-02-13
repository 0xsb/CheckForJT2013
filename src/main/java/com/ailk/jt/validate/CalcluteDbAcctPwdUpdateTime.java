package com.ailk.jt.validate;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import com.ailk.jt.util.DBUtil;
import com.ailk.jt.util.PropertiesUtil;
import com.ailk.jt.util.TimeAndOtherUtil;

public class CalcluteDbAcctPwdUpdateTime {

	private static final Logger log = Logger.getLogger(CalcluteDbAcctPwdUpdateTime.class);
	private static String bossUrlServer = PropertiesUtil.getValue("boss_url_server");
	private static String bossjdbcusername = PropertiesUtil.getValue("boss_jdbc_username");
	private static String cbossUrlServer = PropertiesUtil.getValue("cboss_url_server");
	private static String cbossjdbcusername = PropertiesUtil.getValue("cboss_jdbc_username");
	private static Connection conn;
	private static ResultSet rs;
	private static PreparedStatement prepStmt;
	private static long spaceDay;

	/**
	 * 该方法主要是为了监控集团考核中涉及到的数据库程序账号密码修改时间是否接近三个月 如果不足15天则发短信通知修改
	 */

	public static long CalcluteDbAcct(String serviceName, String acctName) {
		conn = DBUtil.getAiuap20Connection();
		String dbSql = "select (to_date(to_char(sysdate, 'YYYY-MM-DD'), 'YYYY-MM-DD')-to_date(to_char(b.pwd_update_time, 'YYYY-MM-DD'), 'YYYY-MM-DD'))"
				+ " from uap_db a, uap_db_acct b "
				+ "where b.db_id = a.db_id and a.service_name = '"
				+ serviceName
				+ "' and b.acct_name = '" + acctName + "'";
		log.info("dbSql====" + dbSql);
		try {
			prepStmt = conn.prepareStatement(dbSql);
			rs = prepStmt.executeQuery();
			while (rs.next()) {
				spaceDay = rs.getLong(1);
			}
			log.info("spaceDay===" + spaceDay);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeConnection(conn);
			DBUtil.closePrepStmt(prepStmt);
			DBUtil.closeResultSet(rs);
		}
		return spaceDay;
	}

	public static boolean checkDbPassWord(String type) {
		Connection bossConnection = null;
		try {

			String driverClass = PropertiesUtil.getValue(type + "_jdbc_driverClassName");
			String url = PropertiesUtil.getValue(type + "_url_server");
			String userName = PropertiesUtil.getValue(type + "_jdbc_username");
			String password = PropertiesUtil.getValue(type + "_jdbc_password");

			Class.forName(driverClass);
			log.debug("<!--url=" + url + "-->" + "<!--userName=" + userName + "-->" + "<!--password=" + password
					+ "-->");
			bossConnection = DriverManager.getConnection(url, userName, password);
			log.debug("<!--get "+type.toUpperCase()+"Connection successfully-->");

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e);
		} finally {
			if (bossConnection != null)
				try {
					bossConnection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return false;
	}

	public static void main(String[] args) {

		boolean bossflag = checkDbPassWord("boss");
		boolean cbossflag = checkDbPassWord("cboss");
		StringBuffer message4Boss = new StringBuffer("【4A】截止到" + TimeAndOtherUtil.getCurrentDateTimeStr() + "");
		StringBuffer message4Cboss = new StringBuffer("【4A】截止到" + TimeAndOtherUtil.getCurrentDateTimeStr() + "");

		long days4Boss = CalcluteDbAcct(bossUrlServer.substring(bossUrlServer.lastIndexOf(":") + 1, bossUrlServer
				.length()), bossjdbcusername.toUpperCase());
		long days4Cboss = CalcluteDbAcct(cbossUrlServer.substring(cbossUrlServer.lastIndexOf(":") + 1, cbossUrlServer
				.length()), cbossjdbcusername.toUpperCase());
		message4Boss.append(",").append(bossUrlServer).append("密码距离过期还有").append(days4Boss).append("天，请及时修改！ ");
		log.info(message4Boss.toString());
		message4Cboss.append(",").append(cbossUrlServer).append("密码距离过期还有").append(days4Cboss).append("天，请及时修改！");
		log.info(message4Cboss.toString());
		if (!bossflag) {
			DBUtil.notice(message4Boss.append("该库连接失败！！！").toString());
		} else if (days4Boss < 15) {
			DBUtil.notice(message4Boss.toString());
		}
		if (!cbossflag) {
			DBUtil.notice(message4Cboss.append("该库连接失败！！！").toString());
		} else if (days4Cboss < 15) {
			DBUtil.notice(message4Cboss.toString());
		}
	}

}
