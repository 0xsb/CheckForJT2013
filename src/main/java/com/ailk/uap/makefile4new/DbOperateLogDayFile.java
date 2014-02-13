package com.ailk.uap.makefile4new;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.fileupload.util.Streams;

import com.ailk.jt.util.DBUtil;
import com.ailk.jt.util.DateUtil;
import com.ailk.jt.util.SQLUtil;
import com.ailk.uap.dbconn.ConnectionManager;

public class DbOperateLogDayFile extends AbstractMakeDayFile {

	public static void main(String[] args) {
		AbstractMakeFile makeFile = new DbOperateLogDayFile();
    	makeFile.makeFile();
	}

	private static void updateDateSource() {
		Connection conn = DBUtil.getAuditConnection();

		String truncate_Sql = SQLUtil.getSql("truncate_table_a4_iap_device_session");
		DBUtil.executeSQL(conn, truncate_Sql);
		log.info("truncate_Sql=======" + truncate_Sql);

		String db_day_file = SQLUtil.getSql("db_day_file");
		HashMap<String,String> parameterMap = new HashMap<String,String>();

		Calendar cal = Calendar.getInstance();
		cal.add(5, -1);
		Date date = cal.getTime();
		String dataBasePart = DateUtil.formatDateyyyyMM(date);
//		String dataBasePart = "201205";
		parameterMap.put("dataBasePart", "PART_DEVICE_SESSION_" + dataBasePart);
		parameterMap.put("beginTime", beginTime);
		parameterMap.put("endTime", endTime);
		String mainAcctDayAddFileCountSql = SQLUtil.replaceParameter(db_day_file, parameterMap, true);
		log.info("mainAcctDayAddFileCountSql=======" + mainAcctDayAddFileCountSql);
		DBUtil.executeSQL(conn, mainAcctDayAddFileCountSql);
 
		String truncateTestDeviceSession = SQLUtil.getSql("truncate_test_device_session");
		DBUtil.executeSQL(conn, truncateTestDeviceSession);
		log.info("truncate_test_device_session======"+truncateTestDeviceSession);
		
		String sepreate_device = SQLUtil.getSql("sepreate_device");
		DBUtil.executeSQL(conn, sepreate_device);
		log.info("sepreate_device=======" + sepreate_device);

		String truncate_second_final_device_session = SQLUtil.getSql("truncate_second_final_device_session");
		DBUtil.executeSQL(conn, truncate_second_final_device_session);
		log.info("truncate_second_final_device_session=======" + truncate_second_final_device_session);
		String second_final_device_session = SQLUtil.getSql("second_final_device_session");
		DBUtil.executeSQL(conn, second_final_device_session);
		log.info("second_final_device_session=======" + second_final_device_session);

		String final_device = SQLUtil.getSql("final_device");
		DBUtil.executeSQL(conn, final_device);
		log.info("final_device=======" + final_device);
	}

	private static void generateDbOperateLogDayFile() throws Exception, IOException, FileNotFoundException,
			UnsupportedEncodingException {
		StringBuffer subAcctFileBuffer = new StringBuffer();
		subAcctFileBuffer.append("<?xml version='1.0' encoding='UTF-8'?>\r\n");
		subAcctFileBuffer.append("<smp>\r\n");
		subAcctFileBuffer.append("<type>SMDAR</type>\r\n");
		subAcctFileBuffer.append("<province>" + prov_code + "</province>" + "\r\n");
		subAcctFileBuffer.append("<createtime>" + createTime.replaceAll(" ", "T") + "</createtime>" + "\r\n");

		String subAcctDeviceIds = "";

		subAcctDeviceIds = SQLUtil.getSql("count_sys_device");
		log.info("subAcctDeviceIdsSql===" + subAcctDeviceIds);

		int belongsSya = getDeviceBelongsSys(subAcctDeviceIds);
		if (belongsSya != 0)
			sum = belongsSya * 48;
		else {
			sum = 0L;
		}
		log.info("total sum ===432");
		subAcctFileBuffer.append("<sum>" + sum + "</sum>" + "\r\n");
		subAcctFileBuffer.append("<begintime>" + beginTime.replace(" ", "T") + "</begintime>" + "\r\n");
		subAcctFileBuffer.append("<endtime>" + endTime.replace(" ", "T") + "</endtime>" + "\r\n");
		subAcctFileBuffer.append("<data>\r\n");

		log.info("getDbOperateLogInfo*******************Start*********");
		if (sum != 0L) {
			getSubAcctInfo(subAcctFileBuffer);
		}
		log.info("getDbOperateLogInfo*******************End*********");

		subAcctFileBuffer.append("</data>\r\n");
		subAcctFileBuffer.append("</smp>");

		log.info("subAcctFileBuffer====" + subAcctFileBuffer.toString());

		uapLoadTempFile = new File(uap_file_uapload_temp + "/" + uploadFileName);

//		File uapLoadTempFile = new File(uap_file_uapload_temp + "/" + uploadFileName);
		if (uapLoadTempFile.exists()) {
			uapLoadTempFile.delete();
			uapLoadTempFile.createNewFile();
		}
		fos = new FileOutputStream(uapLoadTempFile, true);
		output = new OutputStreamWriter(fos, "UTF-8");
		bw = new BufferedWriter(output);
		writeFileBufferToTempFile(subAcctFileBuffer);

		BufferedInputStream in = new BufferedInputStream(new FileInputStream(uapLoadTempFile));
		File uapLoadFile = new File(uap_file_uapload + "/" + uploadFileName);
		if (!uapLoadFile.exists()) {
			uapLoadFile.createNewFile();
		}
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(uapLoadFile));
		Streams.copy(in, out, true);
		in.close();
		out.close();
	}

	private static int getDeviceBelongsSys(String sql) throws Exception {
		PreparedStatement prepStmt = conn.prepareStatement(sql);
		ResultSet rs = prepStmt.executeQuery();
		int dbIds = 0;
		while (rs.next()) {
			dbIds = rs.getInt("total_sys_device");
		}
		ConnectionManager.closeResultSet(rs);
		ConnectionManager.closePrepStmt(prepStmt);

		return dbIds;
	}

	private static void getSubAcctInfo(StringBuffer mainAcctFileBuffer) throws Exception {
		String truncate_Sql = SQLUtil.getSql("g_file");
		ResultSet rs = DBUtil.getQueryResultSet(conn, truncate_Sql);
		int i = 1;
		while (rs.next()) {
			mainAcctFileBuffer.append("<rcd>\r\n");
			mainAcctFileBuffer.append("<seq>" + i + "</seq>" + "\r\n");
			mainAcctFileBuffer.append("<restype>" + rs.getString("belong_sys") + "</restype>" + "\r\n");
			mainAcctFileBuffer.append("<num>" + rs.getString("login_hour") + "</num>" + "\r\n");
			mainAcctFileBuffer.append("<value>" + rs.getString("total") + "</value>" + "\r\n");
			mainAcctFileBuffer.append("</rcd>\r\n");
			i++;
		}
	}

	@Override
	public void generateMakeFile() throws Exception {
		updateDateSource();
		generateDbOperateLogDayFile();
	}

	@Override
	public String getFileType() {
		return super.TYPE_SMDAR;
	}
	
	protected  Connection getConn() {
		return DBUtil.getAuditConnection();
	}
	
}