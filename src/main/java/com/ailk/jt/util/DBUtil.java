package com.ailk.jt.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 * @ClassName: dbUtil
 * @Description: 【操作数据库工具类】
 * @author huangpumm@asiainfo-linkage.com
 * @date May 31, 2012 12:51:54 PM
 */
public class DBUtil {
	private static Logger log = Logger.getLogger(DBUtil.class);// 获取打印日志工具类对象

	public static void main(String[] args) {
		try {
			Connection connection = getAiuap20Connection();
			log.debug(connection);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("<!--closed connection error-->");
		}
	}

	/**
	 * @Title: getUapAcctConnection
	 * @Description: 获取aiuap20用户的数据库连接信息
	 * @return 返回aiuap20用户获取的数据库连接
	 * @throws Exception
	 *             抛出无法获取数据库连接异常
	 */
	public static Connection getAiuap20Connection() {
		try {
			String driverClass = PropertiesUtil.getValue("uap_jdbc_driverClassName");
			String url = PropertiesUtil.getValue("uap_url_server");
			String userName = PropertiesUtil.getValue("uap_jdbc_username");
			String password = PropertiesUtil.getValue("uap_jdbc_password");

			Class.forName(driverClass);
			log.debug("<!--url=" + url + "-->" + "<!--userName=" + userName + "-->" + "<!--password=" + password
					+ "-->");
			Connection con = DriverManager.getConnection(url, userName, password);
			log.debug("<!--get UapConnection successfully-->");
			return con;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			log.debug("<!--get driverClass error-->");
		} catch (SQLException e) {
			e.printStackTrace();
			log.debug("<!--get UapConnection error-->");
		}
		return null;
	}

	/**
	 * @Title: getUapAuditConnection
	 * @Description: 获取审计库连接对象
	 * @throws Exception
	 *             抛出无法获得数据库连接异常
	 * @return 返回aiuap20用户获取的数据库连接对象
	 */
	public static Connection getAuditConnection() {
		try {
			String driverClass = PropertiesUtil.getValue("iap_jdbc_driverClassName");
			String url = PropertiesUtil.getValue("iap_url_server");
			String userName = PropertiesUtil.getValue("iap_jdbc_username");
			String password = PropertiesUtil.getValue("iap_jdbc_password");

			Class.forName(driverClass);
			log.debug("<!--url=" + url + "-->" + "<!--userName=" + userName + "-->" + "<!--password=" + password
					+ "-->");
			Connection con = DriverManager.getConnection(url, userName, password);
			log.debug("<!--get iapConnection successfully-->");
			return con;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			log.debug("<!--get driverClass error-->");
		} catch (SQLException e) {
			e.printStackTrace();
			log.debug("<!--get iapConnection error-->");
		}
		return null;

	}

	/**
	 * @Title: getUapAuditConnection
	 * @Description: 获取BOSS库连接对象
	 * @throws Exception
	 *             抛出无法获得数据库连接异常
	 * @return 返回BOSS用户获取的数据库连接对象
	 */
	public static Connection getBOSSConnection() {
		try {
			String driverClass = PropertiesUtil.getValue("boss_jdbc_driverClassName");
			String url = PropertiesUtil.getValue("boss_url_server");
			String userName = PropertiesUtil.getValue("boss_jdbc_username");
			String password = PropertiesUtil.getValue("boss_jdbc_password");

			Class.forName(driverClass);
			log.debug("<!--url=" + url + "-->" + "<!--userName=" + userName + "-->" + "<!--password=" + password
					+ "-->");
			Connection con = DriverManager.getConnection(url, userName, password);
			log.debug("<!--get BOSSConnection successfully-->");
			return con;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			log.debug("<!--get driverClass error-->");
		} catch (SQLException e) {
			e.printStackTrace();
			log.debug("<!--get BOSSConnection error-->");
		}
		return null;
	}

	/**
	 * @Title: closeConnection
	 * @Description: 关闭数据库连接对象
	 * @param 数据库连接对象
	 */
	public static void closeConnection(Connection con) {
		try {
			if (con != null)
				con.close();
		} catch (Exception e) {
			log.error("<!--closed connection error-->");
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				log.error("<!--closed connection error-->");
				e.printStackTrace();
			}
		}
	}

	/**
	 * @Title: closePrepStmt
	 * @param PreparedStatement
	 * @Description: 关闭数据库statement连接
	 * @param prepStmt
	 */
	public static void closePrepStmt(PreparedStatement prepStmt) {
		try {
			if (prepStmt != null)
				prepStmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Title: closeResultSet
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param ResultSet
	 */
	public static void closeResultSet(ResultSet rs) {
		try {
			if (rs != null)
				rs.close();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("<!--closed connection error-->");
		}
	}

	/**
	 * @Title: notice
	 * @author huangpumm@asiainfo-linkage.com
	 * @Description: 发送短信
	 * @return void
	 * @param message
	 *            发送的短信内容
	 */
	public static void notice(String message) {
		Connection conn = null;
		try {
			conn = DBUtil.getAiuap20Connection();
			String teString0 = PropertiesUtil.getValue("sms_people");
			String[] people = teString0.split(",");
			for (int i = 0; i < people.length; i++) {
				StringBuffer sBuffer1 = new StringBuffer(1000);
				sBuffer1
						.append("insert into uap_notice (ID, TYPE_ID, CONTENT, PARAM_A, PARAM_B, SEND_BEGIN_DATE, SEND_END_DATE, CREATE_DATE_TIME)values "
								+ "(uap_notice_seq.nextval, 'SMS', '"
								+ message
								+ "', '"
								+ people[i]
								+ "', '', '', '', (select sysdate from dual))");
				PreparedStatement prepStmt1 = conn.prepareStatement(sBuffer1.toString());
				prepStmt1.executeUpdate();
				DBUtil.closePrepStmt(prepStmt1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeConnection(conn);
		}
	}

	/**
	 * @Title: getResultSet
	 * @Description: 根据数据库连接对象和SQL，反馈查询后的结果集合
	 * @param conn
	 * @param sql
	 * @return ResultSet 返回类型
	 */
	public static ResultSet getQueryResultSet(Connection conn, String sql) {
		try {
			PreparedStatement prepStmt = conn.prepareStatement(sql);
			ResultSet rs = prepStmt.executeQuery();
			return rs;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @Title: getResultSet
	 * @Description: 执行一些sql，比如备份某一个表，删除某些数据
	 * @param conn
	 * @param sql
	 */
	public static void executeSQL(Connection conn, String sql) {
		PreparedStatement prepStmt = null;
		ResultSet result = null;
		try {
			prepStmt = conn.prepareStatement(sql);
			result = prepStmt.executeQuery(sql);
			DBUtil.closeResultSet(result);
			DBUtil.closePrepStmt(prepStmt);
		} catch (Exception e) {
			log.error("<!-- when exectue sql occur error! sql is: -->" + sql);
			DBUtil.closeResultSet(result);
			DBUtil.closePrepStmt(prepStmt);
			e.printStackTrace();
		}
	}

	/**
	 * @Title: getCountValue
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param conn
	 * @param sql
	 * @return 设定文件
	 * @return int 返回类型
	 * @throws
	 */
	public static int getCountValue(Connection conn, String sql) {
		try {
			PreparedStatement prepStmt = conn.prepareStatement(sql);
			ResultSet rs = prepStmt.executeQuery();
			int result = 0;
			while (rs.next()) {
				result = rs.getInt(1);

			}
			DBUtil.closeResultSet(rs);
			DBUtil.closePrepStmt(prepStmt);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;// 异常情况反馈-1
		}
	}

	/**
	 * @Title: getResultSet
	 * @Description: 根据数据库连接对象和SQL，反馈查询后的结果集合
	 * @param conn
	 * @param sql
	 * @return ResultSet 返回类型
	 */
	public static boolean getExecuteResultSet(Connection conn, String sql) {
		try {
			PreparedStatement prepStmt = conn.prepareStatement(sql);
			return prepStmt.execute();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static Connection getCBOSSConnection() {
		try {
			String driverClass = PropertiesUtil.getValue("cboss_jdbc_driverClassName");
			String url = PropertiesUtil.getValue("cboss_url_server");
			String userName = PropertiesUtil.getValue("cboss_jdbc_username");
			String password = PropertiesUtil.getValue("cboss_jdbc_password");

			Class.forName(driverClass);
			log.debug("<!--url=" + url + "-->" + "<!--userName=" + userName + "-->" + "<!--password=" + password
					+ "-->");
			Connection con = DriverManager.getConnection(url, userName, password);
			log.debug("<!--get CBOSSConnection successfully-->");
			return con;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			log.debug("<!--get driverClass error-->");
		} catch (SQLException e) {
			e.printStackTrace();
			log.debug("<!--get CBOSSConnection error-->");
		}
		return null;
	}

    public static Connection getSMPConnection() {
        try {
            String driverClass = PropertiesUtil.getValue("smp_jdbc_driverClassName");
            String url = PropertiesUtil.getValue("smp_url_server");
            String userName = PropertiesUtil.getValue("smp_jdbc_username");
            String password = PropertiesUtil.getValue("smp_jdbc_password");

            Class.forName(driverClass);
            log.debug("<!--url=" + url + "-->" + "<!--userName=" + userName + "-->" + "<!--password=" + password
                    + "-->");
            Connection con = DriverManager.getConnection(url, userName, password);
            log.debug("<!--get SMPConnection successfully-->");
            return con;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            log.debug("<!--get driverClass error-->");
        } catch (SQLException e) {
            e.printStackTrace();
            log.debug("<!--get SMPConnection error-->");
        }
        return null;
    }

}
