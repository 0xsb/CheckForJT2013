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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import org.apache.commons.fileupload.util.Streams;

import com.ailk.uap.dbconn.ConnectionManager;
import com.ailk.uap.makefile4new.sql.A4AppAcctDayAddFileSQL;
import com.ailk.uap.util.DatetimeServices;
/**
 * 4A侧记录应用系统从账号日增量文件
 * type=SM4AI
 *
 */
public class A4AppAcctDayAddFile  extends AbstractMakeDayFile implements A4AppAcctDayAddFileSQL {
    public static void main(String[] args) {
    	AbstractMakeFile makeFile = new A4AppAcctDayAddFile();
    	makeFile.makeFile();
    }

    private static void generateA4AppAcctDayAddFile() throws Exception,
	    IOException, FileNotFoundException, UnsupportedEncodingException {
	StringBuffer a4AppAcctDayAddFileBuffer = new StringBuffer();
	a4AppAcctDayAddFileBuffer
		.append("<?xml version='1.0' encoding='UTF-8'?>\r\n");
	a4AppAcctDayAddFileBuffer.append("<smp>\r\n");
	a4AppAcctDayAddFileBuffer.append("<type>SM4AI</type>\r\n");
	a4AppAcctDayAddFileBuffer.append("<province>" + prov_code
		+ "</province>" + "\r\n");
	a4AppAcctDayAddFileBuffer.append("<createtime>" + createTime
		+ "</createtime>" + "\r\n");

	String a4AppAcctDayAddFileSql = "";
	log.info("uap_version=================" + uap_version);

	a4AppAcctDayAddFileSql = A4_APP_ACCT_DAY_ADD_SQL
		+ "   AND APPACC.CREATE_TIME >=                                         "
		+ "       TO_DATE('"
		+ beginTime
		+ "', 'yyyy-MM-dd HH24:mi:ss')            "
		+ "   AND APPACC.CREATE_TIME <                                          "
		+ "       TO_DATE('"
		+ endTime
		+ "', 'yyyy-MM-dd HH24:mi:ss')               "
		+ " UNION ALL "
		+ A4_APP_ACCT_DAY_STOP_SQL
		+ "   AND APPACC1.LAST_UPDATE_TIME >=                                   "
		+ "       TO_DATE('"
		+ beginTime
		+ "', 'yyyy-MM-dd HH24:mi:ss')            "
		+ "   AND APPACC1.LAST_UPDATE_TIME <                                    "
		+ "       TO_DATE('"
		+ endTime
		+ "', 'yyyy-MM-dd HH24:mi:ss')               "
		+ " UNION ALL "
		+ A4_APP_ACCT_DAY_DEL_SQL
		+ "   AND APPACCHIS.LAST_UPDATE_TIME >=                                 "
		+ "       TO_DATE('"
		+ beginTime
		+ "', 'yyyy-MM-dd HH24:mi:ss') and        "
		+ "       APPACCHIS.LAST_UPDATE_TIME <                                  "
		+ "       TO_DATE('" + endTime
		+ "', 'yyyy-MM-dd HH24:mi:ss')               ";

	log.info("a4AppAcctDayAddFileSql===" + a4AppAcctDayAddFileSql);
	log.info("getA4AppAcctFullInfo*******************Start******");
	getA4AppAcctDayAddFullInfo(a4AppAcctDayAddFileSql,
		a4AppAcctDayAddFileBuffer);

	log.info("getA4AppAcctFullInfo*******************End*********");

	a4AppAcctDayAddFileBuffer.append("</data>\r\n");
	a4AppAcctDayAddFileBuffer.append("</smp>");

	log.info("a4AppAcctDayAddFileBuffer===="
		+ a4AppAcctDayAddFileBuffer.toString());

	uapLoadTempFile = new File(uap_file_uapload_temp + "/" + uploadFileName);
	writeFileBufferToTempFile(a4AppAcctDayAddFileBuffer);

	BufferedInputStream in = new BufferedInputStream(new FileInputStream(
		uapLoadTempFile));

	File uapLoadFile = new File(uap_file_uapload + "/" + uploadFileName);
	if (!uapLoadFile.exists()) {
	    uapLoadFile.createNewFile();
	}
	BufferedOutputStream out = new BufferedOutputStream(
		new FileOutputStream(uapLoadFile));

	Streams.copy(in, out, true);
	in.close();
	out.close();
    }

    private static void getA4AppAcctDayAddFullInfo(
	    String a4AppAcctDayAddFileSql,
	    StringBuffer a4AppAcctDayAddFileBuffer) throws Exception {
	PreparedStatement prepStmt = conn.prepareStatement(
		a4AppAcctDayAddFileSql, ResultSet.TYPE_SCROLL_INSENSITIVE,
		ResultSet.CONCUR_READ_ONLY);
	ResultSet rs = prepStmt.executeQuery();
	rs.last();
	sum = rs.getRow();
	log.info("sum=========== " + sum);

	a4AppAcctDayAddFileBuffer.append("<sum>" + sum + "</sum>" + "\r\n");
	a4AppAcctDayAddFileBuffer.append("<begintime>" + beginTimeWithT
		+ "</begintime>" + "\r\n");
	a4AppAcctDayAddFileBuffer.append("<endtime>" + endTimeWithT
		+ "</endtime>" + "\r\n");
	a4AppAcctDayAddFileBuffer.append("<data>");
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
		log
			.info("writeA4AppAcctDayAddFileBufferToTempFile******start***");

		writeFileBufferToTempFile(a4AppAcctDayAddFileBuffer);
		log
			.info("writeA4AppAcctDayAddFileBufferToTempFile******end***");
		a4AppAcctDayAddFileBuffer.setLength(0);
	    }
	    a4AppAcctDayAddFileBuffer.append("<rcd>\r\n");
	    a4AppAcctDayAddFileBuffer.append("<seq>" + String.valueOf(i)
		    + "</seq>" + "\r\n");

	    a4AppAcctDayAddFileBuffer.append("<mode>" + rs.getString("mode1")
		    + "</mode>" + "\r\n");

	    a4AppAcctDayAddFileBuffer.append("<userid>"
		    + String.valueOf(rs.getLong("id")) + "</userid>" + "\r\n");

	    a4AppAcctDayAddFileBuffer.append("<loginname>"
		    + rs.getString("loginname") + "</loginname>" + "\r\n");

	    a4AppAcctDayAddFileBuffer.append("<resname>"
		    + rs.getString("resname") + "</resname>" + "\r\n");

	    a4AppAcctDayAddFileBuffer.append("<restype>"
		    + rs.getString("restype") + "</restype>" + "\r\n");

	    Timestamp updatetimeStamp = rs.getTimestamp("updatetime");
	    String updatetime = "";
	    if (updatetimeStamp != null) {
		updatetime = DatetimeServices
			.converterToDateTimeWithT(updatetimeStamp);
	    }

	    a4AppAcctDayAddFileBuffer.append("<updatetime>" + updatetime
		    + "</updatetime>" + "\r\n");

	    a4AppAcctDayAddFileBuffer.append("</rcd>\r\n");
	}

	ConnectionManager.closeResultSet(rs);
	ConnectionManager.closePrepStmt(prepStmt);
    }

	@Override
	public void generateMakeFile() throws Exception {
	    generateA4AppAcctDayAddFile();
	}

	@Override
	public String getFileType() {
		return super.TYPE_SM4AI;
	}

	@Override
	public String getUploadFileDirInPath() {
		return "I";
	}
    
}