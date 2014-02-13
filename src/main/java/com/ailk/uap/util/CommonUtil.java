package com.ailk.uap.util;

import com.ailk.uap.config.PropertiesUtil;
import com.ailk.uap.dbconn.ConnectionManager;
import com.ailk.uap.entity.DrUploadFileInfo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import org.apache.log4j.Logger;

public class CommonUtil {
	private static final Logger log = Logger.getLogger(CommonUtil.class);

	public static void main(String[] args) {
	}

	public static void readConfig(String uap_file_uapload,String uap_version,String prov_code,String uap_file_uapload_temp) {
		uap_file_uapload = PropertiesUtil.getValue("uap_file_uapload_for_day")
				.trim();
		uap_version = PropertiesUtil.getValue("uap_version").trim();
		prov_code = PropertiesUtil.getValue("prov_code").trim();
		uap_file_uapload_temp = PropertiesUtil
				.getValue("uap_file_uapload_temp").trim();
	}
	public static void setValues(DrUploadFileInfo fileInfo,String uploadFileName,String fileSeq,String style,String prov_code,String reloadFlag,long sum,String type ){
		fileInfo.setFileName(uploadFileName);
		fileInfo.setFileSeq(fileSeq);
		fileInfo.setIntval(style);
		fileInfo.setProv(prov_code);
		fileInfo.setReloadFlag(reloadFlag);
		fileInfo.setTotal(Long.valueOf(sum));
		fileInfo.setType(type);
		fileInfo.setUploadStatus("0");
	}
	
	public static void getXmlHeadString(String type, String prov_code,
			String createTime, StringBuffer acctFileBuffer, Long sum,
			String beginTime, String endTime) throws Exception {
		acctFileBuffer.append("<?xml version='1.0' encoding='UTF-8'?>\r\n");
		acctFileBuffer.append("<smp>\r\n");
		acctFileBuffer.append("<type>" + type + "</type>" + "\r\n");
		acctFileBuffer
				.append("<province>" + prov_code + "</province>" + "\r\n");
		acctFileBuffer.append("<createtime>" + createTime + "</createtime>"
				+ "\r\n");
		acctFileBuffer.append("<sum>" + sum + "</sum>" + "\r\n");
		acctFileBuffer.append("<begintime>" + beginTime + "</begintime>"
				+ "\r\n");
		acctFileBuffer.append("<endtime>" + endTime + "</endtime>" + "\r\n");
		acctFileBuffer.append("<data>\r\n");
	}

	public static String getStr(String str) {
		return str == null ? "" : str.trim();
	}

	public static String getTime(Timestamp timeTemp) {
		String rltStr = "";
		if (timeTemp != null)
			try {
				rltStr = DatetimeServices.converterToDateTimeWithT(timeTemp);
			} catch (Exception e) {
			}
		return rltStr;
	}

	public static String changeAcctStatus(String lockStatus) throws Exception {
		if ((lockStatus != null) && (lockStatus.equals("0")))
			return "1";
		if ((lockStatus != null) && (lockStatus.equals("1"))) {
			return "0";
		}
		return lockStatus == null ? "" : lockStatus;
	}

	public static String changeAcctType(String version, String acctType)
			throws Exception {
		if (version.equalsIgnoreCase("uap2.0")) {
			return acctType == null ? "" : acctType;
		}
		if (acctType != null) {
			if (acctType.equals("0"))
				return "00";
			if (acctType.equals("1"))
				return "03";
			if (acctType.equals("2"))
				return "04";
			if (acctType.equals("3"))
				return "01";
			if (acctType.equals("4")) {
				return "02";
			}
			return "00";
		}

		return "";
	}

	public static void insertUploadFileInfo(DrUploadFileInfo fileInfo,
			Connection conn, String beginTime, String endTime) throws Exception {
		String sqlDel = " DELETE FROM DR_UPLOAD_FILE_INFO WHERE FILE_NAME = '"
				+ fileInfo.getFileName() + "'";
		log.debug(sqlDel);
		String sql = "INSERT INTO DR_UPLOAD_FILE_INFO VALUES('"
				+ fileInfo.getFileName() + "','" + fileInfo.getProv() + "','"
				+ fileInfo.getType() + "','" + fileInfo.getIntval() + "','"
				+ fileInfo.getFileSeq() + "','" + fileInfo.getReloadFlag()
				+ "',TO_DATE('" + DatetimeServices.getNowDateTimeStr(conn)
				+ "','yyyy-MM-dd HH24:mi:ss')," + fileInfo.getTotal()
				+ ",TO_DATE('" + beginTime
				+ "','yyyy-MM-dd HH24:mi:ss'),TO_DATE('" + endTime
				+ "','yyyy-MM-dd HH24:mi:ss'),'" + fileInfo.getUploadStatus()
				+ "')";

		log.debug(sql);
		try {
			conn.setAutoCommit(false);
			PreparedStatement prepStmtDel = conn.prepareStatement(sqlDel);
			prepStmtDel.executeUpdate();
			PreparedStatement prepStmt = conn.prepareStatement(sql);
			prepStmt.executeUpdate();
			conn.commit();
			ConnectionManager.closePrepStmt(prepStmt);
		} catch (Exception ex) {
			ex.printStackTrace();
			try {
				conn.rollback();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static String arrayToSQLString(String[] args) {
		String str = "";
		for (int i = 0; i < args.length - 1; i++) {
			str = str + "'" + args[i] + "',";
		}
		str = str + "'" + args[(args.length - 1)] + "'";
		return str;
	}
}