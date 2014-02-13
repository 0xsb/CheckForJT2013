package com.ailk.jt.mannul;

import com.ailk.jt.util.DBUtil;
import com.ailk.jt.util.PropertiesUtil;
import com.ailk.jt.util.SQLUtil;
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
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.fileupload.util.Streams;
import org.apache.log4j.Logger;

public class WebExceptionBusinessDayAddFile_local
{
  private static final Logger log = Logger.getLogger(WebExceptionBusinessDayAddFile_local.class);
  static int i;
  static int j;
  private static final String[] proviceParam = { "100", "311", "731", "200", "351", "771", "210", "371", "791", "220", "431", "851", "230", "451", "871", "240", "471", "891", "250", "531", "898", "270", "551", "931", "280", "571", "951", "290", "591", "971", "991" };
  private static String uap_file_uapload;
  private static String uap_file_uapload_temp;
  private static final String type = "SMBHR";
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
  private static String nopaymentOperTypeId;
  private static String paymentOperTypeId;
  private static String bossname;
  private static String bossCode;
  private static File uapLoadTempFile;

  public static void readConfig()
  {
    uap_file_uapload = PropertiesUtil.getValue("uap_file_uapload").trim();
    prov_code = PropertiesUtil.getValue("prov_code").trim();
    uap_file_uapload_temp = PropertiesUtil.getValue("uap_file_uapload_temp").trim();

    nopaymentOperTypeId = PropertiesUtil.getValue("search_nopayment_oper_type_id").trim();
    paymentOperTypeId = PropertiesUtil.getValue("search_payment_oper_type_id").trim();
    bossCode = PropertiesUtil.getValue("boss_appcode").trim();
  }

  public static void main(String[] args)
  {
    readConfig();
    log.info("WebExceptionBusinessDayAddFile start to run......");
    log.info("uap_file_uapload==" + uap_file_uapload);
    long statisticRunStartTime = System.currentTimeMillis();
    try {
      conn = DBUtil.getAiuap20Connection();
      createTime = DatetimeServices.getNowDateTimeStrWithT(conn);
      beginTime = DatetimeServices.getLastDayStartTimeStr(conn);
      beginTimeWithT = DatetimeServices.getLastDayStartTimeStrWithT(conn);
      endTime = DatetimeServices.getTodayStartTimeStr(conn);
      endTimeWithT = DatetimeServices.getTodayStartTimeStrWithT(conn);

      uploadFileName = "SMBHR.xml";

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

      log.info("generateWebExceptionBusinessDayAddFile  ******Start***************");

      generateWebExceptionBusinessDayAddFile();
      System.out.println("-------------------------------");

      log.info("generateWebExceptionBusinessDayAddFile  ******End ***************");

      log.info("DR_UPLOAD_FILE_INFO**********insert ********Start*********");

      DrUploadFileInfo fileInfo = new DrUploadFileInfo();
      fileInfo.setFileName(uploadFileName);
      fileInfo.setFileSeq("000");
      fileInfo.setIntval("01DY");
      fileInfo.setProv(prov_code);
      fileInfo.setReloadFlag(reloadFlag);
      fileInfo.setTotal(Long.valueOf(sum));
      fileInfo.setType("SMBHR");
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
        if (uapLoadTempFile.exists())
          uapLoadTempFile.delete();
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }
  }

  private static void generateWebExceptionBusinessDayAddFile() throws Exception, IOException, FileNotFoundException, UnsupportedEncodingException
  {
    StringBuffer webExceptionBusinessDayAddFileBuffer = new StringBuffer();
    webExceptionBusinessDayAddFileBuffer.append("<?xml version='1.0' encoding='UTF-8'?>\r\n");
    webExceptionBusinessDayAddFileBuffer.append("<bomc>\r\n");
    webExceptionBusinessDayAddFileBuffer.append("<type>SMBHR</type>\r\n");
    webExceptionBusinessDayAddFileBuffer.append("<province>" + prov_code + "</province>" + "\r\n");
    webExceptionBusinessDayAddFileBuffer.append("<createtime>" + createTime + "</createtime>" + "\r\n");

    String totalSql = "";
    log.info("webExceptionBussinessCountSql===" + totalSql);

    sum = 93L;
    log.info("total sum ===" + sum);
    webExceptionBusinessDayAddFileBuffer.append("<sum>" + sum + "</sum>" + "\r\n");
    webExceptionBusinessDayAddFileBuffer.append("<begintime>" + beginTimeWithT + "</begintime>" + "\r\n");
    webExceptionBusinessDayAddFileBuffer.append("<endtime>" + endTimeWithT + "</endtime>" + "\r\n");
    webExceptionBusinessDayAddFileBuffer.append("<data>");

    log.info("getWebExceptionBusinessInfo*******************Start******");
    Map proviceName = getProviceName();
    getWebExceptionBusinessInfo(nopaymentOperTypeId, paymentOperTypeId, webExceptionBusinessDayAddFileBuffer, proviceName);

    log.info("getWebExceptionBusinessInfo*******************End*********");

    webExceptionBusinessDayAddFileBuffer.append("</data>\r\n");
    webExceptionBusinessDayAddFileBuffer.append("</bomc>");

    log.info("webExceptionBusinessDayAddFileBuffer====" + webExceptionBusinessDayAddFileBuffer.toString());

    uapLoadTempFile = new File(uap_file_uapload_temp + "/" + uploadFileName);

    writeWebExceptionBusinessDayAddFileBufferToTempFile(webExceptionBusinessDayAddFileBuffer);

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

  private static void writeWebExceptionBusinessDayAddFileBufferToTempFile(StringBuffer mainAcctDayAddFileBuffer) throws IOException
  {
    bw.write(mainAcctDayAddFileBuffer.toString());
    bw.flush();
    output.flush();
    fos.flush();
  }

  private static void getWebExceptionBusinessInfo(String operTypeId, String operTypeBusinessId, StringBuffer webExceptionBusinessDayAddFileBuffer, Map<String, String> proviceName)
    throws Exception
  {
    int behaviour = 101;

    uapLoadTempFile = new File(uap_file_uapload_temp + "/" + uploadFileName);
    if (uapLoadTempFile.exists()) {
      uapLoadTempFile.delete();
      uapLoadTempFile.createNewFile();
    }
    fos = new FileOutputStream(uapLoadTempFile, true);
    output = new OutputStreamWriter(fos, "UTF-8");
    bw = new BufferedWriter(output);

    for (int x = 0; x < 3; x++) {
      int h = 0;
      for (int k = 1; k < proviceParam.length + 1; k++) {
        bossname = getResName(bossCode);

        webExceptionBusinessDayAddFileBuffer.append("<rcd>\r\n");
        int z;
        if (x == 0)
          z = k + x * 30;
        else
          z = k + x * 30 + 1;
        if (x == 2) {
          z = k + x * 30 + 2;
          for (int m = 0; m < 31; m++) {
            if (m != 0)
              webExceptionBusinessDayAddFileBuffer.append("<rcd>\r\n");
            webExceptionBusinessDayAddFileBuffer.append("<seq>" + (z + m) + "</seq>" + "\r\n");

            webExceptionBusinessDayAddFileBuffer.append("<resname>" + bossname + "</resname>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("<behaviour>" + (behaviour + x) + "</behaviour>" + "\r\n");

            webExceptionBusinessDayAddFileBuffer.append("<companyname>" + (String)proviceName.get(proviceParam[m]) + "</companyname>" + "\r\n");

            webExceptionBusinessDayAddFileBuffer.append("<total>0</total>\r\n");
            webExceptionBusinessDayAddFileBuffer.append("<buztotal>0</buztotal>\r\n");
            webExceptionBusinessDayAddFileBuffer.append("</rcd>\r\n");
          }
        }

        webExceptionBusinessDayAddFileBuffer.append("<seq>" + z + "</seq>" + "\r\n");

        webExceptionBusinessDayAddFileBuffer.append("<resname>" + bossname + "</resname>" + "\r\n");
        webExceptionBusinessDayAddFileBuffer.append("<behaviour>" + (behaviour + x) + "</behaviour>" + "\r\n");
        webExceptionBusinessDayAddFileBuffer.append("<companyname>" + (String)proviceName.get(proviceParam[(k - 1)]) + "</companyname>" + "\r\n");

        String thing = ((String)proviceName.get(proviceParam[(k - 1)])).substring(0, 2);
        String sqlName = PropertiesUtil.getValue("WEBEXCEPTIONBUSINESS_cx");
        String sqlData_temp = SQLUtil.getSql(sqlName);
        HashMap parameterMap = new HashMap();
        parameterMap.put("thing", thing);
        String sqlData = SQLUtil.replaceParameter(sqlData_temp, parameterMap, false);

        log.debug("" + sqlData);

        PreparedStatement prepStmt = conn.prepareStatement(sqlData);
        ResultSet rs = prepStmt.executeQuery();
        while (rs.next()) {
          h = rs.getInt(1);

          switch (k) {
          case 1:
            i = h;
            j = h;
            webExceptionBusinessDayAddFileBuffer.append("<total>" + i + "</total>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("<buztotal>" + j + "</buztotal>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("</rcd>\r\n");
            break;
          case 2:
            i = h;
            j = h;
            webExceptionBusinessDayAddFileBuffer.append("<total>" + i + "</total>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("<buztotal>" + j + "</buztotal>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("</rcd>\r\n");
            break;
          case 3:
            i = h;
            j = h;
            webExceptionBusinessDayAddFileBuffer.append("<total>" + i + "</total>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("<buztotal>" + j + "</buztotal>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("</rcd>\r\n");
            break;
          case 4:
            i = h;
            j = h;
            webExceptionBusinessDayAddFileBuffer.append("<total>" + i + "</total>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("<buztotal>" + j + "</buztotal>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("</rcd>\r\n");
            break;
          case 5:
            i = h;
            j = h;
            webExceptionBusinessDayAddFileBuffer.append("<total>" + i + "</total>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("<buztotal>" + j + "</buztotal>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("</rcd>\r\n");
            break;
          case 6:
            i = h;
            j = h;
            webExceptionBusinessDayAddFileBuffer.append("<total>" + i + "</total>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("<buztotal>" + j + "</buztotal>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("</rcd>\r\n");
            break;
          case 7:
            i = h;
            j = h;
            webExceptionBusinessDayAddFileBuffer.append("<total>" + i + "</total>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("<buztotal>" + j + "</buztotal>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("</rcd>\r\n");
            break;
          case 8:
            i = h;
            j = h;
            webExceptionBusinessDayAddFileBuffer.append("<total>" + i + "</total>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("<buztotal>" + j + "</buztotal>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("</rcd>\r\n");
            break;
          case 9:
            i = h;
            j = h;
            webExceptionBusinessDayAddFileBuffer.append("<total>" + i + "</total>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("<buztotal>" + j + "</buztotal>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("</rcd>\r\n");
            break;
          case 10:
            i = h;
            j = h;
            webExceptionBusinessDayAddFileBuffer.append("<total>" + i + "</total>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("<buztotal>" + j + "</buztotal>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("</rcd>\r\n");
            break;
          case 11:
            i = h;
            j = h;
            webExceptionBusinessDayAddFileBuffer.append("<total>" + i + "</total>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("<buztotal>" + j + "</buztotal>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("</rcd>\r\n");
            break;
          case 12:
            i = h;
            j = h;
            webExceptionBusinessDayAddFileBuffer.append("<total>" + i + "</total>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("<buztotal>" + j + "</buztotal>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("</rcd>\r\n");
            break;
          case 13:
            i = h;
            j = h;
            webExceptionBusinessDayAddFileBuffer.append("<total>" + i + "</total>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("<buztotal>" + j + "</buztotal>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("</rcd>\r\n");
            break;
          case 14:
            i = h;
            j = h;
            webExceptionBusinessDayAddFileBuffer.append("<total>" + i + "</total>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("<buztotal>" + j + "</buztotal>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("</rcd>\r\n");
            break;
          case 15:
            i = h;
            j = h;
            webExceptionBusinessDayAddFileBuffer.append("<total>" + i + "</total>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("<buztotal>" + j + "</buztotal>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("</rcd>\r\n");
            break;
          case 16:
            i = h;
            j = h;
            webExceptionBusinessDayAddFileBuffer.append("<total>" + i + "</total>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("<buztotal>" + j + "</buztotal>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("</rcd>\r\n");
            break;
          case 17:
            i = h;
            j = h;
            webExceptionBusinessDayAddFileBuffer.append("<total>" + i + "</total>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("<buztotal>" + j + "</buztotal>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("</rcd>\r\n");
            break;
          case 18:
            i = h;
            j = h;
            webExceptionBusinessDayAddFileBuffer.append("<total>" + i + "</total>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("<buztotal>" + j + "</buztotal>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("</rcd>\r\n");
            break;
          case 19:
            i = h;
            j = h;
            webExceptionBusinessDayAddFileBuffer.append("<total>" + i + "</total>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("<buztotal>" + j + "</buztotal>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("</rcd>\r\n");
            break;
          case 20:
            i = h;
            j = h;
            webExceptionBusinessDayAddFileBuffer.append("<total>" + i + "</total>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("<buztotal>" + j + "</buztotal>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("</rcd>\r\n");
            break;
          case 21:
            i = h;
            j = h;
            webExceptionBusinessDayAddFileBuffer.append("<total>" + i + "</total>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("<buztotal>" + j + "</buztotal>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("</rcd>\r\n");
            break;
          case 22:
            i = h;
            j = h;
            webExceptionBusinessDayAddFileBuffer.append("<total>" + i + "</total>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("<buztotal>" + j + "</buztotal>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("</rcd>\r\n");
            break;
          case 23:
            i = h;
            j = h;
            webExceptionBusinessDayAddFileBuffer.append("<total>" + i + "</total>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("<buztotal>" + j + "</buztotal>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("</rcd>\r\n");
            break;
          case 24:
            i = h;
            j = h;
            webExceptionBusinessDayAddFileBuffer.append("<total>" + i + "</total>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("<buztotal>" + j + "</buztotal>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("</rcd>\r\n");
            break;
          case 25:
            i = h;
            j = h;
            webExceptionBusinessDayAddFileBuffer.append("<total>" + i + "</total>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("<buztotal>" + j + "</buztotal>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("</rcd>\r\n");
            break;
          case 26:
            i = h;
            j = h;
            webExceptionBusinessDayAddFileBuffer.append("<total>" + i + "</total>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("<buztotal>" + j + "</buztotal>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("</rcd>\r\n");
            break;
          case 27:
            i = h;
            j = h;
            webExceptionBusinessDayAddFileBuffer.append("<total>" + i + "</total>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("<buztotal>" + j + "</buztotal>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("</rcd>\r\n");
            break;
          case 28:
            i = h;
            j = h;
            webExceptionBusinessDayAddFileBuffer.append("<total>" + i + "</total>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("<buztotal>" + j + "</buztotal>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("</rcd>\r\n");
            break;
          case 29:
            i = h;
            j = h;
            webExceptionBusinessDayAddFileBuffer.append("<total>" + i + "</total>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("<buztotal>" + j + "</buztotal>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("</rcd>\r\n");
            break;
          case 30:
            i = h;
            j = h;
            webExceptionBusinessDayAddFileBuffer.append("<total>" + i + "</total>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("<buztotal>" + j + "</buztotal>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("</rcd>\r\n");
            break;
          case 31:
            i = h;
            j = h;
            webExceptionBusinessDayAddFileBuffer.append("<total>" + i + "</total>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("<buztotal>" + j + "</buztotal>" + "\r\n");
            webExceptionBusinessDayAddFileBuffer.append("</rcd>\r\n");
          }
        }
      }
    }
  }

  private static void insertUploadFileInfo(DrUploadFileInfo fileInfo) throws Exception
  {
    String sql = "insert into DR_UPLOAD_FILE_INFO values('" + fileInfo.getFileName() + "','" + fileInfo.getProv() + "','" + fileInfo.getType() + "','" + fileInfo.getIntval() + "','" + fileInfo.getFileSeq() + "','" + fileInfo.getReloadFlag() + "',to_date('" + DatetimeServices.getNowDateTimeStr(conn) + "','yyyy-MM-dd HH24:mi:ss')," + fileInfo.getTotal() + ",to_date('" + beginTime + "','yyyy-MM-dd HH24:mi:ss'),to_date('" + endTime + "','yyyy-MM-dd HH24:mi:ss'),'" + fileInfo.getUploadStatus() + "')";

    log.info(sql);
    Connection connuap = DBUtil.getAiuap20Connection();
    try {
      PreparedStatement prepStmt = connuap.prepareStatement(sql);
      prepStmt.executeUpdate();
      ConnectionManager.closePrepStmt(prepStmt);
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

  private static Long getSearchOrPaymentNum(String sql) throws Exception
  {
    PreparedStatement prepStmt = conn.prepareStatement(sql);
    ResultSet rs = prepStmt.executeQuery();
    Long count = Long.valueOf(0L);
    while (rs.next()) {
      count = Long.valueOf(rs.getLong(1));
    }
    ConnectionManager.closeResultSet(rs);
    ConnectionManager.closePrepStmt(prepStmt);

    return count;
  }

  private static List getProviceNameAndSearchNum(String sql) throws Exception {
    PreparedStatement prepStmt = conn.prepareStatement(sql);
    ResultSet rs = prepStmt.executeQuery();
    List result = new ArrayList();
    while (rs.next()) {
      String proviceName = rs.getString("provincename");
      Long num = Long.valueOf(rs.getLong("numc"));
      result.add(proviceName);
      result.add(num);
    }
    ConnectionManager.closeResultSet(rs);
    ConnectionManager.closePrepStmt(prepStmt);

    return result;
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
      ConnectionManager.closePrepStmt(prepStmt);
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

  private static Map<String, String> getProviceName()
    throws Exception
  {
    Map proviceName = new HashMap();
    proviceName.put("100", "北京移动");
    proviceName.put("311", "河北移动");
    proviceName.put("731", "湖南移动");
    proviceName.put("200", "广东移动");
    proviceName.put("351", "山西移动");
    proviceName.put("771", "广西移动");
    proviceName.put("210", "上海移动");
    proviceName.put("371", "河南移动");
    proviceName.put("791", "江西移动");
    proviceName.put("220", "天津移动");
    proviceName.put("431", "吉林移动");
    proviceName.put("851", "贵州移动");
    proviceName.put("230", "重庆移动");
    proviceName.put("451", "黑龙江移动");
    proviceName.put("871", "云南移动");
    proviceName.put("240", "辽宁移动");
    proviceName.put("471", "内蒙移动");
    proviceName.put("891", "西藏移动");
    proviceName.put("250", "江苏移动");
    proviceName.put("531", "山东移动");
    proviceName.put("898", "海南移动");
    proviceName.put("270", "湖北移动");
    proviceName.put("551", "安徽移动");
    proviceName.put("931", "甘肃移动");
    proviceName.put("280", "四川移动");
    proviceName.put("571", "浙江移动");
    proviceName.put("951", "宁夏移动");
    proviceName.put("290", "陕西移动");
    proviceName.put("591", "福建移动");
    proviceName.put("971", "青海移动");
    proviceName.put("991", "新疆移动");

    return proviceName;
  }
}