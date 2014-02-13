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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.fileupload.util.Streams;

import com.ailk.jt.util.DBUtil;
import com.ailk.uap.config.PropertiesUtil;
import com.ailk.uap.dbconn.ConnectionManager;
import com.ailk.uap.entity.DrUploadFileInfo;
import com.ailk.uap.makefile4new.sql.GoldSceneOperateDayAddFileSQL;
import com.ailk.uap.util.CommonUtil;
import com.ailk.uap.util.DatetimeServices;
import com.hp.ngecc.acl.domain.AclInfo;
import com.hp.ngecc.acl.domain.OptLog;
import com.hp.ngecc.acl.service.AclCompareService;

/**
 * 防火墙登录和操作日增量文件 type=SMFLO，SMFWL 
 * 
 * @author jinlin
 * @date 2014-01-21
 * @author liujie 
 * 		   liujie_09_24@163.com
 * 
 * @version 4.0
 * @since 4.0
 * @see com.ailk.uap.makefile4new.AbstractMakeDayFile
 */
public class FireWallLoginAndOperateDayAddFile extends AbstractMakeDayFile implements
	GoldSceneOperateDayAddFileSQL {
    private static String uap_file_uapload_smflo;
    private static long sumForSMFLO;
    private static Connection connSMP;
    private static OutputStreamWriter outputFLO;
    private static FileOutputStream fosFLO;
    private static BufferedWriter bwFLO;
    private static File uapLoadTempFileFLO;

    public static void initConfig() {
    	uap_file_uapload_smflo = PropertiesUtil.getValue("uap_file_uapload_for_smflo_db_now").trim();
    	uap_file_uapload_temp = PropertiesUtil.getValue("uap_file_uapload_temp");
    }

    public static void main(String[] args) {
    	initConfig();
	try {
        connSMP = DBUtil.getSMPConnection();
        AbstractMakeFile abstractMakeFile = new FireWallLoginAndOperateDayAddFile();
        abstractMakeFile.makeFile();

	} catch (Exception e) {
	    e.printStackTrace();
	    log.error("FireWallLoginAndOperateDayAddFile have Exception ...", e);
	} finally {
        ConnectionManager.closeConnection(connSMP);
	    try {
            if (bwFLO != null) {
                bwFLO.close();
            }
            if (outputFLO != null) {
                outputFLO.close();
            }
            if (fosFLO != null) {
                fosFLO.close();
            }
            if (uapLoadTempFileFLO != null && uapLoadTempFileFLO.exists())
                uapLoadTempFileFLO.delete();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
    }

    // 生成SMFWL文件方法
    private static void createSMFWLDayFile(StringBuffer a4SMFWLBuffer,
	    String type) throws FileNotFoundException,
	    UnsupportedEncodingException, IOException, Exception {

	createDayFileCommon(a4SMFWLBuffer, type,uap_file_uapload);
//	System.out.println("SMFWL===="+uploadFileName);
//	insertDrUploadFileInfo(type, sum);

    }

    // 生成SMFLO文件方法
    private static void createSMFLODayFile(StringBuffer a4SMFLOBuffer,
	    String type) throws FileNotFoundException,
	    UnsupportedEncodingException, IOException, Exception {
	createDayFileCommon(a4SMFLOBuffer, type,uap_file_uapload_smflo);
	System.out.println("SMFLO===="+uploadFileName);
	
	insertDrUploadFileInfo(type, sumForSMFLO);

    }

    // 插入DR_UPLOAD_FILE_INFO记录公共方法
    private static void insertDrUploadFileInfo(String type, long sum)
	    throws Exception {
	log.info("DR_UPLOAD_FILE_INFO**********insert**" + type
		+ "********Start*********");
	DrUploadFileInfo fileInfo = new DrUploadFileInfo();
	fileInfo.setFileName(uploadFileName);
	fileInfo.setFileSeq("000");
	fileInfo.setIntval("01DY");
	fileInfo.setProv(prov_code);
	fileInfo.setReloadFlag(reloadFlag);
	fileInfo.setTotal(Long.valueOf(sum));
	fileInfo.setType(type);
	fileInfo.setUploadStatus("0");

	String sql = "INSERT INTO DR_UPLOAD_FILE_INFO VALUES('"
		+ fileInfo.getFileName() + "','" + fileInfo.getProv() + "','"
		+ fileInfo.getType() + "','" + fileInfo.getIntval() + "','"
		+ fileInfo.getFileSeq() + "','" + fileInfo.getReloadFlag()
		+ "',TO_DATE('" + DatetimeServices.getNowDateTimeStr(conn)
		+ "','yyyy-MM-dd HH24:mi:ss')," + fileInfo.getTotal()
		+ ",TO_DATE('" + beginTime
		+ "','yyyy-MM-dd HH24:mi:ss'),TO_DATE('" + endTime
		+ "','yyyy-MM-dd HH24:mi:ss'),'" + fileInfo.getUploadStatus()
		+ "')";
	CommonUtil.insertUploadFileInfo(fileInfo, conn, beginTime, endTime);
	log.info("DR_UPLOAD_FILE_INFO**********insert**" + type
		+ " ********End*********");
    }

    //生成文件头方法
    private static void createDayFileCommon(StringBuffer a4FireWallDayFileBuffer,
	    String type,String uapfileupload) throws Exception, IOException, FileNotFoundException,
	    UnsupportedEncodingException {

	uploadFileName = type + "_" + prov_code + "_" + "01DY" + "_"
		+ DatetimeServices.getLastDayStr(conn) + "_" + "000" + "_"
		+ "000.xml";

	a4FireWallDayFileBuffer
		.append("<?xml version='1.0' encoding='UTF-8'?>\r\n");
	a4FireWallDayFileBuffer.append("<smp>\r\n");
	a4FireWallDayFileBuffer.append("<type>"+type+"</type>\r\n");
	a4FireWallDayFileBuffer.append("<province>" + prov_code + "</province>"
		+ "\r\n");
	a4FireWallDayFileBuffer.append("<createtime>" + createTime
		+ "</createtime>" + "\r\n");

	String a4FirewallLoginSql = "select distinct t.mainacctid,t.loginname,t.logintime,t.ip,t.resname,t.resaddress,t.result,t.logintype,t.loginsessionid from A4_FIRE_WALL_ALL_OPE t where t.is_acl = '3' or t.is_acl = '0' ";
	String a4FireWallOptSql = "select distinct t.mainacctid,t.loginname,t.opertime,t.opercontent,t.loginsessionid,t.resname,t.resaddress,t.result from A4_FIRE_WALL_ALL_OPE t where t.is_acl = '3' or t.is_acl = '0' ";

	log.info("a4FirewallLoginSql===" + a4FirewallLoginSql);
	log.info("a4FireWallOptSql===" + a4FireWallOptSql);
	log.info("getSMFWLDayFullInfo*******************Start******");

	if ("SMFWL".equals(type)) {
	    getDayFileSMFWLInfo(a4FirewallLoginSql, a4FireWallDayFileBuffer);
	} else if ("SMFLO".equals(type)) {
	    getDayFileSMFLOInfo(a4FireWallOptSql, a4FireWallDayFileBuffer);
	}
	log.info("get a4FirewallLoginSql*******************End*********");
	a4FireWallDayFileBuffer.append("</data>\r\n");
	a4FireWallDayFileBuffer.append("</smp>");
	log.info("a4FireWallDayFileBuffer====" + a4FireWallDayFileBuffer.toString());

	uapLoadTempFile = new File(uap_file_uapload_temp + "/" + uploadFileName);

        // 处理只在temp中删除了FWL的问题 2013-07-09
        if ("SMFWL".equals(type)) {
            uapLoadTempFile = uapLoadTempFile;
            writeFileBufferToTempFile(a4FireWallDayFileBuffer);
        } else if ("SMFLO".equals(type)) {
            uapLoadTempFileFLO = uapLoadTempFile;
            writeFLOBufferToTempFile(a4FireWallDayFileBuffer);
        }

        /*废弃代码*/
	/*writeA4AppAcctDayAddFileBufferToTempFile(a4FireWallDayFileBuffer);*/

	BufferedInputStream in = new BufferedInputStream(new FileInputStream(
		uapLoadTempFile));
	File uapLoadFile = new File(uapfileupload + "/" + uploadFileName);
	if (!uapLoadFile.exists()) {
	    uapLoadFile.createNewFile();
	}
	BufferedOutputStream out = new BufferedOutputStream(
		new FileOutputStream(uapLoadFile));
	Streams.copy(in, out, true);
	in.close();
	out.close();
    }

    private static void writeFLOBufferToTempFile(StringBuffer a4AppAcctDayAddFileBuffer) throws IOException {
        bwFLO.write(a4AppAcctDayAddFileBuffer.toString());
        bwFLO.flush();
        outputFLO.flush();
        fosFLO.flush();
    }

    ////生成SMFWL文件data部分方法
    private static void getDayFileSMFWLInfo(String qqLogSql,
	    StringBuffer a4FireWallLoginBuff) throws Exception {
	PreparedStatement prepStmt = conn.prepareStatement(qqLogSql,
		ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	ResultSet rs = prepStmt.executeQuery();
	rs.last();

	sum = rs.getRow();
	log.info("total sumForSMFWL ===" + sum);
	a4FireWallLoginBuff.append("<sum>" + sum + "</sum>\r\n");
	a4FireWallLoginBuff.append("<begintime>" + beginTimeWithT
		+ "</begintime>" + "\r\n");
	a4FireWallLoginBuff.append("<endtime>" + endTimeWithT + "</endtime>"
		+ "\r\n");
	a4FireWallLoginBuff.append("<data>");

	rs.beforeFirst();
	uapLoadTempFile = new File(uap_file_uapload_temp + "/" + uploadFileName);
	if (uapLoadTempFile.exists()) {
	    uapLoadTempFile.delete();
	    uapLoadTempFile.createNewFile();
	}
	fos = new FileOutputStream(uapLoadTempFile, true);
	output = new OutputStreamWriter(fos, "UTF-8");
	bw = new BufferedWriter(output);

	int i = 0;
	while (rs.next()) {
	    i++;
	    if (i % 1000 == 0) {
		log
			.info("writeA4GoldSceneOpDayAddFileBufferToTempFile******start***");
		writeFileBufferToTempFile(a4FireWallLoginBuff);
		log
			.info("writeGoldSceneOpDayAddFileBufferToTempFile******end***");
		a4FireWallLoginBuff.setLength(0);
	    }

	    a4FireWallLoginBuff.append("<rcd>\r\n");
	    a4FireWallLoginBuff.append("<seq>" + String.valueOf(i)
		    + "</seq>" + "\r\n");
	    a4FireWallLoginBuff.append("<mainacctid>"
		    + rs.getString("mainacctid").trim() + "</mainacctid>"
		    + "\r\n");
	    a4FireWallLoginBuff.append("<loginname>"
		    + rs.getString("loginname").trim().replace(" ", "T")
		    + "</loginname>" + "\r\n");
	    a4FireWallLoginBuff.append("<logintime>"
		    + rs.getString("logintime").trim().replace(" ", "T") + "</logintime>" + "\r\n");
	    a4FireWallLoginBuff.append("<ip>"
		    + rs.getString("ip").trim() + "</ip>" + "\r\n");
	    a4FireWallLoginBuff.append("<resname>"
		    + rs.getString("resname").trim() + "</resname>" + "\r\n");
	    a4FireWallLoginBuff.append("<resaddress>"
		    + rs.getString("resaddress").trim() + "</resaddress>" + "\r\n");
	    a4FireWallLoginBuff.append("<result>"
		    + rs.getString("result").trim() + "</result>" + "\r\n");
	    a4FireWallLoginBuff.append("<logintype>"
		    + rs.getString("logintype").trim() + "</logintype>" + "\r\n");
	    a4FireWallLoginBuff.append("<loginsessionid>"
		    + rs.getString("loginsessionid").trim() + "</loginsessionid>" + "\r\n");
	   
	    a4FireWallLoginBuff.append("</rcd>\r\n");

	}
	ConnectionManager.closeResultSet(rs);
	ConnectionManager.closePrepStmt(prepStmt);
    }
    //生成SMFLO文件data部分方法
    private static void getDayFileSMFLOInfo(String qqLogSql,
	    StringBuffer a4FireWallOptBuff) throws Exception {
	PreparedStatement prepStmt = conn.prepareStatement(qqLogSql,
		ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	ResultSet rs = prepStmt.executeQuery();
	rs.last();

	sumForSMFLO = rs.getRow();
	log.info("total sumForSMFLO ===" + sumForSMFLO);
	a4FireWallOptBuff.append("<sum>" + sumForSMFLO + "</sum>\r\n");
	a4FireWallOptBuff.append("<begintime>" + beginTimeWithT
		+ "</begintime>" + "\r\n");
	a4FireWallOptBuff.append("<endtime>" + endTimeWithT + "</endtime>"
		+ "\r\n");
	a4FireWallOptBuff.append("<data>");

	rs.beforeFirst();
	uapLoadTempFile = new File(uap_file_uapload_temp + "/" + uploadFileName);
	if (uapLoadTempFile.exists()) {
	    uapLoadTempFile.delete();
	    uapLoadTempFile.createNewFile();
	}
	fosFLO = new FileOutputStream(uapLoadTempFile, true);
	outputFLO = new OutputStreamWriter(fosFLO, "UTF-8");
	bwFLO = new BufferedWriter(outputFLO);

	int i = 0;
	while (rs.next()) {
	    i++;
	    String opercontent =  rs.getString("opercontent").trim();
	    if(opercontent.length()>1000) {
	    	opercontent = opercontent.substring(0,1000);
	    }
	    if (i % 1000 == 0) {
		log.info("writeA4FireWallOptDayAddFileBufferToTempFile******start***");
		writeFLOBufferToTempFile(a4FireWallOptBuff);
		log.info("writeA4FireWallOptDayAddFileBufferToTempFile******end***");
		a4FireWallOptBuff.setLength(0);
	    }
	    a4FireWallOptBuff.append("<rcd>\r\n");
	    a4FireWallOptBuff.append("<seq>" + String.valueOf(i)
		    + "</seq>" + "\r\n");
	    a4FireWallOptBuff.append("<mainacctid>"
		    + rs.getString("mainacctid").trim() + "</mainacctid>"
		    + "\r\n");
	    a4FireWallOptBuff.append("<loginname>"
		    + rs.getString("loginname").trim().replace(" ", "T")
		    + "</loginname>" + "\r\n");
	    a4FireWallOptBuff.append("<opertime>"
		    + rs.getString("opertime").trim().replace(" ", "T") + "</opertime>" + "\r\n");
        a4FireWallOptBuff.append("<opercontent><![CDATA["
                + opercontent + "]]></opercontent>" + "\r\n");
	    a4FireWallOptBuff.append("<loginsessionid>"
		    + rs.getString("loginsessionid").trim() + "</loginsessionid>" + "\r\n");
	    a4FireWallOptBuff.append("<resname>"
		    + rs.getString("resname").trim() + "</resname>" + "\r\n");
	    a4FireWallOptBuff.append("<resaddress>"
		    + rs.getString("resaddress").trim() + "</resaddress>" + "\r\n");
	    a4FireWallOptBuff.append("<result>"
		    + rs.getString("result").trim() + "</result>" + "\r\n");
	   
	    a4FireWallOptBuff.append("</rcd>\r\n");
	}
	ConnectionManager.closeResultSet(rs);
	ConnectionManager.closePrepStmt(prepStmt);
    }

    private static boolean dataCompare() throws SQLException {
        String smpSql = "select t.operator, " +
                "       t.ip, " +
                "       to_char(t.opertime, 'yyyy-mm-dd hh24:mi:ss') opertime, " +
                "       t.value2 " +
                "  from SMP.SMFWC_371_01DY t " +
                " where t.is_upload = '0' ";

        PreparedStatement prepStmt = connSMP.prepareStatement(smpSql, ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = prepStmt.executeQuery();
        List<AclInfo> aclList = new ArrayList<AclInfo>();
        while (rs.next()) {
            AclInfo smp = new AclInfo();
            smp.setOperator(CommonUtil.getStr(rs.getString("operator")));
            smp.setIp(CommonUtil.getStr(rs.getString("ip")));
            smp.setOperateTime(CommonUtil.getStr(rs.getString("opertime")));
            smp.setAclContent(CommonUtil.getStr(rs.getString("value2")));
            aclList.add(smp);
        }
        ConnectionManager.closeResultSet(rs);
        ConnectionManager.closePrepStmt(prepStmt);

        String a4Sql = "select w.loginname, w.resaddress, w.opertime, w.opercontent " +
                "  from (select distinct t.mainacctid, " +
                "                        t.loginname, " +
                "                        t.opertime, " +
                "                        t.opercontent, " +
                "                        t.loginsessionid, " +
                "                        t.resname, " +
                "                        t.resaddress, " +
                "                        t.result, " +
                "                        t.is_acl " +
                "          from A4_FIRE_WALL_ALL_OPE t) w " +
                " where w.is_acl = '3'";
        PreparedStatement prepStmt2 = conn.prepareStatement(a4Sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        ResultSet rs2 = prepStmt2.executeQuery();
        List<OptLog> optList = new ArrayList<OptLog>();
        while (rs2.next()) {
            OptLog a4 = new OptLog();
            a4.setOperator(CommonUtil.getStr(rs2.getString("loginname")));
            a4.setIp(CommonUtil.getStr(rs2.getString("resaddress")));
            a4.setOperateTime(CommonUtil.getStr(rs2.getString("opertime")));
            a4.setOptContent(CommonUtil.getStr(rs2.getString("opercontent")));
            optList.add(a4);
        }
        ConnectionManager.closeResultSet(rs2);
        ConnectionManager.closePrepStmt(prepStmt2);

        return AclCompareService.getInstance().compare(aclList, optList);
    }

	@Override
	public void generateMakeFile() throws Exception {
		 StringBuffer a4SMFWLBuffer = new StringBuffer();
	     StringBuffer a4SMFLOBuffer = new StringBuffer();
	     createSMFWLDayFile(a4SMFWLBuffer, "SMFWL");
	     createSMFLODayFile(a4SMFLOBuffer, "SMFLO");
	}

	@Override
	public String getFileType() {
		return super.TYPE_SMFWL;
	}

}