package com.ailk.uap.makefile4new.manual;
/*
此程序主要根据配置文件中设置的时间信息来生成对应时间的文件
type=SMDAR
 */

import com.ailk.jt.util.DBUtil;
import com.ailk.jt.util.DateUtil;
import com.ailk.jt.util.SQLUtil;
import com.ailk.uap.config.PropertiesUtil;
import com.ailk.uap.dbconn.ConnectionManager;
import com.ailk.uap.entity.DrUploadFileInfo;
import com.ailk.uap.util.DatetimeServices;
import org.apache.commons.fileupload.util.Streams;
import org.apache.log4j.Logger;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class DbOperateLogDayFileManual {
    private static final Logger log = Logger.getLogger(DbOperateLogDayFileManual.class);
    private static String uap_file_uapload;
    private static String uap_file_uapload_temp;
    private static final String type = "SMDAR";
    private static String prov_code;
    private static final String intval = "01DY";
    private static String createTime;
    private static String beginTime;
    private static String endTime;
    private static String fileName;
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

    public static void readConfig() {
        uap_file_uapload = PropertiesUtil.getValue("uap_file_uapload_for_smdar_db_now").trim();
        prov_code = PropertiesUtil.getValue("prov_code").trim();
        uap_file_uapload_temp = PropertiesUtil.getValue("uap_file_uapload_temp").trim();
    }

    public static void main(String[] args) {
        readConfig();
        log.info("DbOperateLogDayFileManual start to run......");
        log.info("uap_file_uapload==" + uap_file_uapload);
        long statisticRunStartTime = System.currentTimeMillis();
        System.out.println("DbOperateLogDayFileManual...");
        try {
            conn = DBUtil.getAuditConnection();
            //从配置文件中读取时间信息
            createTime = PropertiesUtil.getValue("create_time");
            beginTime = PropertiesUtil.getValue("begin_time");
            endTime = PropertiesUtil.getValue("end_time");
            fileName = PropertiesUtil.getValue("file_name");
            beginTimeWithT = beginTime.replace(" ", "T");
            endTimeWithT = endTime.replace(" ", "T");

            fileSeq = DatetimeServices.getHourFileSeq(conn);
            uploadFileName = "SMDAR_" + prov_code + "_" + "01DY" + "_" + fileName + "_"
                    + "000" + "_" + "000.xml";
            if (args.length != 0) {
                uploadFileName = args[0];

                beginTime = args[1];

                endTime = args[2];

                int fileSeqIndex = uploadFileName.lastIndexOf("_");
                fileSeq = uploadFileName.substring(fileSeqIndex - 3, fileSeqIndex);

                int reloadFlagIndex = uploadFileName.lastIndexOf(".");
                if (!uploadFileName.substring(reloadFlagIndex - 3, reloadFlagIndex).equals("000")) {
                    reloadFlag = "1";
                }

            }

            updateDateSource();

            log.info("generateDbOperateLogDayFile  ******Start***************");

            generateDbOperateLogDayFile();

            log.info("generateDbOperateLogDayFile  ******End ***************");

            log.info("DR_UPLOAD_FILE_INFO**********insert ********Start*********");

            DrUploadFileInfo fileInfo = new DrUploadFileInfo();
            fileInfo.setFileName(uploadFileName);
            fileInfo.setFileSeq(fileSeq);
            fileInfo.setReloadFlag(reloadFlag);
            fileInfo.setIntval("01DY");
            fileInfo.setProv(prov_code);
            fileInfo.setTotal(Long.valueOf(sum));
            fileInfo.setType("SMDAR");
            fileInfo.setUploadStatus("0");

//            insertUploadFileInfo(fileInfo);
            long statisticRunEndTime = System.currentTimeMillis();
            log.info("GENERATE DbOperateLogDayFileManual FULL FILE  TOTALTIME======"
                    + (statisticRunEndTime - statisticRunStartTime) / 1000L + "s");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        } finally {
            ConnectionManager.closeConnection(conn);
            try {
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
            } catch (IOException e) {
                e.printStackTrace();
            }
            log.info("-------------DbOperateLogDayFileManual end-----------------");
        }
    }

    private static void updateDateSource() {
        Connection conn = DBUtil.getAuditConnection();

        String truncate_Sql = SQLUtil.getSql("truncate_table_a4_iap_device_session");
        DBUtil.executeSQL(conn, truncate_Sql);
        log.info("truncate_Sql=======" + truncate_Sql);

        String db_day_file = SQLUtil.getSql("db_day_file");
        HashMap<String, String> parameterMap = new HashMap<String, String>();

        Calendar cal = Calendar.getInstance();
        cal.add(5, -1);
        Date date = cal.getTime();
        //正常情况数据库分区采用当前时间月，手动生成文件时所采用的分区要为文件开始时间月
        String dataBasePart = beginTime.substring(0,7).replace("-","");
         System.out.println("dateBasePart=="+dataBasePart);
//        String dataBasePart = DateUtil.formatDateyyyyMM(date);
//		String dataBasePart = "201205";
        parameterMap.put("dataBasePart", "PART_DEVICE_SESSION_" + dataBasePart);
        parameterMap.put("beginTime", beginTime);
        parameterMap.put("endTime", endTime);
        String mainAcctDayAddFileCountSql = SQLUtil.replaceParameter(db_day_file, parameterMap, true);
        log.info("mainAcctDayAddFileCountSql=======" + mainAcctDayAddFileCountSql);
        DBUtil.executeSQL(conn, mainAcctDayAddFileCountSql);

        String truncateTestDeviceSession = SQLUtil.getSql("truncate_test_device_session");
        DBUtil.executeSQL(conn, truncateTestDeviceSession);
        log.info("truncate_test_device_session======" + truncateTestDeviceSession);

        String sepreate_device = SQLUtil.getSql("sepreate_device");
        DBUtil.executeSQL(conn, sepreate_device);
        log.info("sepreate_device=======" + sepreate_device);

        String truncate_second_final_device_session = SQLUtil.getSql("truncate_second_final_device_session");
        DBUtil.executeSQL(conn, truncate_second_final_device_session);
        log.info("truncate_second_final_device_session=======" + truncate_second_final_device_session);
        String second_final_device_session = SQLUtil.getSql("second_final_device_session");
        DBUtil.executeSQL(conn, second_final_device_session);
        log.info("second_final_device_session=======" + second_final_device_session);

        String final_device = SQLUtil.getSql("final_device");
        DBUtil.executeSQL(conn, final_device);
        log.info("final_device=======" + final_device);
    }

    private static void generateDbOperateLogDayFile() throws Exception, IOException, FileNotFoundException,
            UnsupportedEncodingException {
        StringBuffer subAcctFileBuffer = new StringBuffer();
        subAcctFileBuffer.append("<?xml version='1.0' encoding='UTF-8'?>\r\n");
        subAcctFileBuffer.append("<smp>\r\n");
        subAcctFileBuffer.append("<type>SMDAR</type>\r\n");
        subAcctFileBuffer.append("<province>" + prov_code + "</province>" + "\r\n");
        subAcctFileBuffer.append("<createtime>" + createTime.replace(" ", "T") + "</createtime>" + "\r\n");

        String subAcctDeviceIds = "";

        subAcctDeviceIds = SQLUtil.getSql("count_sys_device");
        log.info("subAcctDeviceIdsSql===" + subAcctDeviceIds);

        int belongsSya = getDeviceBelongsSys(subAcctDeviceIds);
        if (belongsSya != 0)
            sum = belongsSya * 48;
        else {
            sum = 0L;
        }
        log.info("total sum ===432");
        subAcctFileBuffer.append("<sum>" + sum + "</sum>" + "\r\n");
        subAcctFileBuffer.append("<begintime>" + beginTime.replace(" ", "T") + "</begintime>" + "\r\n");
        subAcctFileBuffer.append("<endtime>" + endTime.replace(" ", "T") + "</endtime>" + "\r\n");
        subAcctFileBuffer.append("<data>\r\n");

        log.info("getDbOperateLogInfo*******************Start*********");
        if (sum != 0L) {
            getSubAcctInfo(subAcctFileBuffer);
        }
        log.info("getDbOperateLogInfo*******************End*********");

        subAcctFileBuffer.append("</data>\r\n");
        subAcctFileBuffer.append("</smp>");

        log.info("subAcctFileBuffer====" + subAcctFileBuffer.toString());

        uapLoadTempFile = new File(uap_file_uapload_temp + "/" + uploadFileName);

        File uapLoadTempFile = new File(uap_file_uapload_temp + "/" + uploadFileName);
        if (uapLoadTempFile.exists()) {
            uapLoadTempFile.delete();
            uapLoadTempFile.createNewFile();
        }
        fos = new FileOutputStream(uapLoadTempFile, true);
        output = new OutputStreamWriter(fos, "UTF-8");
        bw = new BufferedWriter(output);
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

    private static void insertUploadFileInfo(DrUploadFileInfo fileInfo) throws Exception {
        String sql = "insert into DR_UPLOAD_FILE_INFO values ('" + fileInfo.getFileName() + "','" + fileInfo.getProv()
                + "','" + fileInfo.getType() + "','" + fileInfo.getIntval() + "','" + fileInfo.getFileSeq() + "','"
                + fileInfo.getReloadFlag() + "',to_date('" + DatetimeServices.getNowDateTimeStr(conn)
                + "','yyyy-MM-dd HH24:mi:ss')," + fileInfo.getTotal() + ",to_date('" + beginTime
                + "','yyyy-MM-dd HH24:mi:ss'),to_date('" + endTime + "','yyyy-MM-dd HH24:mi:ss'),'"
                + fileInfo.getUploadStatus() + "')";
        log.info(sql);
        Connection connuap = DBUtil.getAiuap20Connection();
        try {
            PreparedStatement prepStmt = connuap.prepareStatement(sql);
            prepStmt.executeUpdate();
            ConnectionManager.closePrepStmt(prepStmt);
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            try {
                if (connuap != null)
                    connuap.close();
            } catch (Exception e1) {
                e1.printStackTrace();
                log.error(e1.getMessage());
            }
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

    private static void writeMainAcctFileBufferToTempFile(StringBuffer mainAcctFileBuffer) throws IOException {
        bw.write(mainAcctFileBuffer.toString());
        bw.flush();
        output.flush();
        fos.flush();
    }

    private static int getDeviceBelongsSys(String sql) throws Exception {
        PreparedStatement prepStmt = conn.prepareStatement(sql);
        ResultSet rs = prepStmt.executeQuery();
        int dbIds = 0;
        while (rs.next()) {
            dbIds = rs.getInt("total_sys_device");
        }
        ConnectionManager.closeResultSet(rs);
        ConnectionManager.closePrepStmt(prepStmt);

        return dbIds;
    }

    private static void getSubAcctInfo(StringBuffer mainAcctFileBuffer) throws Exception {
        String truncate_Sql = SQLUtil.getSql("g_file");
        ResultSet rs = DBUtil.getQueryResultSet(conn, truncate_Sql);
        int i = 1;
        while (rs.next()) {
            mainAcctFileBuffer.append("<rcd>\r\n");
            mainAcctFileBuffer.append("<seq>" + i + "</seq>" + "\r\n");
            mainAcctFileBuffer.append("<restype>" + rs.getString("belong_sys") + "</restype>" + "\r\n");
            mainAcctFileBuffer.append("<num>" + rs.getString("login_hour") + "</num>" + "\r\n");
            mainAcctFileBuffer.append("<value>" + rs.getString("total") + "</value>" + "\r\n");
            mainAcctFileBuffer.append("</rcd>\r\n");
            i++;
        }
    }
}