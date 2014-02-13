package com.ailk.jt.mannul;

import com.ailk.jt.util.PropertiesUtil;
import com.ailk.jt.util.SQLUtil;
import com.ailk.uap.dbconn.ConnectionManager;
import com.ailk.uap.entity.DrUploadFileInfo;
import com.ailk.uap.util.CommonUtil;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import org.apache.commons.fileupload.util.Streams;
import org.apache.log4j.Logger;

public class BOSSAppAcctDayAddFile
{
  private static final Logger log = Logger.getLogger(BOSSAppAcctDayAddFile.class);
  private static String uap_file_uapload;
  private static String uap_file_uapload_temp;
  private static String uap_version;
  private static final String type = "SMAAI";
  private static String prov_code;
  private static final String intval = "01DY";
  private static String createTime;
  private static String beginTime;
  private static String beginTimeWithT;
  private static String endTime;
  private static String endTimeWithT;
  private static String uploadFileName;
  private static String reloadFlag = "0";
  private static long sum;
  private static Connection conn;
  private static OutputStreamWriter output;
  private static FileOutputStream fos;
  private static BufferedWriter bw;
  private static File uapLoadTempFile;

  public static void readConfig()
  {
    uap_file_uapload = PropertiesUtil.getValue("uap_file_uapload").trim();
    uap_version = PropertiesUtil.getValue("uap_version").trim();
    prov_code = PropertiesUtil.getValue("prov_code").trim();
    uap_file_uapload_temp = PropertiesUtil.getValue("uap_file_uapload_temp").trim();
  }

  public static void main(String[] args)
  {
    readConfig();
    log.info("UpLoadFileThread start to run......");
    log.info("uap_file_uapload==" + uap_file_uapload);
    log.info("uap_version==" + uap_version);
    long statisticRunStartTime = System.currentTimeMillis();
    try {
      conn = ConnectionManager.getUapAcctConnection();
      createTime = DatetimeServices.getNowDateTimeStrWithT(conn);
      beginTime = DatetimeServices.getLastDayStartTimeStr(conn);
      beginTimeWithT = DatetimeServices.getLastDayStartTimeStrWithT(conn);
      endTime = DatetimeServices.getTodayStartTimeStr(conn);
      endTimeWithT = DatetimeServices.getTodayStartTimeStrWithT(conn);

      uploadFileName = "SMAAI_" + prov_code + "_" + "01DY" + "_" + DatetimeServices.getLastDayStr(conn) + "_" + "000" + "_" + "000.xml";

      if (args.length != 0) {
        uploadFileName = args[0];
        beginTime = args[1];
        endTime = args[2];
        beginTimeWithT = beginTime.replace(" ", "T");
        endTimeWithT = endTime.replace(" ", "T");
        int reloadFlagIndex = uploadFileName.lastIndexOf(".");
        if (!uploadFileName.substring(reloadFlagIndex - 3, reloadFlagIndex).equals("000")) {
          reloadFlag = "1";
        }
      }

      log.info("generateA4AppAcctDayAddFile  ******Start***************");

      generateBOSSAppAcctDayAddFile();

      log.info("generateA4AppAcctDayAddFile  ******End ***************");

      log.info("DR_UPLOAD_FILE_INFO**********insert ********Start*********");

      DrUploadFileInfo fileInfo = new DrUploadFileInfo();
      fileInfo.setFileName(uploadFileName);
      fileInfo.setFileSeq("000");
      fileInfo.setIntval("01DY");
      fileInfo.setProv(prov_code);
      fileInfo.setReloadFlag(reloadFlag);
      fileInfo.setTotal(Long.valueOf(sum));
      fileInfo.setType("SMAAI");
      fileInfo.setUploadStatus("0");

      CommonUtil.insertUploadFileInfo(fileInfo, conn, beginTime, endTime);
      long statisticRunEndTime = System.currentTimeMillis();
      log.info("GENERATE A4AppAcct Day Add FILE  TOTALTIME======" + (statisticRunEndTime - statisticRunStartTime) / 1000L + "s");
    }
    catch (Exception e) {
      e.printStackTrace();
      log.error(e.getMessage());
    }
    finally {
      ConnectionManager.closeConnection(conn);
      try
      {
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
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }
  }

  private static void generateBOSSAppAcctDayAddFile() throws Exception, IOException, FileNotFoundException, UnsupportedEncodingException
  {
    StringBuffer bossAppAcctDayAddFileBuffer = new StringBuffer();
    bossAppAcctDayAddFileBuffer.append("<?xml version='1.0' encoding='UTF-8'?>\r\n");
    bossAppAcctDayAddFileBuffer.append("<bomc>\r\n");
    bossAppAcctDayAddFileBuffer.append("<type>SMAAI</type>\r\n");
    bossAppAcctDayAddFileBuffer.append("<province>" + prov_code + "</province>" + "\r\n");
    bossAppAcctDayAddFileBuffer.append("<createtime>" + createTime + "</createtime>" + "\r\n");

    String bossAppAcctDayAddFileSql = "";
    String bossAppAcctDayAddFileCountSql = "";
    log.info("uap_version=================" + uap_version);

    bossAppAcctDayAddFileSql = SQLUtil.getSql(PropertiesUtil.getValue("sql_boss_acct_cs"));

    log.info("a4AppAcctDayAddFileSql===" + bossAppAcctDayAddFileSql);

    bossAppAcctDayAddFileCountSql = "SELECT COUNT(1) FROM (" + bossAppAcctDayAddFileSql + ")";

    log.info("a4AppAcctDayAddFileCountSql===" + bossAppAcctDayAddFileCountSql);

    sum = getA4AppAcctFullCount(bossAppAcctDayAddFileCountSql).longValue();
    log.info("total sum ===" + sum);
    bossAppAcctDayAddFileBuffer.append("<sum>" + sum + "</sum>" + "\r\n");
    bossAppAcctDayAddFileBuffer.append("<begintime>" + beginTimeWithT + "</begintime>" + "\r\n");
    bossAppAcctDayAddFileBuffer.append("<endtime>" + endTimeWithT + "</endtime>" + "\r\n");
    bossAppAcctDayAddFileBuffer.append("<data>");

    log.info("getA4AppAcctFullInfo*******************Start******");
    getA4AppAcctDayAddFullInfo(bossAppAcctDayAddFileSql, bossAppAcctDayAddFileBuffer);
    log.info("getA4AppAcctFullInfo*******************End*********");

    bossAppAcctDayAddFileBuffer.append("</data>\r\n");
    bossAppAcctDayAddFileBuffer.append("</bomc>");

    log.info("a4AppAcctDayAddFileBuffer====" + bossAppAcctDayAddFileBuffer.toString());

    uapLoadTempFile = new File(uap_file_uapload_temp + "/" + uploadFileName);

    writeA4AppAcctDayAddFileBufferToTempFile(bossAppAcctDayAddFileBuffer);

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

  private static void writeA4AppAcctDayAddFileBufferToTempFile(StringBuffer a4AppAcctDayAddFileBuffer) throws IOException
  {
    bw.write(a4AppAcctDayAddFileBuffer.toString());
    bw.flush();
    output.flush();
    fos.flush();
  }

  private static Long getA4AppAcctFullCount(String sql) throws Exception
  {
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

  private static void getA4AppAcctDayAddFullInfo(String a4AppAcctDayAddFileSql, StringBuffer a4AppAcctDayAddFileBuffer) throws Exception
  {
    PreparedStatement prepStmt = conn.prepareStatement(a4AppAcctDayAddFileSql);
    ResultSet rs = prepStmt.executeQuery();
    int i = 0;
    uapLoadTempFile = new File(uap_file_uapload_temp + "/" + uploadFileName);
    if (uapLoadTempFile.exists()) {
      uapLoadTempFile.delete();
      uapLoadTempFile.createNewFile();
    }
    fos = new FileOutputStream(uapLoadTempFile, true);
    output = new OutputStreamWriter(fos, "UTF-8");
    bw = new BufferedWriter(output);

    while (rs.next())
    {
      i++;
      if (i % 1000 == 0) {
        log.info("writeA4AppAcctDayAddFileBufferToTempFile******start***");

        writeA4AppAcctDayAddFileBufferToTempFile(a4AppAcctDayAddFileBuffer);
        log.info("writeA4AppAcctDayAddFileBufferToTempFile******end***");
        a4AppAcctDayAddFileBuffer.setLength(0);
      }
      a4AppAcctDayAddFileBuffer.append("<rcd>\r\n");
      a4AppAcctDayAddFileBuffer.append("<seq>" + String.valueOf(i) + "</seq>" + "\r\n");
      a4AppAcctDayAddFileBuffer.append("<mode>" + rs.getString("chage_mode") + "</mode>" + "\r\n");
      a4AppAcctDayAddFileBuffer.append("<id>" + String.valueOf(rs.getLong("staff_id")) + "</id>" + "\r\n");
      a4AppAcctDayAddFileBuffer.append("<loginname>" + rs.getString("loginname") + "</loginname>" + "\r\n");
      a4AppAcctDayAddFileBuffer.append("<resname>BOSS</resname>\r\n");
      a4AppAcctDayAddFileBuffer.append("<restype>11</restype>\r\n");
      Timestamp updatetimeStamp = rs.getTimestamp("updatetime");
      String updatetime = "";
      if (updatetimeStamp != null) {
        updatetime = DatetimeServices.converterToDateTimeWithT(updatetimeStamp);
      }
      a4AppAcctDayAddFileBuffer.append("<updatetime>" + updatetime + "</updatetime>" + "\r\n");
      a4AppAcctDayAddFileBuffer.append("</rcd>\r\n");
    }

    ConnectionManager.closeResultSet(rs);
    ConnectionManager.closePrepStmt(prepStmt);
  }
}