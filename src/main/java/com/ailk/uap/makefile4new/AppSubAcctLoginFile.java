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
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.fileupload.util.Streams;

import com.ailk.jt.util.DBUtil;
import com.ailk.jt.util.DateUtil;
import com.ailk.jt.util.SQLUtil;
import com.ailk.uap.config.PropertiesUtil;

/**
 * 4A侧记录应用系统登录及操作统计日增量文件 type=SM4AR
 * 
 */
public class AppSubAcctLoginFile extends AbstractMakeDayFile {
    private static Connection connuap;
    private static String app_code;
    private static String op_type_id;
    private static String crm_domain_id;

    public static void initConfig() {
	app_code = PropertiesUtil.getValue("crm_appcode").trim();
	op_type_id = PropertiesUtil.getValue("appres_sso_oper_type_id").trim();
	crm_domain_id = PropertiesUtil.getValue("crm_domain_id").trim();
    }

    public static void main(String[] args) {
    	initConfig();
	    connuap = DBUtil.getAiuap20Connection();
	    AbstractMakeFile makeFile = new AppSubAcctLoginFile();
		makeFile.makeFile();
    }

    private static void generateAppAcctLoginFile() throws Exception,
	    IOException, FileNotFoundException, UnsupportedEncodingException {
	StringBuffer subAcctFileBuffer = new StringBuffer();
	subAcctFileBuffer.append("<?xml version='1.0' encoding='UTF-8'?>\r\n");

	subAcctFileBuffer.append("<smp>\r\n");
	subAcctFileBuffer.append("<type>SM4AR</type>\r\n");
	subAcctFileBuffer.append("<province>" + prov_code + "</province>"
		+ "\r\n");
	subAcctFileBuffer.append("<createtime>"
		+ createTime.replaceAll(" ", "T") + "</createtime>" + "\r\n");

	sum = 48L;
	log.info("total sum ===" + sum);
	subAcctFileBuffer.append("<sum>" + sum + "</sum>" + "\r\n");
	subAcctFileBuffer.append("<begintime>" + beginTime.replace(" ", "T")
		+ "</begintime>" + "\r\n");
	subAcctFileBuffer.append("<endtime>" + endTime.replace(" ", "T")
		+ "</endtime>" + "\r\n");
	subAcctFileBuffer.append("<data>\r\n");

	log.info("getSubAcctInfo*******************Start*********");
	getSubAcctInfo(subAcctFileBuffer);
	log.info("getSubAcctInfo*******************End*********");

	subAcctFileBuffer.append("</data>\r\n");
	subAcctFileBuffer.append("</smp>");

	log.info("subAcctFileBuffer====" + subAcctFileBuffer.toString());

	uapLoadTempFile = new File(uap_file_uapload_temp + "/" + uploadFileName);

	writeMainAcctFileBufferToTempFile(subAcctFileBuffer);

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

    private static void writeMainAcctFileBufferToTempFile(
	    StringBuffer mainAcctFileBuffer) throws IOException {
	bw.write(mainAcctFileBuffer.toString());
	bw.flush();
	output.flush();
	fos.flush();
    }

    private static void getSubAcctInfo(StringBuffer mainAcctFileBuffer)
	    throws Exception {
	String truncatea4aartempsql = "truncate table a4_4ar_temp";
	PreparedStatement trunprepStmt = connuap
		.prepareStatement(truncatea4aartempsql);
	ResultSet trunrs = trunprepStmt.executeQuery();

	DBUtil.closeResultSet(trunrs);
	DBUtil.closePrepStmt(trunprepStmt);

	uapLoadTempFile = new File(uap_file_uapload_temp + "/"
		+ uploadFileName);
	if (uapLoadTempFile.exists()) {
	    uapLoadTempFile.delete();
	    uapLoadTempFile.createNewFile();
	}
	fos = new FileOutputStream(uapLoadTempFile, true);
	output = new OutputStreamWriter(fos, "UTF-8");
	bw = new BufferedWriter(output);

	Calendar calendar = Calendar.getInstance();
	calendar.set(5, calendar.get(5) - 1);
	calendar.set(11, 0);
	calendar.set(12, 0);
	calendar.set(13, 0);
	calendar.set(14, 0);
	Timestamp a = new Timestamp(calendar.getTimeInMillis());
	String init = a.toString().replaceAll("\\.0", "");

	Connection uapConnection = DBUtil.getAiuap20Connection();

	String tempSql = SQLUtil.getSql("sub_4ar_Acct_Login_Opearte_Sql");

	HashMap<String, String> parameterMap = new HashMap<String, String>();

	Calendar cal = Calendar.getInstance();
	cal.add(5, -1);
	Date date = cal.getTime();
	String dataBasePart = DateUtil.formatDateyyyyMM(date);
	parameterMap.put("dataBasePart", "part_app_log_" + dataBasePart);
	parameterMap.put("app_code", app_code);
	parameterMap.put("op_type_id", op_type_id);
	parameterMap.put("crm_domain_id", crm_domain_id);
	parameterMap.put("begin", beginTime);
	parameterMap.put("end", endTime);

	String mainAcctDayAddFileCountSql = SQLUtil.replaceParameter(tempSql,
		parameterMap, false);
	log.info("mainAcctDayAddFileCountSql======="
		+ mainAcctDayAddFileCountSql);
	PreparedStatement prepStmt = uapConnection
		.prepareStatement(mainAcctDayAddFileCountSql);
	ResultSet rs = prepStmt.executeQuery();

	String updatea4aarsql = "update a4_4ar t  set (t.dlvalue, t.czvalue) = (select w.dlvalue, w.czvalue  from a4_4ar_temp w   where w.a = t.num)";

	PreparedStatement updateprepStmt = connuap
		.prepareStatement(updatea4aarsql);
	ResultSet updaters = updateprepStmt.executeQuery();

	DBUtil.closeResultSet(updaters);
	DBUtil.closePrepStmt(updateprepStmt);

	String selectvalue = "select * from a4_4ar";
	PreparedStatement selectprepStmt = uapConnection
		.prepareStatement(selectvalue);
	ResultSet selectrs = selectprepStmt.executeQuery();

	while (selectrs.next()) {
	    String resname = selectrs.getString("resname");
	    String restype = selectrs.getString("restype");
	    int num = selectrs.getInt("num");
	    int dlvalue = selectrs.getInt("dlvalue");
	    int czvalue = selectrs.getInt("czvalue");

	    mainAcctFileBuffer.append("<rcd>\r\n");
	    mainAcctFileBuffer.append("<seq>" + num + "</seq>" + "\r\n");
	    mainAcctFileBuffer.append("<resname>" + resname + "</resname>"
		    + "\r\n");
	    mainAcctFileBuffer.append("<restype>" + restype + "</restype>"
		    + "\r\n");
	    mainAcctFileBuffer.append("<num>" + num + "</num>" + "\r\n");
	    mainAcctFileBuffer.append("<dlvalue>" + dlvalue + "</dlvalue>"
		    + "\r\n");
	    mainAcctFileBuffer.append("<czvalue>" + czvalue + "</czvalue>"
		    + "\r\n");
	    mainAcctFileBuffer.append("</rcd>\r\n");
	}

	DBUtil.closeResultSet(rs);
	DBUtil.closePrepStmt(prepStmt);
	DBUtil.closeResultSet(selectrs);
	DBUtil.closePrepStmt(selectprepStmt);
    }

	@Override
	public void generateMakeFile() throws Exception {
		generateAppAcctLoginFile();
	}

	@Override
	public String getFileType() {
		return super.TYPE_SM4AR;
	}
    
	protected  Connection getConn() {
		return DBUtil.getAuditConnection();
	}

	public  String getUploadFileDirInPath() {
		return "R";
	}
}