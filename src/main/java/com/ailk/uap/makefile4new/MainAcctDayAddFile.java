package com.ailk.uap.makefile4new;

import com.ailk.uap.dbconn.ConnectionManager;
import com.ailk.uap.util.DatetimeServices;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import org.apache.commons.fileupload.util.Streams;

/**
 * 主账号日增量文件
 * type=SMMAI
 *
 */
public class MainAcctDayAddFile extends AbstractMakeDayFile{

	public static void main(String[] args) {
		AbstractMakeFile makeFile = new MainAcctDayAddFile();
		makeFile.makeFile();
	}

	private static void generateMainAcctDayAddFile() throws Exception, IOException, FileNotFoundException,
			UnsupportedEncodingException {
		StringBuffer mainAcctDayAddFileBuffer = new StringBuffer();
		mainAcctDayAddFileBuffer.append("<?xml version='1.0' encoding='UTF-8'?>\r\n");
		mainAcctDayAddFileBuffer.append("<smp>\r\n");
		mainAcctDayAddFileBuffer.append("<type>SMMAI</type>\r\n");
		mainAcctDayAddFileBuffer.append("<province>" + prov_code + "</province>" + "\r\n");
		mainAcctDayAddFileBuffer.append("<createtime>" + createTime + "</createtime>" + "\r\n");

		String mainAcctDayAddFileSql = "";
		mainAcctDayAddFileSql = "select 'add' as  modify_mode,t1.main_acct_id,t1.login_name,t1.name as user_name,case when (sysdate >= t1.effect_time and sysdate< t1.expire_time and t1.lock_status='0') then  '1' else '0' end valid,t1.lock_status, t1.acct_type, t1.effect_time,t1.expire_time,t1.create_time,t1.area_id,t1.default_org as org_id, t1.last_update_time as update_time from uap_main_acct t1 where t1.create_time >=  to_date('"
				+ beginTime
				+ "', 'yyyy-MM-dd HH24:mi:ss') and  t1.create_time < to_date('"
				+ endTime
				+ "', 'yyyy-MM-dd HH24:mi:ss')"
				+ "  union all "
				+ "select 'upd'  as modify_mode,"
				+ "t2.main_acct_id,"
				+ "t2.login_name,"
				+ "t2.name as user_name,"
				+ "case when (sysdate >= t2.effect_time and  sysdate< t2.expire_time and t2.lock_status='0') then  '1' else '0' end valid,"
				+ "t2.lock_status,"
				+ " t2.acct_type,"
				+ " t2.effect_time,"
				+ "t2.expire_time,"
				+ "t2.create_time,"
				+ "t2.area_id,"
				+ "t2.default_org as org_id,"
				+ " t2.last_update_time as update_time from uap_main_acct t2"
				+ " where t2.last_update_time >=  to_date('"
				+ beginTime
				+ "', 'yyyy-MM-dd HH24:mi:ss') and  t2.last_update_time< to_date('"
				+ endTime
				+ "', 'yyyy-MM-dd HH24:mi:ss') and t2.last_update_time > t2.create_time"
				+ " union all "
				+ "select 'del'  as modify_mode,"
				+ "t3.main_acct_id,"
				+ "t3.login_name,"
				+ "t3.name as user_name,"
				+ "'0' as valid,"
				+ "t3.lock_status,"
				+ " t3.acct_type,"
				+ " t3.effect_time,"
				+ "t3.expire_time,"
				+ "t3.create_time,"
				+ "t3.area_id,"
				+ "t3.default_org as org_id,"
				+ " t3.last_update_time as update_time from uap_main_acct_his t3"
				+ " where t3.last_update_time >=  to_date('"
				+ beginTime
				+ "', 'yyyy-MM-dd HH24:mi:ss') and  t3.last_update_time< to_date('"
				+ endTime
				+ "', 'yyyy-MM-dd HH24:mi:ss')";

		log.info("mainAcctDayAddFileSql===" + mainAcctDayAddFileSql);

		log.info("getMainAcctFullInfo*******************Start******");
		getMainAcctDayAddFullInfo(mainAcctDayAddFileSql, mainAcctDayAddFileBuffer);
		log.info("getMainAcctFullInfo*******************End*********");

		mainAcctDayAddFileBuffer.append("</data>\r\n");
		mainAcctDayAddFileBuffer.append("</smp>");

		log.info("mainAcctDayAddFileBuffer====" + mainAcctDayAddFileBuffer.toString());

		uapLoadTempFile = new File(uap_file_uapload_temp + "/" + uploadFileName);

		writeFileBufferToTempFile(mainAcctDayAddFileBuffer);

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


	private static Long getMainAcctFullCount(String sql) throws Exception {
		PreparedStatement prepStmt = conn.prepareStatement(sql);
		ResultSet rs = prepStmt.executeQuery();
		Long count = null;
		while (rs.next()) {
			count = Long.valueOf(rs.getLong(1));
		}
		ConnectionManager.closeResultSet(rs);
		ConnectionManager.closePrepStmt(prepStmt);

		return count;
	}

	private static void getMainAcctDayAddFullInfo(String mainAcctDayAddFileSql, StringBuffer mainAcctDayAddFileBuffer)
			throws Exception {
		PreparedStatement prepStmt = conn.prepareStatement(mainAcctDayAddFileSql, ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
		ResultSet rs = prepStmt.executeQuery();

		rs.last();
		sum = rs.getRow();
		mainAcctDayAddFileBuffer.append("<sum>" + rs.getRow() + "</sum>" + "\r\n");
		mainAcctDayAddFileBuffer.append("<begintime>" + beginTimeWithT + "</begintime>" + "\r\n");
		mainAcctDayAddFileBuffer.append("<endtime>" + endTimeWithT + "</endtime>" + "\r\n");
		mainAcctDayAddFileBuffer.append("<data>");
		rs.beforeFirst();
		int i = 0;
		uapLoadTempFile = new File(uap_file_uapload_temp + "/" + uploadFileName);
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
				log.info("writemainAcctDayAddFileBufferToTempFile******start***");

				writeFileBufferToTempFile(mainAcctDayAddFileBuffer);
				log.info("writemainAcctDayAddFileBufferToTempFile******end***");
				mainAcctDayAddFileBuffer.setLength(0);
			}

			mainAcctDayAddFileBuffer.append("<rcd>\r\n");
			mainAcctDayAddFileBuffer.append("<seq>" + String.valueOf(i) + "</seq>" + "\r\n");
			mainAcctDayAddFileBuffer.append("<mode>" + rs.getString("modify_mode").trim() + "</mode>" + "\r\n");
			mainAcctDayAddFileBuffer.append("<mainacctid>" + String.valueOf(rs.getLong("main_acct_id"))
					+ "</mainacctid>" + "\r\n");
			mainAcctDayAddFileBuffer.append("<loginname>" + rs.getString("login_name") + "</loginname>" + "\r\n");
			mainAcctDayAddFileBuffer.append("<username>" + rs.getString("user_name") + "</username>" + "\r\n");
			mainAcctDayAddFileBuffer.append("<valid>" + rs.getString("valid").trim() + "</valid>" + "\r\n");
			mainAcctDayAddFileBuffer.append("<lockstatus>" + rs.getString("lock_status") + "</lockstatus>" + "\r\n");
			mainAcctDayAddFileBuffer.append("<accttype>" + rs.getString("acct_type") + "</accttype>" + "\r\n");

			mainAcctDayAddFileBuffer.append("<rolelist>"
					+ getMainAcctRoleListInfo(Long.valueOf(rs.getLong("main_acct_id"))) + "</rolelist>" + "\r\n");

			Timestamp effectTimeStamp = rs.getTimestamp("effect_time");
			String effectTime = "";
			if (effectTimeStamp != null) {
				effectTime = DatetimeServices.converterToDateTimeWithT(effectTimeStamp);
			}
			mainAcctDayAddFileBuffer.append("<effecttime>" + effectTime + "</effecttime>" + "\r\n");
			Timestamp expiretimeStamp = rs.getTimestamp("expire_time");
			String expireTime = "";
			if (expiretimeStamp != null) {
				expireTime = DatetimeServices.converterToDateTimeWithT(expiretimeStamp);
			}
			mainAcctDayAddFileBuffer.append("<expiretime>" + expireTime + "</expiretime>" + "\r\n");

			Timestamp createtimeStamp = rs.getTimestamp("create_time");
			String createtime = "";
			if (createtimeStamp != null) {
				createtime = DatetimeServices.converterToDateTimeWithT(createtimeStamp);
			}
			mainAcctDayAddFileBuffer.append("<establishtime>" + createtime + "</establishtime>" + "\r\n");

			mainAcctDayAddFileBuffer.append("<areaid>" + rs.getString("area_id") + "</areaid>" + "\r\n");
			mainAcctDayAddFileBuffer.append("<orgid>" + rs.getLong("org_id") + "</orgid>" + "\r\n");

			mainAcctDayAddFileBuffer.append("<orgname>" + getMainAcctOrgPath(Long.valueOf(rs.getLong("org_id")))
					+ "</orgname>" + "\r\n");
			Timestamp updateTimeStamp = rs.getTimestamp("update_time");
			String updateTime = "";
			if (updateTimeStamp != null) {
				updateTime = DatetimeServices.converterToDateTimeWithT(updateTimeStamp);
			}
			mainAcctDayAddFileBuffer.append("<updatetime>" + updateTime + "</updatetime>" + "\r\n");

			mainAcctDayAddFileBuffer.append("</rcd>\r\n");
		}

		ConnectionManager.closeResultSet(rs);
		ConnectionManager.closePrepStmt(prepStmt);
	}

	private static String getMainAcctRoleListInfo(Long mainAcctId) throws Exception {
		log.info("getMainAcctRoleListInfo**********Start*********");
		StringBuffer roleListBuffer = new StringBuffer();
		String roleList = "";
		String sql = "select t3.role_name from uap_main_acct_role t1, uap_main_acct t2, uap_role t3 where t1.main_acct_id = t2.main_acct_id and t1.role_id = t3.role_id and t1.main_acct_id = "
				+ mainAcctId;
		PreparedStatement prepStmt = conn.prepareStatement(sql);
		ResultSet rs = prepStmt.executeQuery();
		while (rs.next()) {
			roleListBuffer.append(rs.getString("role_name")).append(",");
			roleList = roleListBuffer.toString();
			int index = roleList.lastIndexOf(",");
			roleList = roleList.substring(0, index);
		}

		ConnectionManager.closeResultSet(rs);
		ConnectionManager.closePrepStmt(prepStmt);

		if (roleList.equals("")) {
			roleList = "无";
		}
		log.info("getMainAcctRoleListInfo** mainAcctId***" + mainAcctId + "*****roleList = " + roleList);

		return roleList;
	}

	private static String getMainAcctOrgPath(Long orgId) throws Exception {
		StringBuffer orgPathBuffer = new StringBuffer();
		String orgIdColumn = "org_id";
		String org_nameColumn = "org_name";

		String orgPath = "";
		String sql = "select " + org_nameColumn + " from uap_organization   start with " + orgIdColumn + " =  " + orgId
				+ " connect by prior  parent_id =  " + orgIdColumn + " order by org_id ";
		PreparedStatement prepStmt = conn.prepareStatement(sql);
		ResultSet rs = prepStmt.executeQuery();
		while (rs.next()) {
			orgPathBuffer.append(rs.getString(org_nameColumn)).append("-");
		}

		orgPath = orgPathBuffer.toString();
		int index = orgPath.lastIndexOf("-");

		ConnectionManager.closeResultSet(rs);
		ConnectionManager.closePrepStmt(prepStmt);

		return index == -1 ? orgPath : orgPath.substring(0, index);
	}

	@Override
	public void generateMakeFile() throws Exception {
		generateMainAcctDayAddFile();
	}

	@Override
	public String getFileType() {
		return super.TYPE_SMMAI;
	}
	
}