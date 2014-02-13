package com.ailk.uap.makefile4new;

/**
 * 主从帐号绑定关系月全量接口文件
 * type=SMMSF
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import org.apache.commons.fileupload.util.Streams;

import com.ailk.uap.dbconn.ConnectionManager;
import com.ailk.uap.makefile4new.sql.AuthorFullFileSQL;
import com.ailk.uap.util.CommonUtil;
import com.ailk.uap.util.DatetimeServices;

public class AuthorFullFile extends AbstractMakeMonthFile implements AuthorFullFileSQL {
	
	public static void main(String[] args) {
		AbstractMakeFile abstractMakeFile = new AuthorFullFile();
		abstractMakeFile.makeFile();
	}

	private static void generateMainAcctAuthorFullFile() throws Exception, IOException, FileNotFoundException,
			UnsupportedEncodingException {
		StringBuffer authorFileBuffer = new StringBuffer();
		authorFileBuffer.append("<?xml version='1.0' encoding='UTF-8'?>\r\n");

		authorFileBuffer.append("<smp>\r\n");
		authorFileBuffer.append("<type>SMMSF</type>\r\n");
		authorFileBuffer.append("<province>" + prov_code + "</province>" + "\r\n");

		authorFileBuffer.append("<createtime>" + createTime.replace(" ", "T") + "</createtime>" + "\r\n");

		String authorFullFileSql = "";
		log.info("uap_version=================" + uap_version);
		authorFullFileSql = "select * from (SELECT M.LOGIN_NAME MAINACC,                                     M.MAIN_ACCT_ID MAINID,                                    CASE                                                        WHEN T.RES_KIND = '1' THEN                                 (SELECT APP.LOGIN_ACCT                                       FROM UAP_APP_ACCT APP                                    WHERE APP.ACCT_SEQ = T.RES_ACCT_ID)                    WHEN T.RES_KIND = '2' THEN                                 (SELECT HOST.ACCT_NAME                                       FROM UAP_HOST_ACCT HOST                                  WHERE HOST.HOST_ACCT_ID = T.RES_ACCT_ID)               WHEN T.RES_KIND = '3' THEN                                 (SELECT DB.ACCT_NAME                                         FROM UAP_DB_ACCT DB                                      WHERE DB.DB_ACCT_ID = T.RES_ACCT_ID)                   WHEN T.RES_KIND = '4' THEN                                 (SELECT ND.ACCT_NAME                                         FROM UAP_ND_ACCT ND                                      WHERE ND.ND_ACCT_ID = T.RES_ACCT_ID)                   WHEN T.RES_KIND = '5' THEN                                 (SELECT SD.ACCT_NAME                                         FROM UAP_SD_ACCT SD                                      WHERE SD.SD_ACCT_ID = T.RES_ACCT_ID)                   ELSE                                                       ''                                                     END SUBACC,                                               T.RES_ACCT_ID SUBID,                                      CASE                                                        WHEN T.RES_KIND = '1' THEN                                 '2'                                                      ELSE                                                       '1'                                                    END SUBACCTYPE,                                           T.AUTHOR_EFFECT_TIME EFFECTTIME,                          T.AUTHOR_EXPIRE_TIME EXPIRETIME                      FROM UAP_ACCT_AUTHOR T, UAP_MAIN_ACCT M                  WHERE T.USE_MAIN_ACCT_ID = M.MAIN_ACCT_ID                 AND   T.AUTHOR_EFFECT_TIME <  TO_DATE('"
				+ endTime + "', 'yyyy-MM-dd HH24:mi:ss')) y where y.subacc is not null ";

		log.info("authorFullFileSql===" + authorFullFileSql);
		
		log.info("getAuthorFullInfo*******************Start******");
		getAuthorFullInfo(authorFullFileSql, authorFileBuffer);
		log.info("getAuthorFullInfo*******************End*********");

		authorFileBuffer.append("</data>\r\n");
		authorFileBuffer.append("</smp>");

//		log.info("AuthorFileBuffer====" + authorFileBuffer.toString());

		uapLoadTempFile = new File(uap_file_uapload_temp + "/" + uploadFileName);

		writeFileBufferToTempFile(authorFileBuffer);

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


	private static void getAuthorFullInfo(String authorFullFileSql, StringBuffer authorFileBuffer) throws Exception {
		PreparedStatement prepStmt = conn.prepareStatement(authorFullFileSql, ResultSet.TYPE_SCROLL_INSENSITIVE,
			ResultSet.CONCUR_READ_ONLY);
		ResultSet rs = prepStmt.executeQuery();
		rs.last();
		sum = rs.getRow();
		authorFileBuffer.append("<sum>" + sum + "</sum>" + "\r\n");
		authorFileBuffer.append("<begintime>" + beginTimeWithT.replace(" ", "T") + "</begintime>" + "\r\n");
		authorFileBuffer.append("<endtime>" + endTime.replace(" ", "T") + "</endtime>" + "\r\n");
		authorFileBuffer.append("<data>");
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
				writeFileBufferToTempFile(authorFileBuffer);
				authorFileBuffer.setLength(0);
			}

			authorFileBuffer.append("<rcd>\r\n");
			authorFileBuffer.append("<seq>" + String.valueOf(i) + "</seq>" + "\r\n");

			authorFileBuffer.append("<mainacc>" + String.valueOf(rs.getString("mainacc")) + "</mainacc>" + "\r\n");

			authorFileBuffer.append("<mainid>" + String.valueOf(rs.getString("mainid")) + "</mainid>" + "\r\n");

			authorFileBuffer.append("<subacc>" + String.valueOf(CommonUtil.getStr(rs.getString("subacc")))
					+ "</subacc>" + "\r\n");

			authorFileBuffer.append("<subid>" + String.valueOf(rs.getString("subid")) + "</subid>" + "\r\n");

			authorFileBuffer.append("<subacctype>" + String.valueOf(CommonUtil.getStr(rs.getString("subacctype")))
					+ "</subacctype>" + "\r\n");

			Timestamp effectTimeStamp = rs.getTimestamp("effecttime");
			String effectTime = "";
			if (effectTimeStamp != null) {
				effectTime = DatetimeServices.converterToDateTime(effectTimeStamp);
			}

			authorFileBuffer.append("<effecttime>" + effectTime.replace(" ", "T") + "</effecttime>" + "\r\n");

			Timestamp expiretimeStamp = rs.getTimestamp("expiretime");
			String expireTime = "";
			if (expiretimeStamp != null) {
				expireTime = DatetimeServices.converterToDateTime(expiretimeStamp);
			}

			authorFileBuffer.append("<expiretime>" + expireTime.replace(" ", "T") + "</expiretime>" + "\r\n");

			authorFileBuffer.append("<establishtime>" + effectTime.replace(" ", "T") + "</establishtime>" + "\r\n");

			authorFileBuffer.append("<updatetime>" + effectTime.replace(" ", "T") + "</updatetime>" + "\r\n");

			authorFileBuffer.append("</rcd>\r\n");
		}
		ConnectionManager.closeResultSet(rs);
		ConnectionManager.closePrepStmt(prepStmt);
	}
	
	public    String  getFileType(){
		return super.TYPE_SMMSF;
	}

	@Override
	public void generateMakeFile() throws Exception {
		generateMainAcctAuthorFullFile();
	}
	
}