package com.ailk.jt.mannul;

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

import org.apache.commons.fileupload.util.Streams;
import org.apache.log4j.Logger;

import com.ailk.jt.util.DBUtil;
import com.ailk.jt.util.PropertiesUtil;
import com.ailk.jt.util.SQLUtil;
import com.ailk.uap.entity.DrUploadFileInfo;
import com.ailk.uap.util.CommonUtil;
import com.ailk.uap.util.DatetimeServices;

public class A4AppAcctDayAddFile
{
  private static final Logger log = Logger.getLogger(A4AppAcctDayAddFile.class);
  private static String uap_file_uapload;
  private static String uap_file_uapload_temp;
  private static String uap_version;
  private static final String type = "SM4AI";
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
      conn = DBUtil.getAiuap20Connection();
      createTime = DatetimeServices.getNowDateTimeStrWithT(conn);
      beginTime = DatetimeServices.getLastDayStartTimeStr(conn);
      beginTimeWithT = DatetimeServices.getLastDayStartTimeStrWithT(conn);

      endTime = DatetimeServices.getTodayStartTimeStr(conn);
      endTimeWithT = DatetimeServices.getTodayStartTimeStrWithT(conn);

      uploadFileName = "SM4AI.xml";

      if (args.length != 0) {
        uploadFileName = args[0];
        beginTime = args[1];
        endTime = args[2];
        beginTimeWithT = beginTime.replace(" ", "T");
        endTimeWithT = endTime.replace(" ", "T");
        int reloadFlagIndex = uploadFileName.lastIndexOf(".");
        if (!uploadFileName.substring(reloadFlagIndex - 3, reloadFlagIndex).equals("000"))
        {
          reloadFlag = "1";
        }
      }

      log.info("generateA4AppAcctDayAddFile  ******Start***************");

      generateA4AppAcctDayAddFile();

      log.info("generateA4AppAcctDayAddFile  ******End ***************");

      log.info("DR_UPLOAD_FILE_INFO**********insert ********Start*********");

      DrUploadFileInfo fileInfo = new DrUploadFileInfo();
      fileInfo.setFileName(uploadFileName);
      fileInfo.setFileSeq("000");
      fileInfo.setIntval("01DY");
      fileInfo.setProv(prov_code);
      fileInfo.setReloadFlag(reloadFlag);
      fileInfo.setTotal(Long.valueOf(sum));
      fileInfo.setType("SM4AI");
      fileInfo.setUploadStatus("0");

      CommonUtil.insertUploadFileInfo(fileInfo, conn, beginTime, endTime);
      long statisticRunEndTime = System.currentTimeMillis();
      log.info("GENERATE A4AppAcct Day Add FILE  TOTALTIME======" + (statisticRunEndTime - statisticRunStartTime) / 1000L + "s");
    }
    catch (Exception e)
    {
      e.printStackTrace();
      log.error(e.getMessage());
    }
    finally {
      DBUtil.closeConnection(conn);
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

  private static void generateA4AppAcctDayAddFile() throws Exception, IOException, FileNotFoundException, UnsupportedEncodingException
  {
    StringBuffer a4AppAcctDayAddFileBuffer = new StringBuffer();
    a4AppAcctDayAddFileBuffer.append("<?xml version='1.0' encoding='UTF-8'?>\r\n");

    a4AppAcctDayAddFileBuffer.append("<bomc>\r\n");
    a4AppAcctDayAddFileBuffer.append("<type>SM4AI</type>\r\n");
    a4AppAcctDayAddFileBuffer.append("<province>" + prov_code + "</province>" + "\r\n");

    a4AppAcctDayAddFileBuffer.append("<createtime>" + createTime + "</createtime>" + "\r\n");

    String a4AppAcctDayAddFileSql = "";
    String a4AppAcctDayAddFileCountSql = "";
    log.info("uap_version=================" + uap_version);

    a4AppAcctDayAddFileSql = SQLUtil.getSql("a4_");
    log.info("a4AppAcctDayAddFileSql===" + a4AppAcctDayAddFileSql);

    a4AppAcctDayAddFileCountSql = "SELECT COUNT(1) FROM (" + a4AppAcctDayAddFileSql + ")";

    log.info("a4AppAcctDayAddFileCountSql===" + a4AppAcctDayAddFileCountSql);

    sum = getA4AppAcctFullCount(a4AppAcctDayAddFileCountSql).longValue();
    log.info("total sum ===" + sum);
    a4AppAcctDayAddFileBuffer.append("<sum>" + sum + "</sum>" + "\r\n");
    a4AppAcctDayAddFileBuffer.append("<begintime>" + beginTimeWithT + "</begintime>" + "\r\n");

    a4AppAcctDayAddFileBuffer.append("<endtime>" + endTimeWithT + "</endtime>" + "\r\n");

    a4AppAcctDayAddFileBuffer.append("<data>");

    log.info("getA4AppAcctFullInfo*******************Start******");
    getA4AppAcctDayAddFullInfo(a4AppAcctDayAddFileSql, a4AppAcctDayAddFileBuffer);

    log.info("getA4AppAcctFullInfo*******************End*********");

    a4AppAcctDayAddFileBuffer.append("</data>\r\n");
    a4AppAcctDayAddFileBuffer.append("</bomc>");

    log.info("a4AppAcctDayAddFileBuffer====" + a4AppAcctDayAddFileBuffer.toString());

    uapLoadTempFile = new File(uap_file_uapload_temp + "/" + uploadFileName);

    writeA4AppAcctDayAddFileBufferToTempFile(a4AppAcctDayAddFileBuffer);

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

  private static Long getA4AppAcctFullCount(String sql) throws Exception {
    return Long.valueOf(0L);
  }

  private static void getA4AppAcctDayAddFullInfo(String a4AppAcctDayAddFileSql, StringBuffer a4AppAcctDayAddFileBuffer) throws Exception
  {
    int i = 0;
    uapLoadTempFile = new File(uap_file_uapload_temp + "/" + uploadFileName);
    if (uapLoadTempFile.exists()) {
      uapLoadTempFile.delete();
      uapLoadTempFile.createNewFile();
    }
    fos = new FileOutputStream(uapLoadTempFile, true);
    output = new OutputStreamWriter(fos, "UTF-8");
    bw = new BufferedWriter(output);
  }
}