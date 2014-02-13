package com.ailk.uap.makefile4new.manual;

import com.ailk.jt.util.DBUtil;
import com.ailk.uap.config.PropertiesUtil;
import com.ailk.uap.dbconn.ConnectionManager;
import com.ailk.uap.entity.DrUploadFileInfo;
import com.ailk.uap.util.DatetimeServices;
import org.apache.commons.fileupload.util.Streams;
import org.apache.log4j.Logger;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Calendar;

/*
 * 该程序主要完成根据配置文件中的时间信息生成特定时间的文件
 * 从帐号登录操作1小时增量文件
 * type=SMSAL
 */
public class DeviceSubAcctLoginFileManual {
	private static final Logger log = Logger.getLogger(DeviceSubAcctLoginFileManual.class);
	private static String uap_file_uapload;
	private static String uap_file_uapload_temp;
	private static final String type = "SMSAL";
	private static String prov_code;
	private static final String intval = "01HR";
	private static String createTime;
	private static String beginTime;
	private static String endTime;
    private static String fileName;
    private static String hour;
	private static String uploadFileName;
	private static String fileSeq;
	private static String reloadFlag = "0";
	private static long sum;
	private static long total;
	private static Connection conn;
	private static OutputStreamWriter output;
	private static FileOutputStream fos;
	private static BufferedWriter bw;
	private static File uapLoadTempFile;

	public static void readConfig() {
		uap_file_uapload = PropertiesUtil.getValue("uap_file_uapload_for_smsal_db_now").trim();
		prov_code = PropertiesUtil.getValue("prov_code").trim();
		uap_file_uapload_temp = PropertiesUtil.getValue("uap_file_uapload_temp").trim();
	}

	public static void main(String[] args) {
		readConfig();
		log.info("DeviceSubAcctLoginFileManual start to run......");
		log.info("uap_file_uapload==" + uap_file_uapload);
		long statisticRunStartTime = System.currentTimeMillis();
		System.out.println("DeviceSubAcctLoginFileManual...");

		Calendar calendar = Calendar.getInstance();
		calendar.set(12, 0);
		calendar.set(13, 0);
		calendar.set(14, 0);
		Timestamp a = new Timestamp(calendar.getTimeInMillis());

		String end = a.toString().replaceAll("\\.0", "");

		calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 1);
		a = new Timestamp(calendar.getTimeInMillis());

		String begin = a.toString().replaceAll("\\.0", "");
		try {
			conn = DBUtil.getAuditConnection();
            //从配置文件中获取时间日期
            createTime = PropertiesUtil.getValue("create_time");
            beginTime = PropertiesUtil.getValue("begin_time");
            endTime = PropertiesUtil.getValue("end_time");
            fileName = PropertiesUtil.getValue("file_name");
			hour = PropertiesUtil.getValue("hour");
			fileSeq = hour;
			uploadFileName = "SMSAL_" + prov_code + "_" + "01HR" + "_" + fileName
					+ "_" + fileSeq + "_" + "000.xml";
			if (args.length != 0) {
				uploadFileName = args[0];

				beginTime = args[1];
				endTime = args[2];

				int fileSeqIndex = uploadFileName.lastIndexOf("_");
				fileSeq = uploadFileName.substring(fileSeqIndex - 3, fileSeqIndex);

				int reloadFlagIndex = uploadFileName.lastIndexOf(".");

				if (!uploadFileName.substring(reloadFlagIndex - 3, reloadFlagIndex).equals("000")) {
					reloadFlag = "1";
				}

			}

			log.info("generateDeviceSubAcctLoginFile  ******Start***************");

			generateMainAcctAuthorFullFile();

			log.info("generateDeviceSubAcctLoginFile  ******End ***************");

			log.info("DR_UPLOAD_FILE_INFO**********insert ********Start*********");

			DrUploadFileInfo fileInfo = new DrUploadFileInfo();
			fileInfo.setFileName(uploadFileName);
			fileInfo.setFileSeq(fileSeq);
			fileInfo.setReloadFlag(reloadFlag);
			fileInfo.setIntval("01HR");
			fileInfo.setProv(prov_code);
//			fileInfo.setTotal(Long.valueOf(sum));
			fileInfo.setTotal(total);
			fileInfo.setType("SMSAL");
			fileInfo.setUploadStatus("0");

			insertUploadFileInfo(fileInfo);
			long statisticRunEndTime = System.currentTimeMillis();
			log.info("GENERATE DeviceSubAcctLoginFileManual FULL FILE  TOTALTIME======"
					+ (statisticRunEndTime - statisticRunStartTime) / 1000L + "s");
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} finally {
			ConnectionManager.closeConnection(conn);
			try {
				if (bw != null) {
					bw.close();
				}
				if (output != null) {
					output.close();
				}
				if (fos != null) {
					fos.close();
				}
				if (uapLoadTempFile.exists())
					uapLoadTempFile.delete();
			} catch (IOException e) {
				e.printStackTrace();
			}
			log.info("-------------MainAcctLoginFileManual end-----------------");
		}
	}

	private static void generateMainAcctAuthorFullFile() throws Exception, IOException, FileNotFoundException,
			UnsupportedEncodingException {
		StringBuffer subAcctFileBuffer = new StringBuffer();
		subAcctFileBuffer.append("<?xml version='1.0' encoding='UTF-8'?>\r\n");

		subAcctFileBuffer.append("<smp>\r\n");
		subAcctFileBuffer.append("<type>SMSAL</type>\r\n");
		subAcctFileBuffer.append("<province>" + prov_code + "</province>" + "\r\n");
		log.debug("===========================createtime:" + createTime);

		subAcctFileBuffer.append("<createtime>" + createTime.replaceAll(" ", "T") + "</createtime>" + "\r\n");

		String subAcctLoginFileSql = "select main_acct_id as mainacctid,login_time as logintime,MAIN_ACCT_NAME as loginname, sub_acct_name as acctname, device_name as resname, device_ip as resip,device_port as resport,CLIENT_IP as ip,OPERATE_RESULT as operateresult ,'0' as loginType from  iap_device_session t where  t.main_acct_id is not null and t.login_time is not null  and t.MAIN_ACCT_NAME is not null and t.sub_acct_name is not null  and t.device_name is not null and t.device_ip is not null and t.device_port is not null and t.CLIENT_IP is not null  and t.OPERATE_RESULT is not null and  t.login_time > to_date('"
				+ beginTime
				+ "', 'yyyy-MM-dd HH24:mi:ss') and t.login_time <= to_date('"
				+ endTime
				+ "', 'yyyy-MM-dd HH24:mi:ss') and (t.device_type = 2 or t.device_type = 3)"
				+ " union all"
				+ " select  main_acct_id as mainacctid,login_time as logintime,MAIN_ACCT_NAME as loginname, sub_acct_name as acctname, device_name as resname, device_ip as resip,device_port as resport,CLIENT_IP as ip,OPERATE_RESULT as operateresult,'1' as loginType  from  iap_device_session t where "
				+ " t.main_acct_id is not null"
				+ " and t.login_time is not null"
				+ "  and t.MAIN_ACCT_NAME is not null"
				+ " and t.sub_acct_name is not null"
				+ "  and t.device_name is not null"
				+ " and t.device_ip is not null"
				+ " and t.device_port is not null"
				+ " and t.CLIENT_IP is not null"
				+ "  and t.OPERATE_RESULT is not null"
				+ " and t.logout_time > to_date('"
				+ beginTime
				+ "', 'yyyy-MM-dd HH24:mi:ss') and t.logout_time <= to_date('"
				+ endTime
				+ "', 'yyyy-MM-dd HH24:mi:ss') and (t.device_type = 2 or t.device_type = 3) ";

		log.info("subAcctLoginFileCountSql===" + subAcctLoginFileSql);
		getSubAcctLoginOrLoginoutFullInfo(subAcctLoginFileSql, subAcctFileBuffer);

		log.info("subAcctFileBuffer====" + subAcctFileBuffer.toString());

		uapLoadTempFile = new File(uap_file_uapload_temp + "/" + uploadFileName);

		if (sum == 0L) {
			fos = new FileOutputStream(uapLoadTempFile, true);
			output = new OutputStreamWriter(fos, "UTF-8");
			bw = new BufferedWriter(output);
		}
		writeMainAcctFileBufferToTempFile(subAcctFileBuffer);

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

	private static void insertUploadFileInfo(DrUploadFileInfo fileInfo) throws Exception {
		String sql = "insert into DR_UPLOAD_FILE_INFO values ('" + fileInfo.getFileName() + "','" + fileInfo.getProv()
				+ "','" + fileInfo.getType() + "','" + fileInfo.getIntval() + "','" + fileInfo.getFileSeq() + "','"
				+ fileInfo.getReloadFlag() + "',to_date('" + DatetimeServices.getNowDateTimeStr(conn)
				+ "','yyyy-MM-dd HH24:mi:ss')," + fileInfo.getTotal() + ",to_date('" + beginTime
				+ "','yyyy-MM-dd HH24:mi:ss'),to_date('" + endTime + "','yyyy-MM-dd HH24:mi:ss'),'"
				+ fileInfo.getUploadStatus() + "')";
		log.info(sql);
		Connection connuap = DBUtil.getAiuap20Connection();
		try {
			PreparedStatement prepStmt = connuap.prepareStatement(sql);
			prepStmt.executeUpdate();

			ConnectionManager.closePrepStmt(prepStmt);
		} catch (RuntimeException e) {
			e.printStackTrace();
			log.error(e.getMessage());
			try {
				if (connuap != null)
					connuap.close();
			} catch (Exception e1) {
				e1.printStackTrace();
				log.error(e1.getMessage());
			}

			try {
				if (connuap != null)
					connuap.close();
			} catch (Exception e1) {
				e1.printStackTrace();
				log.error(e1.getMessage());
			}
		} finally {
			try {
				if (connuap != null)
					connuap.close();
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e.getMessage());
			}
		}
	}

	private static void writeMainAcctFileBufferToTempFile(StringBuffer mainAcctFileBuffer) throws IOException {
		bw.write(mainAcctFileBuffer.toString());
		bw.flush();
		output.flush();
		fos.flush();
	}

	private static void getSubAcctLoginOrLoginoutFullInfo(String mainAcctFullFileSql, StringBuffer mainAcctFileBuffer)
			throws Exception {
		PreparedStatement prepStmt = conn.prepareStatement(mainAcctFullFileSql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		ResultSet rs = prepStmt.executeQuery();
		rs.last();
		
		total = rs.getRow();
		
		mainAcctFileBuffer.append("<sum>" + rs.getRow() + "</sum>" + "\r\n");
		mainAcctFileBuffer.append("<begintime>" + beginTime.replace(" ", "T") + "</begintime>" + "\r\n");
		mainAcctFileBuffer.append("<endtime>" + endTime.replace(" ", "T") + "</endtime>" + "\r\n");
		mainAcctFileBuffer.append("<data>");
		rs.beforeFirst();
		int i = 0;
		File uapLoadTempFile = new File(uap_file_uapload_temp + "/" + uploadFileName);
		if (uapLoadTempFile.exists()) {
			uapLoadTempFile.delete();
			uapLoadTempFile.createNewFile();
		}
		fos = new FileOutputStream(uapLoadTempFile, true);
		output = new OutputStreamWriter(fos, "UTF-8");
		bw = new BufferedWriter(output);
		while (rs.next()) {
			i++;
			if (i % 1000 == 0) {
				log.info("writeSubAcctFileBufferToTempFile******start***");
				writeMainAcctFileBufferToTempFile(mainAcctFileBuffer);
				log.info("writeSubAcctFileBufferToTempFile******end***");
				mainAcctFileBuffer.setLength(0);
			}
			mainAcctFileBuffer.append("<rcd>\r\n");

			mainAcctFileBuffer.append("<seq>" + String.valueOf(i) + "</seq>" + "\r\n");
			mainAcctFileBuffer.append("<loginname>"
					+ (rs.getString("loginname") == null ? "" : rs.getString("loginname").trim()) + "</loginname>"
					+ "\r\n");
			mainAcctFileBuffer.append("<acctname>"
					+ (rs.getString("acctname") == null ? "" : rs.getString("acctname").trim()) + "</acctname>"
					+ "\r\n");
			mainAcctFileBuffer.append("<resname>"
					+ (rs.getString("resname") == null ? "" : rs.getString("resname").trim()) + "</resname>" + "\r\n");
			String resIp = rs.getString("resip") == null ? "" : rs.getString("resip").trim();
			String port = rs.getString("resport") == null ? "" : rs.getString("resport").trim();
			mainAcctFileBuffer.append("<resaddress>" + resIp + ":" + port + "</resaddress>" + "\r\n");
			Timestamp updateTimeStamp = rs.getTimestamp("logintime");
			String updateTime = "";
			if (updateTimeStamp != null) {
				updateTime = DatetimeServices.converterToDateTime(updateTimeStamp);
			}
			mainAcctFileBuffer.append("<logintime>" + updateTime.replace(" ", "T") + "</logintime>" + "\r\n");

			mainAcctFileBuffer.append("<ip>" + (rs.getString("ip") == null ? "" : rs.getString("ip").trim()) + "</ip>"
					+ "\r\n");
			mainAcctFileBuffer.append("<result>"
					+ (rs.getString("operateresult") == null ? "" : rs.getString("operateresult").trim())
					+ "</result>" + "\r\n");
			mainAcctFileBuffer.append("<logintype>" + rs.getString("loginType").trim() + "</logintype>" + "\r\n");
			mainAcctFileBuffer.append("</rcd>\r\n");
		}

		mainAcctFileBuffer.append("</data>\r\n");
		mainAcctFileBuffer.append("</smp>");
		ConnectionManager.closeResultSet(rs);
		ConnectionManager.closePrepStmt(prepStmt);
	}
}