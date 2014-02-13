package com.ailk.uap.dbconn;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ailk.uap.config.PropertiesUtil;
import com.asiainfo.uap.util.des.EncryptInterface;


public class ConnectionManager {

	private static Log logger = LogFactory.getLog(ConnectionManager.class);

	public static Connection getUapAcctConnection() throws Exception {

		String driverClass = PropertiesUtil.getValue("uap_jdbc_driverClassName");
		String url  =  PropertiesUtil.getValue("uap_url_server");
		String userName = PropertiesUtil.getValue("uap_jdbc_username");
		String enCryptedPwd = PropertiesUtil.getValue("uap_jdbc_password");
		String password = EncryptInterface.desUnEncryptData(enCryptedPwd);
		
		Class.forName(driverClass);
		logger.debug("url=="+url);
		logger.debug("userName=="+userName);
		logger.debug("password=="+password);
		Connection  con = DriverManager.getConnection(url, userName, password);
		logger.debug("Connected successfully");
		return con;
	
	}
	
	public static Connection getUapAuditConnection() throws Exception {

		String driverClass = PropertiesUtil.getValue("iap_jdbc_driverClassName");
		String url  =  PropertiesUtil.getValue("iap_url_server");
		String userName = PropertiesUtil.getValue("iap_jdbc_username");
		String enCryptedPwd = PropertiesUtil.getValue("iap_jdbc_password");
		String password = EncryptInterface.desUnEncryptData(enCryptedPwd);
//		String password = enCryptedPwd;
		Class.forName(driverClass);
		logger.debug("url=="+url);
		logger.debug("userName=="+userName);
		logger.debug("password=="+password);
		Connection  con = DriverManager.getConnection(url, userName, password);
		logger.debug("Connected successfully");
		return con;
	
	}

	public static void closeConnection(Connection con) {
		try {
			if (con != null)
				con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void closePrepStmt(PreparedStatement prepStmt) {
		try {
			if (prepStmt != null)
				prepStmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void closeResultSet(ResultSet rs) {
		try {
			if (rs != null)
				rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		try {
			getUapAuditConnection();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
