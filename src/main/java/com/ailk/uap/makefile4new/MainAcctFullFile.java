package com.ailk.uap.makefile4new;

/**
 * 主帐号月全量接口文件
 * type=SMMAF
 */
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.fileupload.util.Streams;
import com.ailk.uap.config.PropertiesUtil;
import com.ailk.uap.dbconn.ConnectionManager;
import com.ailk.uap.makefile4new.sql.MainAcctFullFileSQL;
import com.ailk.uap.util.DatetimeServices;
import com.ailk.uap.util.FileProUtil;

/*
 * Company: 		AsiaInfo-Linkage
 * Version: 		1.0 
 * Author: 			chaimj@asiainfo-linkage.com
 * 					zhoulei2@asiainfo-linkage.com
 * Createdate: 		2011-05-9 
 * Lastmodified: 	2011-05-9 
 * Desc: 			【主帐号全量文件生成】根据接口文件描述，生成主帐号全量接口文件
 */
public class MainAcctFullFile extends AbstractMakeMonthFile implements MainAcctFullFileSQL {

	public static void main(String[] args) {
		AbstractMakeFile abstractMakeFile = new MainAcctFullFile();
		abstractMakeFile.makeFile();
	}

	public static void generateMainAcctFullFile() throws Exception {
		StringBuffer mainAcctFileBuffer = new StringBuffer(); // 生成文件的StringBuffer
		mainAcctFileBuffer.append("<?xml version='1.0' encoding='UTF-8'?>" + "\r\n");
		mainAcctFileBuffer.append("<smp>" + "\r\n");
		mainAcctFileBuffer.append("<type>" + type + "</type>" + "\r\n");
		mainAcctFileBuffer.append("<province>" + prov_code + "</province>" + "\r\n");
		mainAcctFileBuffer.append("<createtime>" + createTime + "</createtime>" + "\r\n");
		StringBuffer mainAcctFullFileSql = new StringBuffer(300);
		mainAcctFullFileSql.append(" select * from "+PropertiesUtil.getValue("main_acct_month_table"));

		log.info("mainAcctFullFileSql===" + mainAcctFullFileSql.toString());

		 uapLoadTempFile = new File(uap_file_uapload_temp + File.separator + uploadFileName);
		FileProUtil.createDir(uap_file_uapload_temp + File.separator);
		FileProUtil.createFile(uapLoadTempFile.getPath());
		fos = new FileOutputStream(uapLoadTempFile, true);
		output = new OutputStreamWriter(fos, "UTF-8");
		bw = new BufferedWriter(output);
		log.info("getMainAcctFullInfo*******************Start******");
		getMainAcctFullInfo(mainAcctFullFileSql.toString(), mainAcctFileBuffer, conn);
		log.info("getMainAcctFullInfo*******************End*********");
		mainAcctFileBuffer.append("</data>\r\n");
		mainAcctFileBuffer.append("</smp>");
		log.info("mainAcctFileBuffer====" + mainAcctFileBuffer.toString());
		// 将缓存中剩余的信息一并写入临时文件
		writeFileBufferToTempFile(mainAcctFileBuffer);
		// 上传至本地上传文件目录
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(uapLoadTempFile));// 获得文件输入流
		File uapLoadFile = new File(uap_file_uapload + File.separator + uploadFileName);
		if (!uapLoadFile.exists()) {
			uapLoadFile.createNewFile();
		}
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(uapLoadFile));// 获得文件输出流
		Streams.copy(in, out, true);
		in.close();
		out.close();
		// IO close
		if (bw != null) {
			bw.close();
		}
		if (output != null) {
			output.close();
		}
		if (fos != null) {
			fos.close();
		}
		// 删除临时文件
		uapLoadTempFile.delete();
	}

	private static Long getMainAcctFullCount(Connection conn, String sql) throws Exception {
		PreparedStatement prepStmt = conn.prepareStatement(sql);
		ResultSet rs = prepStmt.executeQuery();
		Long count = null;
		while (rs.next()) {
			count = rs.getLong(1);
		}
		ConnectionManager.closeResultSet(rs);
		ConnectionManager.closePrepStmt(prepStmt);
		return count;
	}

	private static void getMainAcctFullInfo(String mainAcctFullFileSql, StringBuffer mainAcctFileBuffer, Connection conn)
			throws Exception {
		// 查询数据UAP_MAIN_ACCT条数
		Map<String, String> roleMap = getMainAcctRoleList();
		Map<Long, String> orgPathMap = new HashMap<Long, String>();

		PreparedStatement prepStmt = conn.prepareStatement(mainAcctFullFileSql, ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
		ResultSet rs = prepStmt.executeQuery();
		rs.last();
		sum = rs.getRow();
		mainAcctFileBuffer.append("<sum>" + sum + "</sum>" + "\r\n");
		mainAcctFileBuffer.append("<begintime>" + beginTimeWithT + "</begintime>" + "\r\n");
		mainAcctFileBuffer.append("<endtime>" + endTimeWithT + "</endtime>" + "\r\n");
		mainAcctFileBuffer.append("<data>");
		rs.beforeFirst();
		int i = 0;
		while (rs.next()) {
			// 每1000条记录写入文件一次
			i++;
			if (i % 1000 == 0) {
				writeFileBufferToTempFile(mainAcctFileBuffer);
				mainAcctFileBuffer.setLength(0);
			}
			mainAcctFileBuffer.append("<rcd>" + "\r\n");
			mainAcctFileBuffer.append("<seq>" + String.valueOf(i) + "</seq>" + "\r\n");
			mainAcctFileBuffer.append("<mainacctid>" + String.valueOf(rs.getLong("main_acct_id")) + "</mainacctid>"
					+ "\r\n");
			mainAcctFileBuffer.append("<loginname>" + rs.getString("login_name") + "</loginname>" + "\r\n");
			mainAcctFileBuffer.append("<username>" + rs.getString("user_name") + "</username>" + "\r\n");
			String valid = rs.getString("valid").trim();
			mainAcctFileBuffer.append("<valid>" + valid + "</valid>" + "\r\n");
			String lock_status = rs.getString("lock_status");
			mainAcctFileBuffer.append("<lockstatus>" + lock_status + "</lockstatus>" + "\r\n");
			mainAcctFileBuffer.append("<accttype>" + rs.getString("acct_type") + "</accttype>" + "\r\n");// \r\n常量声明
			// 获取主帐号对应的角色信息
			String roleList = getMainAcctRoleListInfo(roleMap.get("" + (rs.getLong("main_acct_id"))));
			mainAcctFileBuffer.append("<rolelist>" + roleList + "</rolelist>" + "\r\n");
			Timestamp effectTimeStamp = rs.getTimestamp("effect_time");
			String effectTime = "";
			if (effectTimeStamp != null) {
				effectTime = DatetimeServices.converterToDateTimeWithT(effectTimeStamp);
			}
			mainAcctFileBuffer.append("<effecttime>" + effectTime + "</effecttime>" + "\r\n");
			Timestamp expiretimeStamp = rs.getTimestamp("expire_time");
			String expireTime = "";
			if (expiretimeStamp != null) {
				expireTime = DatetimeServices.converterToDateTimeWithT(expiretimeStamp);
			}
			mainAcctFileBuffer.append("<expiretime>" + expireTime + "</expiretime>" + "\r\n");
			Timestamp createtimeStamp = rs.getTimestamp("create_time");
			String createtime = "";
			if (createtimeStamp != null) {
				createtime = DatetimeServices.converterToDateTimeWithT(createtimeStamp);
			}
			mainAcctFileBuffer.append("<establishtime>" + createtime + "</establishtime>" + "\r\n");

			mainAcctFileBuffer.append("<areaid>" + rs.getString("area_id") + "</areaid>" + "\r\n");
			mainAcctFileBuffer.append("<orgid>" + rs.getLong("org_id") + "</orgid>" + "\r\n");
			// 获取主帐号对应的组织机构的全路径
			Long orgId = rs.getLong("org_id");
			String org = orgPathMap.get(orgId);
			if (org == null || org.equals("null")) {
				org = getMainAcctOrgPath(conn, orgId);
				orgPathMap.put(orgId, org);
			}
			mainAcctFileBuffer.append("<orgname>" + org + "</orgname>" + "\r\n");
			Timestamp updateTimeStamp = rs.getTimestamp("update_time");
			String updateTime = "";
			if (updateTimeStamp != null) {
				updateTime = DatetimeServices.converterToDateTimeWithT(updateTimeStamp);
			}
			mainAcctFileBuffer.append("<updatetime>" + updateTime + "</updatetime>" + "\r\n");
            mainAcctFileBuffer.append("<superuser>" + rs.getInt("superuser") +
                    "</superuser>" + "\r\n");
			mainAcctFileBuffer.append("<opendays>" + rs.getInt("opendays") + "</opendays>" + "\r\n");
			mainAcctFileBuffer.append("<logondays>" + rs.getInt("logondays") + "</logondays>" + "\r\n");
			double usage = rs.getDouble("acct_usage");
			DecimalFormat nFormat = new DecimalFormat("#0.0000");
			mainAcctFileBuffer.append("<usage>" + nFormat.format(usage) + "</usage>" + "\r\n");
			mainAcctFileBuffer.append("</rcd>" + "\r\n");
		}
		ConnectionManager.closeResultSet(rs);
		ConnectionManager.closePrepStmt(prepStmt);
	}

	private static String getMainAcctRoleListInfo(String roleList) throws Exception {
		if (roleList == null || roleList.equals("null"))
			roleList = "无";
		return roleList;
	}

	private static String getMainAcctOrgPath(Connection conn, Long orgId) throws Exception {
		StringBuffer orgPathBuffer = new StringBuffer();
		String orgIdColumn = "";
		String org_nameColumn = "";
		orgIdColumn = "org_id";
		org_nameColumn = "org_name";
		String orgPath = "";
		String sql = "select " + org_nameColumn + " from uap_organization   start with " + orgIdColumn + " =  " + orgId
				+ " connect by prior  parent_id =  " + orgIdColumn + "";
		PreparedStatement prepStmt = conn.prepareStatement(sql);
		ResultSet rs = prepStmt.executeQuery();
		while (rs.next()) {
			orgPathBuffer.append(rs.getString(org_nameColumn)).append("-");
		}
		orgPath = orgPathBuffer.toString();
		int index = orgPath.lastIndexOf("-");
		ConnectionManager.closeResultSet(rs);
		ConnectionManager.closePrepStmt(prepStmt);
		return (index == -1) ? orgPath : orgPath.substring(0, index);
	}

	private static Map<String, String> getMainAcctRoleList() throws Exception {
		Map<String, String> roleListMap = new HashMap<String, String>();
		StringBuilder roleListSql = new StringBuilder();
		roleListSql.append(DATE_PRIOR_ALL_MAIN_ACCT_ROLE_INFO_SQL);
		roleListSql.append(" AND T2.CREATE_TIME <        ");
		roleListSql.append("       TO_DATE('" + endTime + "', 'yyyy-MM-dd HH24:mi:ss')  ");
		roleListSql.append("GROUP BY T1.MAIN_ACCT_ID       ");
		PreparedStatement prepStmt = conn.prepareStatement("" + roleListSql);
		ResultSet rs = prepStmt.executeQuery();
		while (rs.next()) {
			String mainAcctId = rs.getString("main_acct_id");
			String rolelist = rs.getString("rolelist");
			roleListMap.put(mainAcctId, rolelist);
		}
		ConnectionManager.closeResultSet(rs);
		ConnectionManager.closePrepStmt(prepStmt);
		return roleListMap;
	}
	
	public    String  getFileType(){
		return super.TYPE_SMMAF;
	}
	@Override
	public void generateMakeFile() throws Exception {
		generateMainAcctFullFile();
	}
	
}
