package com.ailk.jt.mannul;

import com.ailk.jt.util.DBUtil;
import com.ailk.jt.util.PropertiesUtil;
import com.ailk.uap.dbconn.ConnectionManager;
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
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.commons.fileupload.util.Streams;
import org.apache.log4j.Logger;

public class MainAcctDayAddFile
{
  private static final Logger log = Logger.getLogger(MainAcctDayAddFile.class);
  private static String uap_file_uapload;
  private static String uap_file_uapload_temp;
  private static String uap_version;
  private static final String type = "SMMAI";
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

    if (uap_version != null)
      try {
        conn = DBUtil.getAiuap20Connection();
        createTime = DatetimeServices.getNowDateTimeStrWithT(conn);
        beginTime = DatetimeServices.getLastDayStartTimeStr(conn);
        beginTimeWithT = DatetimeServices.getLastDayStartTimeStrWithT(conn);
        endTime = DatetimeServices.getTodayStartTimeStr(conn);
        endTimeWithT = DatetimeServices.getTodayStartTimeStrWithT(conn);

        uploadFileName = "SMMAI.xml";

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

        log.info("generateMainAcctFullFile  ******Start***************");

        generateMainAcctDayAddFile();

        log.info("generateMainAcctFullFile  ******End ***************");

        log.info("DR_UPLOAD_FILE_INFO**********insert ********Start*********");

        DrUploadFileInfo fileInfo = new DrUploadFileInfo();
        fileInfo.setFileName(uploadFileName);
        fileInfo.setFileSeq("000");
        fileInfo.setIntval("01DY");
        fileInfo.setProv(prov_code);
        fileInfo.setReloadFlag(reloadFlag);
        fileInfo.setTotal(Long.valueOf(sum));
        fileInfo.setType("SMMAI");
        fileInfo.setUploadStatus("0");

        insertUploadFileInfo(fileInfo);
        long statisticRunEndTime = System.currentTimeMillis();
        log.info("GENERATE MAINACCT Day Add FILE  TOTALTIME======" + (statisticRunEndTime - statisticRunStartTime) / 1000L + "s");
      }
      catch (Exception e)
      {
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
          if (uapLoadTempFile.exists()) {
            uapLoadTempFile.delete();
          }
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
  }

  private static void generateMainAcctDayAddFile()
    throws Exception, IOException, FileNotFoundException, UnsupportedEncodingException
  {
    StringBuffer mainAcctDayAddFileBuffer = new StringBuffer();
    mainAcctDayAddFileBuffer.append("<?xml version='1.0' encoding='UTF-8'?>\r\n");
    mainAcctDayAddFileBuffer.append("<bomc>\r\n");
    mainAcctDayAddFileBuffer.append("<type>SMMAI</type>\r\n");
    mainAcctDayAddFileBuffer.append("<province>" + prov_code + "</province>" + "\r\n");
    mainAcctDayAddFileBuffer.append("<createtime>" + createTime + "</createtime>" + "\r\n");

    String mainAcctDayAddFileSql = "";
    String mainAcctDayAddFileCountSql = "";

    mainAcctDayAddFileCountSql = "";

    mainAcctDayAddFileSql = "";

    log.info("mainAcctDayAddFileCountSql===" + mainAcctDayAddFileCountSql);
    log.info("mainAcctDayAddFileSql===" + mainAcctDayAddFileSql);

    sum = 0L;
    log.info("total sum ===" + sum);
    mainAcctDayAddFileBuffer.append("<sum>" + sum + "</sum>" + "\r\n");
    mainAcctDayAddFileBuffer.append("<begintime>" + beginTimeWithT + "</begintime>" + "\r\n");
    mainAcctDayAddFileBuffer.append("<endtime>" + endTimeWithT + "</endtime>" + "\r\n");
    mainAcctDayAddFileBuffer.append("<data>");
    mainAcctDayAddFileBuffer.append("</data>\r\n");
    mainAcctDayAddFileBuffer.append("</bomc>");

    log.info("mainAcctDayAddFileBuffer====" + mainAcctDayAddFileBuffer.toString());

    uapLoadTempFile = new File(uap_file_uapload_temp + "/" + uploadFileName);

    writemainAcctDayAddFileBufferToTempFile(mainAcctDayAddFileBuffer);

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

  private static void writemainAcctDayAddFileBufferToTempFile(StringBuffer mainAcctDayAddFileBuffer) throws IOException
  {
    bw.write(mainAcctDayAddFileBuffer.toString());
    bw.flush();
    output.flush();
    fos.flush();
  }

  private static Long getMainAcctFullCount(String sql) throws Exception
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

  private static String getMainAcctRoleListInfo(Long mainAcctId) throws Exception
  {
    log.info("getMainAcctRoleListInfo**********Start*********");
    StringBuffer roleListBuffer = new StringBuffer();
    String roleList = "";
    String sql = "select t3.role_name from uap_main_acct_role t1, uap_main_acct t2, uap_role t3 where t1.main_acct_id = t2.main_acct_id and t1.role_id = t3.role_id and t1.main_acct_id = " + mainAcctId;

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

  private static String getMainAcctOrgPath(Long orgId) throws Exception
  {
    StringBuffer orgPathBuffer = new StringBuffer();
    String orgIdColumn = "org_id";
    String org_nameColumn = "org_name";

    String orgPath = "";
    String sql = "select " + org_nameColumn + " from uap_organization   start with " + orgIdColumn + " =  " + orgId + " connect by prior  parent_id =  " + orgIdColumn + " order by org_id ";

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

  private static void insertUploadFileInfo(DrUploadFileInfo fileInfo) throws Exception
  {
    String sql = "insert into DR_UPLOAD_FILE_INFO values('" + fileInfo.getFileName() + "','" + fileInfo.getProv() + "','" + fileInfo.getType() + "','" + fileInfo.getIntval() + "','" + fileInfo.getFileSeq() + "','" + fileInfo.getReloadFlag() + "',to_date('" + DatetimeServices.getNowDateTimeStr(conn) + "','yyyy-MM-dd HH24:mi:ss')," + fileInfo.getTotal() + ",to_date('" + beginTime + "','yyyy-MM-dd HH24:mi:ss'),to_date('" + endTime + "','yyyy-MM-dd HH24:mi:ss'),'" + fileInfo.getUploadStatus() + "')";

    log.info(sql);

    PreparedStatement prepStmt = conn.prepareStatement(sql);
    prepStmt.executeUpdate();

    ConnectionManager.closePrepStmt(prepStmt);
  }
}