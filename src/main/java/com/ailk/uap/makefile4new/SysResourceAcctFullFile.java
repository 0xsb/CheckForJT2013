package com.ailk.uap.makefile4new;

/**
 * 系统资源帐号月全量接口文件
 * type=SMHAF
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

import org.apache.commons.fileupload.util.Streams;
import com.ailk.uap.dbconn.ConnectionManager;
import com.ailk.uap.makefile4new.sql.SysResourceAcctFullFileSQL;
import com.ailk.uap.util.CommonUtil;

public class SysResourceAcctFullFile extends AbstractMakeMonthFile implements SysResourceAcctFullFileSQL {

	public static void main(String[] args) {
		AbstractMakeFile abstractMakeFile = new SysResourceAcctFullFile();
		abstractMakeFile.makeFile();
	}

	private static void generateSysResourceAcctFullFile() throws Exception {
		StringBuffer acctFileBuffer = new StringBuffer();
		
		acctFileBuffer.append("<?xml version='1.0' encoding='UTF-8'?>" + "\r\n");
		acctFileBuffer.append("<smp>" + "\r\n");
		acctFileBuffer.append("<type>" + type + "</type>" + "\r\n");
		acctFileBuffer.append("<province>" + prov_code + "</province>" + "\r\n");
		acctFileBuffer.append("<createtime>" + createTime + "</createtime>" + "\r\n");
		
		StringBuilder sysResourceAcctFullFileSql = new StringBuilder();

		sysResourceAcctFullFileSql
				.append("SELECT  ID,  \t ACCTNAME,                                       RESNAME,                                        RESADDRESS,                                     RESTYPE,                                        LOCKSTATUS,                                     ACCTTYPE,                                       ESTABLISHTIME,                                  UPDATETIME                                      FROM (                                   SELECT T.HOST_ACCT_ID ID,                              T.ACCT_NAME ACCTNAME,                           HOST.ENTITY_NAME RESNAME,                       CASE                                              WHEN HOST.LOGIN_TYPE = '1' THEN                  IP.IP || ':' || HOST.TELNET_PORT               ELSE                                             IP.IP || ':' || HOST.SSH_PORT                END RESADDRESS,                                 '01' RESTYPE,                             DECODE(T.ACCT_STATUS,'0','1','1','0','0') LOCKSTATUS,                       T.ACCT_TYPE ACCTTYPE,                           T.CREATE_TIME ESTABLISHTIME,                    T.LAST_UPDATE_TIME UPDATETIME              FROM UAP_HOST_ACCT T                            LEFT JOIN UAP_HOST HOST                           ON T.HOST_ID = HOST.HOST_ID                   LEFT JOIN UAP_HOST_IP IP                          ON HOST.HOST_ID = IP.HOST_ID                   AND IP.IS_DEFAULT = '1'                      UNION ALL                                       SELECT SDACCT.SD_ACCT_ID ID,                           SDACCT.ACCT_NAME ACCTNAME,                      SD.ENTITY_NAME RESNAME,                         CASE                                              WHEN SD.LOGIN_TYPE = '1' THEN                    SD.IP || ':' || SD.TELNET_PORT                 ELSE                                             SD.IP || ':' || SD.SSH2_PORT                 END RESADDRESS,                                 '04' RESTYPE,                             DECODE(SDACCT.ACCT_STATUS,'0','1','1','0','0') LOCKSTATUS,                  SDACCT.ACCT_TYPE ACCTTYPE,                      SDACCT.CREATE_TIME ESTABLISHTIME,               SDACCT.LAST_UPDATE_TIME UPDATETIME         FROM UAP_SD_ACCT SDACCT                         LEFT JOIN UAP_SECURITY_DEVICE SD                  ON SDACCT.SD_ID = SD.DEVICE_ID              UNION ALL                                       SELECT DBACCT.DB_ACCT_ID ID,                                                  DBACCT.ACCT_NAME ACCTNAME,                                             CASE                                                                     WHEN DHOST.INSTANCE_NAME IS NULL THEN                                   DHOST.DB_NAME                                                         ELSE                                                                    DHOST.INSTANCE_NAME                                                 END RESNAME,                                                           CASE DHOST.DEPLOY_MODE                                                   WHEN '1' THEN                                                           DHOST.IP_ADDR || ':' || DHOST.PORT                                    WHEN '0' THEN                                                 \t\t\t\t(SELECT IP.IP                                            \t\t\t        FROM UAP_HOST HOST, UAP_HOST_IP IP                   \t\t\t         WHERE DHOST.HOST_ID = HOST.HOST_ID                  \t\t\t           AND HOST.HOST_ID = IP.HOST_ID                     \t\t\t           AND IP.IS_DEFAULT = '1'                           \t\t\t    ) || ':' || DHOST.PORT                                            ELSE                                                                    '无'                                                                 END RESADDRESS,                                                        '02' RESTYPE,                                                          DECODE(DBACCT.ACCT_STATUS, '0', '1', '1', '0', '0') LOCKSTATUS,        DBACCT.ACCT_TYPE ACCTTYPE,                                             DBACCT.CREATE_TIME ESTABLISHTIME,                                      DBACCT.LAST_UPDATE_TIME UPDATETIME                                FROM UAP_DB_ACCT DBACCT,                                                    UAP_DB      DB,                                                        UAP_DB_HOST DHOST                                                WHERE                                                                    \t DBACCT.DB_ID = DB.DB_ID                                            AND DHOST.DB_ID = DB.DB_ID                                          UNION ALL                                       SELECT NDACCT.ND_ACCT_ID ID,                           NDACCT.ACCT_NAME ACCTNAME,                      ND.ENTITY_NAME RESNAME,                         CASE                                              WHEN ND.LOGIN_TYPE = '1' THEN                    ND.IP || ':' || ND.TELNET_PORT                 ELSE                                             ND.IP || ':' || ND.SSH2_PORT                 END RESADDRESS,                                 '03' RESTYPE,                                   DECODE(NDACCT.ACCT_STATUS,'0','1','1','0','0') LOCKSTATUS,                  NDACCT.ACCT_TYPE ACCTTYPE,                      NDACCT.CREATE_TIME ESTABLISHTIME,               NDACCT.LAST_UPDATE_TIME UPDATETIME         FROM UAP_ND_ACCT NDACCT                         LEFT JOIN UAP_NET_DEVICE ND                       ON NDACCT.DEVICE_ID = ND.DEVICE_ID)         ");
		sysResourceAcctFullFileSql.append(" WHERE ESTABLISHTIME < TO_DATE('");
		sysResourceAcctFullFileSql.append(endTime);
		sysResourceAcctFullFileSql.append("', 'yyyy-MM-dd HH24:mi:ss')and UPDATETIME is not null ");

		log.info("sysResourceAcctFullFileSql===" + sysResourceAcctFullFileSql.toString());

		log.info("getSysResourceAcctFullInfo*******************Start******");
		getSysResourceAcctFullInfo(sysResourceAcctFullFileSql.toString(), acctFileBuffer);
		log.info("getSysResourceAcctFullInfo*******************End*********");

		acctFileBuffer.append("</data>\r\n");
		acctFileBuffer.append("</smp>");

//		log.info("hostAcctFileBuffer====" + acctFileBuffer.toString());

		 uapLoadTempFile = new File(uap_file_uapload_temp + "/" + uploadFileName);

		 writeFileBufferToTempFile(acctFileBuffer);

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

	private static void getSysResourceAcctFullInfo(String sysResourceAcctFullFileSql,
			StringBuffer sysResouceAcctFileBuffer) throws Exception {
		PreparedStatement prepStmt = conn.prepareStatement(sysResourceAcctFullFileSql,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		ResultSet rs = prepStmt.executeQuery();
		rs.last();
		sum = rs.getRow();
		log.error("sum=========== "+ sum);
		sysResouceAcctFileBuffer.append("<sum>" + sum + "</sum>" + "\r\n");
		sysResouceAcctFileBuffer.append("<begintime>" + beginTimeWithT + "</begintime>" + "\r\n");
		sysResouceAcctFileBuffer.append("<endtime>" + endTimeWithT + "</endtime>" + "\r\n");
		sysResouceAcctFileBuffer.append("<data>");
		int i = 0;
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
				log.info("writeSysResourceAcctFileBufferToTempFile******start***");
				writeFileBufferToTempFile(sysResouceAcctFileBuffer);
				log.info("writeSysResourceAcctFileBufferToTempFile******end***");
				sysResouceAcctFileBuffer.setLength(0);
			}

			sysResouceAcctFileBuffer.append("<rcd>\r\n");
			sysResouceAcctFileBuffer.append("<seq>" + String.valueOf(i) + "</seq>" + "\r\n");
			sysResouceAcctFileBuffer.append("<id>" + CommonUtil.getStr(rs.getString("id")) + "</id>" + "\r\n");
			sysResouceAcctFileBuffer.append("<acctname>" + CommonUtil.getStr(rs.getString("acctname")) + "</acctname>"
					+ "\r\n");
			sysResouceAcctFileBuffer.append("<resname>" + CommonUtil.getStr(rs.getString("resname")) + "</resname>"
					+ "\r\n");
			sysResouceAcctFileBuffer.append("<resaddress>" + CommonUtil.getStr(rs.getString("resaddress"))
					+ "</resaddress>" + "\r\n");
			sysResouceAcctFileBuffer.append("<restype>" + CommonUtil.getStr(rs.getString("restype")) + "</restype>"
					+ "\r\n");
			sysResouceAcctFileBuffer.append("<lockstatus>" + rs.getString("lockstatus") + "</lockstatus>" + "\r\n");
			sysResouceAcctFileBuffer.append("<accttype>"
					+ CommonUtil.changeAcctType(uap_version, rs.getString("accttype").trim()) + "</accttype>" + "\r\n");
			sysResouceAcctFileBuffer.append("<establishtime>" + CommonUtil.getTime(rs.getTimestamp("establishtime"))
					+ "</establishtime>" + "\r\n");

			sysResouceAcctFileBuffer.append("<updatetime>" + CommonUtil.getTime(rs.getTimestamp("updatetime"))
					+ "</updatetime>" + "\r\n");
			sysResouceAcctFileBuffer.append("</rcd>\r\n");
		}

		ConnectionManager.closeResultSet(rs);
		ConnectionManager.closePrepStmt(prepStmt);
	}

	@Override
	public String getFileType() {
		return super.TYPE_SMHAF;
	}
	
	@Override
	public void generateMakeFile() throws Exception {
		generateSysResourceAcctFullFile();
	}
}