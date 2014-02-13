package com.ailk.uap.makefile4new;

/**
 * 前台异常业务操作统计日增量文件
 * type=SMBHR
 */
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.fileupload.util.Streams;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ailk.jt.task.entity.A4CBOSSCX;
import com.ailk.jt.task.entity.A4CBOSSJF;
import com.ailk.jt.task.service.A4CBOSSCXService;
import com.ailk.jt.task.service.A4CBOSSJFService;
import com.ailk.jt.util.DBUtil;
import com.ailk.jt.util.SQLUtil;
import com.ailk.jt.util.SpringUtil;
import com.ailk.uap.config.PropertiesUtil;
import com.ailk.uap.dbconn.ConnectionManager;
import com.ailk.uap.entity.DrUploadFileInfo;
import com.ailk.uap.util.DatetimeServices;

public class WebExceptionBusinessDayAddlFile_new extends AbstractMakeDayFile {

	public static void main(String[] args) {
		AbstractMakeFile abstractMakeFile = new WebExceptionBusinessDayAddlFile_new();
		abstractMakeFile.makeFile();
	}

	private static void generateWebExceptionBusinessDayAddFile() throws Exception, IOException, FileNotFoundException,
			UnsupportedEncodingException {
		StringBuffer webExceptionBusinessDayAddFileBuffer = new StringBuffer();
		webExceptionBusinessDayAddFileBuffer.append("<?xml version='1.0' encoding='UTF-8'?>\r\n");
		webExceptionBusinessDayAddFileBuffer.append("<smp>\r\n");
		webExceptionBusinessDayAddFileBuffer.append("<type>SMBHR</type>\r\n");

		webExceptionBusinessDayAddFileBuffer.append("<province>" + prov_code + "</province>" + "\r\n");
		webExceptionBusinessDayAddFileBuffer.append("<createtime>" + createTime + "</createtime>" + "\r\n");

		String totalSql = "";
		log.info("webExceptionBussinessCountSql===" + totalSql);

		sum = 93L;
		log.info("total sum ===" + sum);
		webExceptionBusinessDayAddFileBuffer.append("<sum>" + sum + "</sum>" + "\r\n");
		webExceptionBusinessDayAddFileBuffer.append("<begintime>" + beginTimeWithT + "</begintime>" + "\r\n");
		webExceptionBusinessDayAddFileBuffer.append("<endtime>" + endTimeWithT + "</endtime>" + "\r\n");
		webExceptionBusinessDayAddFileBuffer.append("<data>");

		log.info("getWebExceptionBusinessInfo*******************Start******");
		getWebExceptionBusinessInfo(webExceptionBusinessDayAddFileBuffer);
		log.info("getWebExceptionBusinessInfo*******************End*********");

		webExceptionBusinessDayAddFileBuffer.append("</data>\r\n");
		webExceptionBusinessDayAddFileBuffer.append("</smp>");

		log.info("webExceptionBusinessDayAddFileBuffer====" + webExceptionBusinessDayAddFileBuffer.toString());

		uapLoadTempFile = new File(uap_file_uapload_temp + "/" + uploadFileName);

		writeFileBufferToTempFile(webExceptionBusinessDayAddFileBuffer);

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



	private static void getWebExceptionBusinessInfo(StringBuffer webExceptionBusinessDayAddFileBuffer) throws Exception {
		String[] behaviour = { "101", "102", "103" };

		uapLoadTempFile = new File(uap_file_uapload_temp + "/" + uploadFileName);
		if (uapLoadTempFile.exists()) {
			uapLoadTempFile.delete();
			uapLoadTempFile.createNewFile();
		}
		fos = new FileOutputStream(uapLoadTempFile, true);
		output = new OutputStreamWriter(fos, "UTF-8");
		bw = new BufferedWriter(output);
		ResultSet cx_rs = null;
		ResultSet jf_rs = null;

		String provName = "";
		String totalNumber = "";
		String busiNumber = "";
		String a4_cboss_all_temp = SQLUtil.getSql("a4_cboss_all_temp");
		ResultSet rs = DBUtil.getQueryResultSet(conn, a4_cboss_all_temp);

		int j = 1;
		for (int i = 0; i < behaviour.length; i++) {
			if (i == 0) {
				while (rs.next()) {
					provName = rs.getString("prvo_name");
					totalNumber = rs.getString("total_count");
					busiNumber = rs.getString("busi_count");
					webExceptionBusinessDayAddFileBuffer.append("<rcd>\r\n");

					webExceptionBusinessDayAddFileBuffer.append("<seq>" + j + "</seq>" + "\r\n");
					webExceptionBusinessDayAddFileBuffer.append("<resname>BOSS系统</resname>\r\n");

					webExceptionBusinessDayAddFileBuffer.append("<behaviour>" + behaviour[0] + "</behaviour>" + "\r\n");
					webExceptionBusinessDayAddFileBuffer.append("<companyname>" + provName + "</companyname>" + "\r\n");
					webExceptionBusinessDayAddFileBuffer.append("<total>" + totalNumber + "</total>" + "\r\n");
					webExceptionBusinessDayAddFileBuffer.append("<buztotal>" + busiNumber + "</buztotal>" + "\r\n");
					webExceptionBusinessDayAddFileBuffer.append("</rcd>\r\n");

					j++;
				}
				webExceptionBusinessDayAddFileBuffer.append("<rcd>\r\n");
				webExceptionBusinessDayAddFileBuffer.append("<seq>31</seq>\r\n");

				webExceptionBusinessDayAddFileBuffer.append("<resname>BOSS系统</resname>\r\n");

				webExceptionBusinessDayAddFileBuffer.append("<behaviour>" + behaviour[0] + "</behaviour>" + "\r\n");
				webExceptionBusinessDayAddFileBuffer.append("<companyname>河南移动</companyname>\r\n");

				webExceptionBusinessDayAddFileBuffer.append("<total>0</total>\r\n");

				webExceptionBusinessDayAddFileBuffer.append("<buztotal>0</buztotal>\r\n");

				webExceptionBusinessDayAddFileBuffer.append("</rcd>\r\n");
			}
			if (i == 1) {
				rs = DBUtil.getQueryResultSet(conn, a4_cboss_all_temp);
				while (rs.next()) {
					provName = rs.getString("prvo_name");
					totalNumber = rs.getString("total_count");
					busiNumber = rs.getString("busi_count");
					webExceptionBusinessDayAddFileBuffer.append("<rcd>\r\n");

					webExceptionBusinessDayAddFileBuffer.append("<seq>" + (j + 1) + "</seq>" + "\r\n");
					webExceptionBusinessDayAddFileBuffer.append("<resname>BOSS系统</resname>\r\n");

					webExceptionBusinessDayAddFileBuffer.append("<behaviour>" + behaviour[1] + "</behaviour>" + "\r\n");
					webExceptionBusinessDayAddFileBuffer.append("<companyname>" + provName + "</companyname>" + "\r\n");
					webExceptionBusinessDayAddFileBuffer.append("<total>0</total>\r\n");

					webExceptionBusinessDayAddFileBuffer.append("<buztotal>0</buztotal>\r\n");

					webExceptionBusinessDayAddFileBuffer.append("</rcd>\r\n");

					j++;
				}
				webExceptionBusinessDayAddFileBuffer.append("<rcd>\r\n");
				webExceptionBusinessDayAddFileBuffer.append("<seq>62</seq>\r\n");

				webExceptionBusinessDayAddFileBuffer.append("<resname>BOSS系统</resname>\r\n");

				webExceptionBusinessDayAddFileBuffer.append("<behaviour>" + behaviour[1] + "</behaviour>" + "\r\n");
				webExceptionBusinessDayAddFileBuffer.append("<companyname>河南移动</companyname>\r\n");

				webExceptionBusinessDayAddFileBuffer.append("<total>0</total>\r\n");

				webExceptionBusinessDayAddFileBuffer.append("<buztotal>0</buztotal>\r\n");

				webExceptionBusinessDayAddFileBuffer.append("</rcd>\r\n");
			}
			if (i == 2) {
				rs = DBUtil.getQueryResultSet(conn, a4_cboss_all_temp);
				while (rs.next()) {
					provName = rs.getString("prvo_name");
					totalNumber = rs.getString("total_count");
					busiNumber = rs.getString("busi_count");
					webExceptionBusinessDayAddFileBuffer.append("<rcd>\r\n");

					webExceptionBusinessDayAddFileBuffer.append("<seq>" + (j + 2) + "</seq>" + "\r\n");
					webExceptionBusinessDayAddFileBuffer.append("<resname>BOSS系统</resname>\r\n");

					webExceptionBusinessDayAddFileBuffer.append("<behaviour>" + behaviour[2] + "</behaviour>" + "\r\n");
					webExceptionBusinessDayAddFileBuffer.append("<companyname>" + provName + "</companyname>" + "\r\n");
					webExceptionBusinessDayAddFileBuffer.append("<total>0</total>\r\n");

					webExceptionBusinessDayAddFileBuffer.append("<buztotal>0</buztotal>\r\n");

					webExceptionBusinessDayAddFileBuffer.append("</rcd>\r\n");

					j++;
				}
				webExceptionBusinessDayAddFileBuffer.append("<rcd>\r\n");
				webExceptionBusinessDayAddFileBuffer.append("<seq>93</seq>\r\n");

				webExceptionBusinessDayAddFileBuffer.append("<resname>BOSS系统</resname>\r\n");

				webExceptionBusinessDayAddFileBuffer.append("<behaviour>" + behaviour[2] + "</behaviour>" + "\r\n");
				webExceptionBusinessDayAddFileBuffer.append("<companyname>河南移动</companyname>\r\n");

				webExceptionBusinessDayAddFileBuffer.append("<total>0</total>\r\n");

				webExceptionBusinessDayAddFileBuffer.append("<buztotal>0</buztotal>\r\n");

				webExceptionBusinessDayAddFileBuffer.append("</rcd>\r\n");
			}

		}

		DBUtil.closeResultSet(rs);
	}


	@Override
	public void generateMakeFile() throws Exception {
		String truncate_cx_temp = SQLUtil.getSql("a4_cboss_update_db_source_truncate_cx");
		ResultSet truncate_cx_temp_ResultSet = DBUtil.getQueryResultSet(conn, truncate_cx_temp);
		log.info("truncate_cx_temp==========" + truncate_cx_temp);
		String truncate_jf_temp = SQLUtil.getSql("a4_cboss_update_db_source_truncate_jf");
		ResultSet truncate_jf_temp_ResultSet = DBUtil.getQueryResultSet(conn, truncate_jf_temp);
		log.info("truncate_jf_temp==========" + truncate_jf_temp);
		DBUtil.closeResultSet(truncate_cx_temp_ResultSet);
		DBUtil.closeResultSet(truncate_jf_temp_ResultSet);

		HashMap<String, String> paramterHashMap = new HashMap<String, String>();
		paramterHashMap.put("beginTime", beginTime);
		paramterHashMap.put("endTime", endTime);

		Connection auditaConn = DBUtil.getCBOSSConnection();
		String update_cx_temp = SQLUtil.getSql("WEBEXCEPTIONBUSINESS_total_cx");
		String update_cx = SQLUtil.replaceParameter(update_cx_temp, paramterHashMap);
		log.debug("==================update_cx============" + update_cx);
		PreparedStatement prepStmt = auditaConn.prepareStatement(update_cx);
		ResultSet rs = prepStmt.executeQuery();

		String HOME_SWITCH_NODE = "";
		String TOTAL_COUNT = "";
		List<A4CBOSSCX> a4CBOSSCXList = new ArrayList<A4CBOSSCX>();
		A4CBOSSCXService a4CBOSSCXService = null;
		try {
			ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
			boolean exits = context.containsBean("a4CBOSSCXService");
			System.out.println(exits);
			System.out.println(context.getBean("a4CBOSSCXService"));
			a4CBOSSCXService = (A4CBOSSCXService) context.getBean("a4CBOSSCXService");
			while (rs.next()) {
				HOME_SWITCH_NODE = rs.getString("HOME_SWITCH_NODE");
				TOTAL_COUNT = rs.getString("TOTAL_COUNT");

				A4CBOSSCX a4CBOSSCX = new A4CBOSSCX();
				a4CBOSSCX.setTotalCount(TOTAL_COUNT);
				a4CBOSSCX.setHomeSwitchNode(HOME_SWITCH_NODE);
				a4CBOSSCXList.add(a4CBOSSCX);
			}
			a4CBOSSCXService.saveOrUpdateAcctList(a4CBOSSCXList);
			DBUtil.closeConnection(auditaConn);

			log.debug("update cx success~~~~~");
		} catch (BeanCreationException ew) {
			ew.printStackTrace();
			log.error("a4CBOSSCXService===============" + a4CBOSSCXService);
		}

		Connection auditaConn2 = DBUtil.getCBOSSConnection();
		String update_jf_temp = SQLUtil.getSql("WEBEXCEPTIONBUSINESS_buztotal_jf");
		String upadate_jf = SQLUtil.replaceParameter(update_jf_temp, paramterHashMap);
		PreparedStatement prepStmt_1 = auditaConn2.prepareStatement(upadate_jf);
		ResultSet rs_1 = prepStmt_1.executeQuery();
		String HOME_SWITCH_NODE_1 = "";
		String BUSI_COUNT = "";
		A4CBOSSJFService a4CBOSSJFService = null;
		try {
			List<A4CBOSSJF> a4CBOSSJFList = new ArrayList<A4CBOSSJF>();
			ClassPathXmlApplicationContext context1 = new ClassPathXmlApplicationContext("applicationContext.xml");
			boolean exits1 = context1.containsBean("a4CBOSSJFService");
			System.out.println(exits1);
			System.out.println(context1.getBean("a4CBOSSJFService"));

			a4CBOSSJFService = (A4CBOSSJFService) SpringUtil.getBean("a4CBOSSJFService");
			while (rs_1.next()) {
				HOME_SWITCH_NODE_1 = rs_1.getString("HOME_SWITCH_NODE");
				BUSI_COUNT = rs_1.getString("BUSI_COUNT");

				A4CBOSSJF a4CBOSSJF = new A4CBOSSJF();
				a4CBOSSJF.setBusiCount(BUSI_COUNT);
				a4CBOSSJF.setHomeSwitchNode(HOME_SWITCH_NODE_1);
				a4CBOSSJFList.add(a4CBOSSJF);
			}
			a4CBOSSJFService.saveOrUpdateAcctList(a4CBOSSJFList);
			DBUtil.closeConnection(auditaConn2);
			log.debug("update jf success~~~~~");
		} catch (BeanCreationException e) {
			e.printStackTrace();
			log.error("a4CBOSSJFService===============" + a4CBOSSJFService);
		}

		HashMap<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("beginTime", beginTime);
		parameterMap.put("endTime", endTime);

		String insert_cx_Temp = SQLUtil.getSql("a4_cboss_update_db_source_insert_cx_temp");
		String insert_jf_Temp = SQLUtil.getSql("a4_cboss_update_db_source_insert_jf_temp");

		String insert_cx = SQLUtil.replaceParameter(insert_cx_Temp, parameterMap);
		String insert_jf = SQLUtil.replaceParameter(insert_jf_Temp, parameterMap);
		log.debug("update final talble  success~~~~~");
		DBUtil.getExecuteResultSet(conn, insert_cx);
		DBUtil.getExecuteResultSet(conn, insert_jf);

		generateWebExceptionBusinessDayAddFile();

	}

	@Override
	public String getFileType() {
		return super.TYPE_SMBHR;
	}
	
}