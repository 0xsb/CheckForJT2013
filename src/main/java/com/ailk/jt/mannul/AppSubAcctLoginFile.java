package com.ailk.jt.mannul;

import com.ailk.jt.util.DBUtil;
import com.ailk.jt.util.PropertiesUtil;
import com.ailk.uap.entity.DrUploadFileInfo;
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
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Calendar;
import org.apache.commons.fileupload.util.Streams;
import org.apache.log4j.Logger;

public class AppSubAcctLoginFile
{
  private static final Logger log = Logger.getLogger(AppSubAcctLoginFile.class);
  private static String uap_file_uapload;
  private static String uap_file_uapload_temp;
  private static final String type = "SM4AR";
  private static String prov_code;
  private static final String intval = "01DY";
  private static String createTime;
  private static String beginTime;
  private static String endTime;
  private static String beginTimeWithT;
  private static String endTimeWithT;
  private static String uploadFileName;
  private static String fileSeq;
  private static String reloadFlag = "0";
  private static long sum;
  private static Connection conn;
  private static OutputStreamWriter output;
  private static FileOutputStream fos;
  private static BufferedWriter bw;
  private static File uapLoadTempFile;
  private static String app_code;
  private static String op_type_id;
  private static String res_name;
  private static String res_type;
  private static String crm_domain_id;

  public static void readConfig()
  {
    uap_file_uapload = PropertiesUtil.getValue("uap_file_uapload");
    prov_code = PropertiesUtil.getValue("prov_code").trim();
    uap_file_uapload_temp = PropertiesUtil.getValue("uap_file_uapload_temp").trim();

    app_code = PropertiesUtil.getValue("crm_appcode").trim();
    op_type_id = PropertiesUtil.getValue("appres_sso_oper_type_id").trim();
    crm_domain_id = PropertiesUtil.getValue("crm_domain_id").trim();
    res_name = PropertiesUtil.getValue("boss_appcode").trim();
    res_type = "12";
  }

  public static void main(String[] args) {
    readConfig();
    log.info("AppSubAcctLoginFile start to run......");
    log.info("uap_file_uapload==" + uap_file_uapload);
    long statisticRunStartTime = System.currentTimeMillis();
    System.out.println("AppSubAcctLoginFile...");
    try {
      conn = DBUtil.getAuditConnection();
      createTime = DatetimeServices.getNowDateTimeStrWithT(conn);
      beginTime = DatetimeServices.getLastDayStartTimeStr(conn);

      beginTimeWithT = DatetimeServices.getLastDayStartTimeStrWithT(conn);

      endTime = DatetimeServices.getTodayStartTimeStr(conn);

      endTimeWithT = DatetimeServices.getTodayStartTimeStrWithT(conn);

      fileSeq = DatetimeServices.getHourFileSeq(conn);
      uploadFileName = "SM4AR.xml";
      if (args.length != 0)
      {
        uploadFileName = args[0];

        beginTime = args[1];

        endTime = args[2];

        int fileSeqIndex = uploadFileName.lastIndexOf("_");
        fileSeq = uploadFileName.substring(fileSeqIndex - 3, fileSeqIndex);

        int reloadFlagIndex = uploadFileName.lastIndexOf(".");
        if (!uploadFileName.substring(reloadFlagIndex - 3, reloadFlagIndex).equals("000"))
        {
          reloadFlag = "1";
        }

      }

      log.info("generateAppSubAcctLoginFile  ******Start***************");

      generateAppAcctLoginFile();

      log.info("generateAppSubAcctLoginFile  ******End ***************");

      log.info("DR_UPLOAD_FILE_INFO**********insert ********Start*********");

      DrUploadFileInfo fileInfo = new DrUploadFileInfo();
      fileInfo.setFileName(uploadFileName);
      fileInfo.setFileSeq(fileSeq);
      fileInfo.setReloadFlag(reloadFlag);
      fileInfo.setIntval("01DY");
      fileInfo.setProv(prov_code);
      fileInfo.setTotal(Long.valueOf(sum));
      fileInfo.setType("SM4AR");
      fileInfo.setUploadStatus("0");

      insertUploadFileInfo(fileInfo);
      long statisticRunEndTime = System.currentTimeMillis();
      log.info("GENERATE AppSubAcctLoginFile FULL FILE  TOTALTIME======" + (statisticRunEndTime - statisticRunStartTime) / 1000L + "s");
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
      log.info("-------------MainAcctLoginFile end-----------------");
    }
  }

  private static void generateAppAcctLoginFile()
    throws Exception, IOException, FileNotFoundException, UnsupportedEncodingException
  {
    StringBuffer subAcctFileBuffer = new StringBuffer();
    subAcctFileBuffer.append("<?xml version='1.0' encoding='UTF-8'?>\r\n");

    subAcctFileBuffer.append("<bomc>\r\n");
    subAcctFileBuffer.append("<type>SM4AR</type>\r\n");
    subAcctFileBuffer.append("<province>" + prov_code + "</province>" + "\r\n");

    subAcctFileBuffer.append("<createtime>" + createTime.replaceAll(" ", "T") + "</createtime>" + "\r\n");

    sum = 48L;
    log.info("total sum ===" + sum);
    subAcctFileBuffer.append("<sum>" + sum + "</sum>" + "\r\n");
    subAcctFileBuffer.append("<begintime>" + beginTime.replace(" ", "T") + "</begintime>" + "\r\n");

    subAcctFileBuffer.append("<endtime>" + endTime.replace(" ", "T") + "</endtime>" + "\r\n");

    subAcctFileBuffer.append("<data>\r\n");

    log.info("getSubAcctInfo*******************Start*********");
    getSubAcctInfo(subAcctFileBuffer);
    log.info("getSubAcctInfo*******************End*********");

    subAcctFileBuffer.append("</data>\r\n");
    subAcctFileBuffer.append("</bomc>");

    log.info("subAcctFileBuffer====" + subAcctFileBuffer.toString());

    uapLoadTempFile = new File(uap_file_uapload_temp + "/" + uploadFileName);

    writeMainAcctFileBufferToTempFile(subAcctFileBuffer);

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

  private static void insertUploadFileInfo(DrUploadFileInfo fileInfo)
    throws Exception
  {
    String sql = "insert into DR_UPLOAD_FILE_INFO values ('" + fileInfo.getFileName() + "','" + fileInfo.getProv() + "','" + fileInfo.getType() + "','" + fileInfo.getIntval() + "','" + fileInfo.getFileSeq() + "','" + fileInfo.getReloadFlag() + "',to_date('" + DatetimeServices.getNowDateTimeStr(conn) + "','yyyy-MM-dd HH24:mi:ss')," + fileInfo.getTotal() + ",to_date('" + beginTime + "','yyyy-MM-dd HH24:mi:ss'),to_date('" + endTime + "','yyyy-MM-dd HH24:mi:ss'),'" + fileInfo.getUploadStatus() + "')";

    log.info(sql);
    Connection connuap = DBUtil.getAiuap20Connection();
    try {
      PreparedStatement prepStmt = connuap.prepareStatement(sql);
      prepStmt.executeUpdate();

      DBUtil.closePrepStmt(prepStmt);
    }
    catch (RuntimeException e) {
      e.printStackTrace();
      log.error(e.getMessage());
    } finally {
      try {
        if (connuap != null)
          connuap.close();
      } catch (Exception e) {
        e.printStackTrace();
        log.error(e.getMessage());
      }
    }
  }

  private static void writeMainAcctFileBufferToTempFile(StringBuffer mainAcctFileBuffer)
    throws IOException
  {
    bw.write(mainAcctFileBuffer.toString());
    bw.flush();
    output.flush();
    fos.flush();
  }

  private static Long getSubAcctCount(String sql) throws Exception
  {
    return Long.valueOf(0L);
  }

  private static String getResName(String appCode) throws Exception {
    String sql = "select t.app_name from uap_app t where t.app_code = '" + appCode + "'";
    String resName = "";

    Connection connuap = DBUtil.getAiuap20Connection();
    try {
      PreparedStatement prepStmt = connuap.prepareStatement(sql);
      ResultSet rs = prepStmt.executeQuery();
      while (rs.next()) {
        resName = rs.getString("app_name");
      }
      DBUtil.closePrepStmt(prepStmt);
    }
    catch (RuntimeException e) {
      e.printStackTrace();
      log.error(e.getMessage());
    } finally {
      try {
        if (connuap != null)
          connuap.close();
      } catch (Exception e) {
        e.printStackTrace();
        log.error(e.getMessage());
      }
    }

    return resName;
  }

  private static void getSubAcctInfo(StringBuffer mainAcctFileBuffer)
    throws Exception
  {
    File uapLoadTempFile = new File(uap_file_uapload_temp + "/" + uploadFileName);

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

    for (int i = 0; i < 48; i++) {
      mainAcctFileBuffer.append("<rcd>\r\n");
      String begin = init;
      String end = "";
      calendar.set(11, 0);
      calendar.set(12, 0);
      calendar.set(13, 0);
      calendar.set(14, 0);
      if (i == 0) {
        calendar.set(12, 30);
        a = new Timestamp(calendar.getTimeInMillis());
        end = a.toString().replaceAll("\\.0", "");
      } else {
        String[] ms = begin.split("\\ ");
        String hou = ms[1].split("\\:")[0];
        if (i % 2 == 0) {
          calendar.set(11, Integer.parseInt(hou));
          calendar.set(12, 30);
          a = new Timestamp(calendar.getTimeInMillis());
          end = a.toString().replaceAll("\\.0", "");
        }
        if (i % 2 == 1) {
          calendar.set(11, Integer.parseInt(hou) + 1);
          calendar.set(12, 0);
          a = new Timestamp(calendar.getTimeInMillis());
          end = a.toString().replaceAll("\\.0", "");
        }
      }
      init = end;

      String subAcctLoginSqlCount = "SELECT count(t.log_id) from iap_app_log t where t.operate_content like '%appcode=" + app_code + "%' and t.operate_type_id ='" + op_type_id + "' and " + " t.operate_time > to_date('" + begin + "', 'yyyy-MM-dd HH24:mi:ss') and t.operate_time <= to_date('" + end + "', 'yyyy-MM-dd HH24:mi:ss')";

      String subAcctOtherOperateSqlCount = "SELECT count(t.log_id) from iap_app_log t where t.domain_id = '" + crm_domain_id + "' and t.operate_type_id !='" + op_type_id + "' and " + " t.operate_time > to_date('" + begin + "', 'yyyy-MM-dd HH24:mi:ss') and t.operate_time <= to_date('" + end + "', 'yyyy-MM-dd HH24:mi:ss')";

      long loginNum = getSubAcctCount(subAcctLoginSqlCount).longValue();

      long notLoginNum = getSubAcctCount(subAcctOtherOperateSqlCount).longValue();

      res_name = getResName(app_code);

      mainAcctFileBuffer.append("<seq>" + String.valueOf(i + 1) + "</seq>" + "\r\n");

      mainAcctFileBuffer.append("<resname>" + res_name + "</resname>" + "\r\n");

      mainAcctFileBuffer.append("<restype>" + res_type + "</restype>" + "\r\n");

      mainAcctFileBuffer.append("<num>" + (i + 1) + "</num>" + "\r\n");

      mainAcctFileBuffer.append("<dlvalue>" + loginNum + "</dlvalue>" + "\r\n");

      mainAcctFileBuffer.append("<czvalue>" + notLoginNum + "</czvalue>" + "\r\n");

      mainAcctFileBuffer.append("</rcd>\r\n");

      log.info("num=" + (i + 1) + "subAcctLoginSqlCount=====" + subAcctLoginSqlCount);
      log.info("num=" + (i + 1) + "subAcctOtherOperateSqlCount=====" + subAcctOtherOperateSqlCount);
    }
  }
}