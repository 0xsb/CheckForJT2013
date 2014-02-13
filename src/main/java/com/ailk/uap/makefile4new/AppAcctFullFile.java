package com.ailk.uap.makefile4new;

/*
 * 应用资源帐号月全量接口文件
 * type=SMAAF
 */
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import org.apache.commons.fileupload.util.Streams;

import com.ailk.uap.makefile4new.constant.BusiCode;
import com.ailk.uap.util.CommonUtil;
import com.ailk.uap.util.DatetimeServices;

public class AppAcctFullFile extends AbstractMakeMonthFile  implements BusiCode {
	private static ResultSet rs;
	private static PreparedStatement prepStmt;
	
	public static void main(String[] args) {
		AbstractMakeFile abstractMakeFile = new AppAcctFullFile();
		abstractMakeFile.makeFile();
	}

	public static void generateAppAcctFullFile() throws Exception {
		String AppAcctFullFileSql = "";

		AppAcctFullFileSql = "select t.acct_seq as user_id,t.user_id as app_user_id,app.app_name,t.login_acct, " +
				" case when t.acct_status=1 then 0 when t.acct_status=2 then 1 when t.acct_status=0 then 1 end lock_status, " +
				" case when t.acct_type='05' then '99' else t.acct_type end acct_type,t.effect_time,t.expire_time, t.create_time, " +
				" t.last_update_time as update_time,  "
				+ APP_COMMON_SQL_OF_RESTYPE
				+ " restype"
				+ " from "
				+ " uap_app_acct t,UAP_APP app "
				+ "where t.create_time <  to_date('"
				+ endTime
				+ "', 'yyyy-MM-dd HH24:mi:ss')  "
				+ " and t.app_id = app.app_id and t.last_update_time is not null";

		log.info("AppAcctFullFileSql = " + AppAcctFullFileSql);

		StringBuffer AppAcctFileBuffer = new StringBuffer();
		AppAcctFileBuffer.append("<?xml version='1.0' encoding='UTF-8'?>\r\n");
		AppAcctFileBuffer.append("<smp>\r\n");
		AppAcctFileBuffer.append("<type>SMAAF</type>\r\n");
		AppAcctFileBuffer.append("<province>" + prov_code + "</province>" + "\r\n");
		AppAcctFileBuffer.append("<createtime>" + createTime + "</createtime>" + "\r\n");

		log.debug("getAppAcctFullInfo*******************Start******");
		getAppAcctFullInfo(AppAcctFullFileSql, AppAcctFileBuffer);
		log.debug("getAppAcctFullInfo*******************End******");
		AppAcctFileBuffer.append("</data>");
		AppAcctFileBuffer.append("</smp>");

		 uapLoadTempFile = new File(uap_file_uapload_temp + "/" + uploadFileName);

		writeFileBufferToTempFile(AppAcctFileBuffer);

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

	private static void getAppAcctFullInfo(String AppAcctFullFileSql, StringBuffer AppAcctFileBuffer) throws Exception {
		prepStmt = conn.prepareStatement(AppAcctFullFileSql, ResultSet.TYPE_SCROLL_INSENSITIVE,
			ResultSet.CONCUR_READ_ONLY);
		rs = prepStmt.executeQuery();
		rs.last();
		sum = rs.getRow();
		int i = 0;
		AppAcctFileBuffer.append("<sum>" + sum + "</sum>" + "\r\n");
		AppAcctFileBuffer.append("<begintime>" + beginTimeWithT + "</begintime>" + "\r\n");
		AppAcctFileBuffer.append("<endtime>" + endTimeWithT + "</endtime>" + "\r\n");
		AppAcctFileBuffer.append("<data>");

		File uapLoadTempFile = new File(uap_file_uapload_temp + "/" + uploadFileName);
		if (uapLoadTempFile.exists()) {
			uapLoadTempFile.delete();
			uapLoadTempFile.createNewFile();
		}
		fos = new FileOutputStream(uapLoadTempFile, true);
		output = new OutputStreamWriter(fos, "UTF-8");
		bw = new BufferedWriter(output);

		rs.beforeFirst();
		while (rs.next()) {
			i++;
			if (i % 1000 == 0) {
				log.info("****write to file " + i + "****");
				writeFileBufferToTempFile(AppAcctFileBuffer);
				AppAcctFileBuffer.setLength(0);
			}
			AppAcctFileBuffer.append("<rcd>\r\n");
			AppAcctFileBuffer.append("<seq>" + String.valueOf(i) + "</seq>" + "\r\n");
			AppAcctFileBuffer.append("<id>" + CommonUtil.getStr(rs.getString("user_id")) + "</id>" + "\r\n");
			AppAcctFileBuffer.append("<resname>" + CommonUtil.getStr(rs.getString("app_name")) + "</resname>" + "\r\n");
			AppAcctFileBuffer.append("<restype>" + CommonUtil.getStr(rs.getString("restype")) + "</restype>" + "\r\n");
			AppAcctFileBuffer.append("<loginname>" + CommonUtil.getStr(rs.getString("login_acct")) + "</loginname>"
					+ "\r\n");

			AppAcctFileBuffer.append("<lockstatus>" + CommonUtil.changeAcctStatus(rs.getString("lock_status"))
					+ "</lockstatus>" + "\r\n");

			AppAcctFileBuffer.append("<accttype>" + CommonUtil.changeAcctType(uap_version, rs.getString("acct_type"))
					+ "</accttype>" + "\r\n");

			Timestamp effectTimeStamp = rs.getTimestamp("effect_time");
			String effectTime = "";
			if (effectTimeStamp != null) {
				effectTime = DatetimeServices.converterToDateTimeWithT(effectTimeStamp);
			}
			AppAcctFileBuffer.append("<effecttime>" + effectTime + "</effecttime>" + "\r\n");
			Timestamp expiretimeStamp = rs.getTimestamp("expire_time");
			String expireTime = "";
			if (expiretimeStamp != null) {
				expireTime = DatetimeServices.converterToDateTimeWithT(expiretimeStamp);
			}
			AppAcctFileBuffer.append("<expiretime>" + expireTime + "</expiretime>" + "\r\n");
			Timestamp createStamp = rs.getTimestamp("create_time");
			String createTime = "";
			if (createStamp != null) {
				createTime = DatetimeServices.converterToDateTimeWithT(createStamp);
			}
			AppAcctFileBuffer.append("<establishtime>" + createTime + "</establishtime>" + "\r\n");
			Timestamp updateTimeStamp = rs.getTimestamp("update_time");
			String updateTime = "";
			if (updateTimeStamp != null) {
				updateTime = DatetimeServices.converterToDateTimeWithT(updateTimeStamp);
			}
			AppAcctFileBuffer.append("<updatetime>" + updateTime + "</updatetime>" + "\r\n");

			AppAcctFileBuffer.append("</rcd>\r\n");
		}
	}
	
	public    String  getFileType(){
		return super.TYPE_SMAAF;
	}
	
	@Override
	public void generateMakeFile() throws Exception {
		generateAppAcctFullFile();
	}
	
}