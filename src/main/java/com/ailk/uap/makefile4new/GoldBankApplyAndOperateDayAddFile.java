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
import java.util.Properties;

import org.apache.commons.fileupload.util.Streams;
import org.apache.log4j.Logger;

import com.ailk.jt.util.DBUtil;
import com.ailk.uap.config.PropertiesUtil;
import com.ailk.uap.dbconn.ConnectionManager;
import com.ailk.uap.entity.DrUploadFileInfo;
import com.ailk.uap.makefile4new.sql.GoldSceneOperateDayAddFileSQL;
import com.ailk.uap.util.CommonUtil;
import com.ailk.uap.util.DatetimeServices;

/**
 * 金库申请和操作日增量文件 type=SMJKR，SMJKA iap_app_log_gold生产环境在auditb库中，iap_app_gold_info
 * 生产B库,iap_app生产AB库一样
 * 
 * @author jinlin
 * 
 */
public class GoldBankApplyAndOperateDayAddFile implements
	GoldSceneOperateDayAddFileSQL {
    private static final Logger log = Logger
	    .getLogger(GoldBankApplyAndOperateDayAddFile.class);
    private static Properties tran = com.ailk.jt.util.PropertiesUtil.getProperties("/tran.properties");
    private static String uap_file_uapload_smjkr;
    private static String uap_file_uapload_smjka;
    private static String uap_file_uapload_temp;
    private static String uap_version;
    private static String prov_code;
    private static String createTime;
    private static String beginTime;
    private static String beginTimeWithT;
    private static String endTime;
    private static String endTimeWithT;
    private static String uploadFileName;
    private static String reloadFlag = "0";
    private static long sumForSMJKR;
    private static long sumForSMJKA;
    private static Connection conn;
    private static OutputStreamWriter outputJKR;
    private static FileOutputStream fosJKR;
    private static BufferedWriter bwJKR;
    private static OutputStreamWriter outputJKA;
    private static FileOutputStream fosJKA;
    private static BufferedWriter bwJKA;
    private static File uapLoadTempFile;
    private static File uapLoadTempFileJKR;
    private static File uapLoadTempFileJKA;

    
    public static void readConfig() {
	uap_file_uapload_smjkr = PropertiesUtil.getValue(
		"uap_file_uapload_for_smjkr_db_now").trim();
	uap_file_uapload_smjka = PropertiesUtil.getValue(
		"uap_file_uapload_for_smjka_db_now").trim();
	uap_version = PropertiesUtil.getValue("uap_version").trim();
	prov_code = PropertiesUtil.getValue("prov_code").trim();
	uap_file_uapload_temp = PropertiesUtil
		.getValue("uap_file_uapload_temp").trim();
    }

    public static void main(String[] args) {
	readConfig();
	log.info("UpLoadFileThread start to run......");
	log.info("SMJKR  uap_file_uapload==" + uap_file_uapload_smjkr);
	log.info("SMJKA  uap_file_uapload==" + uap_file_uapload_smjka);
	log.info("uap_version==" + uap_version);
	long statisticRunStartTime = System.currentTimeMillis();
	try {
	    conn = DBUtil.getAiuap20Connection();
	    createTime = DatetimeServices.getNowDateTimeStrWithT(conn);
	     beginTime = DatetimeServices.getLastDayStartTimeStr(conn);
	     beginTimeWithT =
	     DatetimeServices.getLastDayStartTimeStrWithT(conn);
	     endTime = DatetimeServices.getTodayStartTimeStr(conn);
	     endTimeWithT = DatetimeServices.getTodayStartTimeStrWithT(conn);
//	    beginTime = "2011-01-08 00:00:00";
//	    endTime = "2013-05-08 00:00:00";
//	    beginTimeWithT = beginTime.replace(" ", "T");
//	    endTimeWithT = endTime.replace(" ", "T");
	    System.out.println(beginTimeWithT + "   " + endTimeWithT);

	    log
		    .info("generateA4GoldSMJKRandSMJKADayAddFile  ******Start***************");

	    StringBuffer a4SMJKRBuffer = new StringBuffer();
	    StringBuffer a4SMJKABuffer = new StringBuffer();
	    getDataForSMJKRAndSMJKA();
	    countRatioForSMJKRAndSMJKA();
	    handleDumpResult();
	    createSMJKRDayFile(a4SMJKRBuffer, "SMJKR");
	    createSMJKADayFile(a4SMJKABuffer, "SMJKA");
	    
//		DBUtil.notice(tran.getProperty("a4File") + uapLoadTempFileJKR.getAbsolutePath() + " "
//				+ tran.getProperty("sum") + sumForSMJKR + "\n"  + tran.getProperty("a4File") + uapLoadTempFileJKA.getAbsolutePath()
//				+" " + tran.getProperty("sum") + sumForSMJKA);
	    log
		    .info("generateA4GoldSMJKRandSMJKADayAddFile  ******End ***************");

	    long statisticRunEndTime = System.currentTimeMillis();
	    log
		    .info("generate A4GoldSMJKRandSMJKA Day Add FILE  TOTALTIME======"
			    + (statisticRunEndTime - statisticRunStartTime)
			    / 1000L + "s");
	} catch (Exception e) {
	    e.printStackTrace();
	    log.error("GoldBankApplyAndOperateDayAddFile have Exception ...", e);
//	    DBUtil.notice("GoldBankApplyAndOperateDayAddFile have Exception");
	} finally {
	    ConnectionManager.closeConnection(conn);
	    try {
		if (bwJKR != null) {
		    bwJKR.close();
		}
		if (outputJKR != null) {
		    outputJKR.close();
		}
		if (fosJKR != null) {
		    fosJKR.close();
		}

            if (bwJKA != null) {
                bwJKA.close();
            }
            if (outputJKA != null) {
                outputJKA.close();
            }
            if (fosJKA != null) {
                fosJKA.close();
            }

            if (uapLoadTempFileJKR.exists())
                uapLoadTempFileJKR.delete();
            if (uapLoadTempFileJKA.exists())
                uapLoadTempFileJKA.delete();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
    }

    private static void handleDumpResult()  {
    	String truncateA4GoldAppOpe2Sql  = "truncate table a4_gold_app_ope2";
    	
    	String insertDumpResultForTempSql = "insert into a4_gold_app_ope2 (select a.requestid,"
		 + " a.requestname,  a.requesttime,  a.requestinfo, a.restype, a.contextid,"
		 + " a.requestmode, a.requestcond, a.begintime, a.endtime,a.approvename, a.approvetime, "
		 + " a.approvemode,  a.result, a.approveinfo "
		 + " from ( "
		 + "  select rowid row_id, row_number() over(partition by t.requestid order by t.requestid)" 
		 +		" duplicate_num, t.*  from a4_gold_app_ope t ) a  where duplicate_num = 2)";
    	
    	String updateDumRequestIdForTempSql = "update a4_gold_app_ope2 t "
    		+ " set t.contextid = "
    		+ " (select contextid from (select requestid,wmsys.wm_concat(a.contextid) as contextid "
            + " from a4_gold_app_ope a "
            + " where (select COUNT(1) "
            + "     from a4_gold_app_ope b "
            + "     where b.requestid = a.requestid) > 1 "
            + " group by a.requestid) s where s.requestid=t.requestid) "
            + " where exists (select 1 from (select requestid,wmsys.wm_concat(a.contextid) as contextid "
            + " from a4_gold_app_ope a "
            + " where (select COUNT(1) "
            + "     from a4_gold_app_ope b "
            + "   where b.requestid = a.requestid) > 1 "
            + " group by a.requestid) s where s.requestid=t.requestid)";
    	
    	String insertSingleResultToTempSql = "insert into a4_gold_app_ope2 "
    		+ "(select * from a4_gold_app_ope a "
    		+ " where (select COUNT(1) from a4_gold_app_ope b where b.requestid = a.requestid) = 1)";
    	
    	log.info("truncateA4GoldAppOpe2Sql = " + truncateA4GoldAppOpe2Sql);
    	log.info("insertDumpResultForTempSql = " + insertDumpResultForTempSql);
    	log.info("updateDumRequestIdForTempSql = " + updateDumRequestIdForTempSql);
    	log.info("insertSingleResultToTempSql = " + insertSingleResultToTempSql);
    	
    	PreparedStatement prepA4goldAppppOpe2truncate;
    	PreparedStatement prepInsertDumpStat;
    	PreparedStatement prepUpdateDumpStat;
    	PreparedStatement prepInsertSingleResultStat;
    	try {
    		prepA4goldAppppOpe2truncate = conn.prepareStatement(truncateA4GoldAppOpe2Sql);
    	    ResultSet rs = prepA4goldAppppOpe2truncate.executeQuery();

    	    prepInsertDumpStat = conn.prepareStatement(insertDumpResultForTempSql);
    	    ResultSet rsResultSet = prepInsertDumpStat.executeQuery();
    	    
    	    prepUpdateDumpStat = conn.prepareStatement(updateDumRequestIdForTempSql);
    	    ResultSet updateResultSet = prepUpdateDumpStat.executeQuery();
    	    
    	    prepInsertSingleResultStat = conn.prepareStatement(insertSingleResultToTempSql);
    	    ResultSet insertSingleResultSet = prepInsertSingleResultStat.executeQuery();

    	    ConnectionManager.closeResultSet(rs);
    	    ConnectionManager.closeResultSet(rsResultSet);
    	    ConnectionManager.closeResultSet(updateResultSet);
    	    ConnectionManager.closeResultSet(insertSingleResultSet);
    	    
    	    ConnectionManager.closePrepStmt(prepInsertDumpStat);
    	    ConnectionManager.closePrepStmt(prepA4goldAppppOpe2truncate);
    	    ConnectionManager.closePrepStmt(prepUpdateDumpStat);
    	    ConnectionManager.closePrepStmt(prepInsertSingleResultStat);
    	    
    	   
    	} catch (SQLException e) {
    	    log.error(e);
    	    e.printStackTrace();
    	}
		
	}

	private static void countRatioForSMJKRAndSMJKA() {
	String countTotal = "select count(*) from a4_gold_app_ope_first";
	String countRatioOver = "select count(*) from a4_gold_app_ope_first t where (to_date(t.endtime, 'yyyy-mm-dd hh24:mi:ss') -"
		+ " to_date(t.begintime, 'yyyy-mm-dd hh24:mi:ss')) > 2 / 24";
	PreparedStatement prepStmttruncate;
	PreparedStatement prepStmtinsert;
	Long count = null;
	Long ratio = null;
	try {
	    prepStmttruncate = conn.prepareStatement(countTotal);
	    ResultSet rs = prepStmttruncate.executeQuery();
	    while (rs.next()) {
		count = Long.valueOf(rs.getLong(1));
	    }

	    prepStmtinsert = conn.prepareStatement(countRatioOver);
	    ResultSet rsResultSet = prepStmtinsert.executeQuery();
	    while (rsResultSet.next()) {
		ratio = Long.valueOf(rsResultSet.getLong(1));
	    }

	    ConnectionManager.closeResultSet(rs);
	    ConnectionManager.closeResultSet(rsResultSet);
	    ConnectionManager.closePrepStmt(prepStmttruncate);
	    ConnectionManager.closePrepStmt(prepStmtinsert);
	} catch (SQLException e) {
	    log.error(e);
	    e.printStackTrace();
	}

	String truncateA4GoldAppOpe = "truncate table a4_gold_app_ope";
	String insertA4GoldAppOpe = "insert into a4_gold_app_ope select * from a4_gold_app_ope_first";
	float float1 = (ratio /(float)count);
	log.info("countOver 10% ratio/count==="+float1);
	if ( float1> 0.1) {
	    insertA4GoldAppOpe = "insert into a4_gold_app_ope (select * from a4_gold_app_ope_first t where (to_date(t.endtime, 'yyyy-mm-dd hh24:mi:ss') - "
		    + "to_date(t.begintime, 'yyyy-mm-dd hh24:mi:ss')) <= 2 / 24)";
	}
	PreparedStatement prepSecondtruncate;
	PreparedStatement prepSecondinsert;
	try {
	    prepSecondtruncate = conn.prepareStatement(truncateA4GoldAppOpe);
	    ResultSet rs = prepSecondtruncate.executeQuery();

	    prepSecondinsert = conn.prepareStatement(insertA4GoldAppOpe);
	    ResultSet rsResultSet = prepSecondinsert.executeQuery();

	    ConnectionManager.closeResultSet(rs);
	    ConnectionManager.closeResultSet(rsResultSet);
	    ConnectionManager.closePrepStmt(prepSecondinsert);
	    ConnectionManager.closePrepStmt(prepSecondtruncate);
	} catch (SQLException e) {
	    log.error(e);
	    e.printStackTrace();
	}
    }

    // 梳理数据方法，取出SMJKR，SMJKA文件的数据，将两部分数据放在同一张表中，生成文件时从该表中取出不同数据
    private static void getDataForSMJKRAndSMJKA() {
	String truncateA4GoldAppOpeFirst = "truncate table a4_gold_app_ope_first";
	String insertA4GoldAppOpeFirst = "insert into a4_gold_app_ope_first"
		+ "  (select * from (SELECT info.op_seriavl as requestid,"
		+ "          (select w.login_name"
		+ "             from uap_main_acct w"
		+ "            where w.main_acct_id = info.oper_main_acct_id) requestname,"
		+ "          to_char(info.apply_time, 'yyyy-mm-dd hh24:mi:ss') as requesttime,"
		+ "          info.oper_content as requestinfo,"
		+ "          (case t.app_code"
		+ "            when 'HANGBOSS' then"
		+ "             '00'"
		+ "            when 'HANGBASS' then"
		+ "             '02'"
		+ "            when 'HANGVGOP' then"
		+ "             '03'"
		+ "            when 'HANGESOP' then"
		+ "             '04'"
		+ "            when 'UAP' then"
		+ "             '05'"
		+ "          end) as restype,"
		+ "          (SELECT RE.UPLOAD_CONTEXTID"
		+ "             FROM UAP_BUSI_SCENE_ID_REALATION RE"
		+ "            WHERE RE.UAP_CONTEXTID = APPSCENE.BUSI_SCENE_ID"
		+ "              AND RE.SCENE_TYPE = '1') CONTEXTID,"
		+ "          '00' as requestmode,"
		+ "          decode(info.apply_expire_time, null, '00', '01') as requestcond,"
		+ "          to_char(info.apply_time, 'yyyy-mm-dd hh24:mi:ss') as begintime,"
		+ "          (case"
		+ "            when info.apply_expire_time is null then"
		+ "             to_char(info.apply_time + 3 / 1440, 'yyyy-mm-dd hh24:mi:ss')"
		+ "            else"
		+ "             to_char(info.apply_expire_time, 'yyyy-mm-dd hh24:mi:ss')"
		+ "          end) as endtime,"
		+ "          (select ww.login_name"
		+ "             from uap_main_acct ww"
		+ "            where ww.main_acct_id = info.appr_main_acct_id) approvename,"
		+ "          to_char(info.check_time, 'yyyy-mm-dd hh24:mi:ss') as approvetime,"
		+ "          (case info.appr_type"
		+ "            when '1' then" + "             '01'"
		+ "            when '4' then" + "             '01'"
		+ "            when '5' then" + "             '01'"
		+ "            when '6' then" + "             '01'"
		+ "            when '2' then" + "             '00'"
		+ "            when '3' then" + "             '00'"
		+ "            when '7' then" + "             '02'"
		+ "            else" + "             '00'"
		+ "          end) as approvemode,"
		+ "          (case info.check_status"
		+ "            when '1' then" + "             '0'"
		+ "            when '2' then" + "             '1'"
		+ "            when '0' then" + "             '2'"
		+ "            when '3' then" + "             '1'"
		+ "          end) as result,"
		+ "          info.bank_code as approveinfo"
		+ "     FROM UAP_APP_SCENE      APPSCENE,"
		+ "          UAP_APP_SCENE_DATA APPR,"
		+ "          UAP_GOLD_INFO      INFO,"
		+ "          uap_app            t"
		+ "    WHERE APPSCENE.BUSI_SCENE_ID = APPR.APP_SCENE_ID"
		+ "      and info.system_id = t.app_id"
		+ "      AND INFO.OPERATE_TYPE = '1'"
		+ "      AND APPR.OPERATE_ID = INFO.OPERATE_ID"
		+ "      AND INFO.CREATE_TIME >=" + "          TO_DATE('"
		+ beginTime
		+ "', 'yyyy-MM-dd HH24:mi:ss')"
		+ "      AND INFO.CREATE_TIME <"
		+ "          TO_DATE('"
		+ endTime
		+ "', 'yyyy-MM-dd HH24:mi:ss')"
		+ "   UNION ALL"
		+ "   SELECT INFOD.op_seriavl as requestid,"
		+ "          (select w.login_name"
		+ "             from uap_main_acct w"
		+ "            where w.main_acct_id = INFOD.oper_main_acct_id) requestname,"
		+ "          to_char(INFOD.apply_time, 'yyyy-mm-dd hh24:mi:ss') as requesttime,"
		+ "          INFOD.oper_content as requestinfo,"
		+ "          '11' as restype,"
		+ "          (SELECT RE.UPLOAD_CONTEXTID"
		+ "             FROM UAP_BUSI_SCENE_ID_REALATION RE"
		+ "            WHERE RE.UAP_CONTEXTID = SYSSCENE.BUSI_SCENE_ID"
		+ "              AND RE.SCENE_TYPE = '2') CONTEXTID,"
		+ "          (case"
		+ "            when infod.apply_acct_info is null then"
		+ "             '00'"
		+ "            else"
		+ "             '01'"
		+ "          end) as requestmode,"
		+ "          decode(INFOD.apply_expire_time, null, '00', '01') as requestcond,"
		+ "          to_char(INFOD.apply_time, 'yyyy-mm-dd hh24:mi:ss') as begintime,"
		+ "          (case"
		+ "            when infod.apply_expire_time is null then"
		+ "             to_char(infod.apply_time + 3 / 1440, 'yyyy-mm-dd hh24:mi:ss')"
		+ "            else"
		+ "             to_char(infod.apply_expire_time, 'yyyy-mm-dd hh24:mi:ss')"
		+ "          end) as endtime,"
		+ "          (select ww.login_name"
		+ "             from uap_main_acct ww"
		+ "            where ww.main_acct_id = INFOD.appr_main_acct_id) approvename,"
		+ "          to_char(INFOD.check_time, 'yyyy-mm-dd hh24:mi:ss') as approvetime,"
		+ "          (case INFOD.appr_type"
		+ "            when '1' then"
		+ "             '01'"
		+ "            when '4' then"
		+ "             '01'"
		+ "            when '5' then"
		+ "             '01'"
		+ "            when '6' then"
		+ "             '01'"
		+ "            when '2' then"
		+ "             '00'"
		+ "            when '3' then"
		+ "             '00'"
		+ "            when '7' then"
		+ "             '02'"
		+ "            else"
		+ "             '00'"
		+ "          end) as approvemode,"
		+ "          (case INFOD.Check_Status"
		+ "            when '1' then"
		+ "             '0'"
		+ "            when '2' then"
		+ "             '1'"
		+ "            when '0' then"
		+ "             '2'"
		+ "            when '3' then"
		+ "             '1'"
		+ "          end) as result,"
		+ "          INFOD.Bank_Code as approveinfo"
		+ "     FROM UAP_SYS_SCENE      SYSSCENE,"
		+ "          UAP_SYS_SCENE_DATA SSD,"
		+ "          UAP_GOLD_INFO      INFOD"
		+ "    WHERE SYSSCENE.BUSI_SCENE_ID = SSD.SYS_SCENE_ID"
		+ "      AND INFOD.OPERATE_TYPE = '2'"
		+ "      AND INFOD.OPERATE_ID = SSD.META_DATA_ID"
		+ "      AND INFOD.CREATE_TIME >="
		+ "          TO_DATE('"
		+ beginTime
		+ "', 'yyyy-MM-dd HH24:mi:ss')"
		+ "      AND INFOD.CREATE_TIME <"
		+ "          TO_DATE('"
		+ endTime
		+ "', 'yyyy-MM-dd HH24:mi:ss')"
		+ "   UNION ALL"
		+ "   SELECT INFOF.op_seriavl as requestid,"
		+ "          (select w.login_name"
		+ "             from uap_main_acct w"
		+ "            where w.main_acct_id = INFOF.oper_main_acct_id) requestname,"
		+ "          to_char(INFOF.apply_time, 'yyyy-mm-dd hh24:mi:ss') as requesttime,"
		+ "          INFOF.oper_content as requestinfo,"
		+ "          '12' as restype,"
		+ "          (SELECT RE.UPLOAD_CONTEXTID"
		+ "             FROM UAP_BUSI_SCENE_ID_REALATION RE"
		+ "            WHERE RE.UAP_CONTEXTID = SYSSCENE.BUSI_SCENE_ID"
		+ "              AND RE.SCENE_TYPE = '2') CONTEXTID,"
		+ "          (case"
		+ "            when infof.apply_acct_info is null then"
		+ "             '00'"
		+ "            else"
		+ "             '01'"
		+ "          end) as requestmode,"
		+ "          decode(INFOF.apply_expire_time, null, '00', '01') as requestcond,"
		+ "          to_char(INFOF.apply_time, 'yyyy-mm-dd hh24:mi:ss') as begintime,"
		+ "          (case"
		+ "            when INFOF.apply_expire_time is null then"
		+ "             to_char(INFOF.apply_time + 3 / 1440, 'yyyy-mm-dd hh24:mi:ss')"
		+ "            else"
		+ "             to_char(INFOF.apply_expire_time, 'yyyy-mm-dd hh24:mi:ss')"
		+ "          end) as endtime,"
		+ "          (select ww.login_name"
		+ "             from uap_main_acct ww"
		+ "            where ww.main_acct_id = INFOF.appr_main_acct_id) approvename,"
		+ "          to_char(INFOF.check_time, 'yyyy-mm-dd hh24:mi:ss') as approvetime,"
		+ "          (case INFOF.appr_type"
		+ "            when '1' then"
		+ "             '01'"
		+ "            when '4' then"
		+ "             '01'"
		+ "            when '5' then"
		+ "             '01'"
		+ "            when '6' then"
		+ "             '01'"
		+ "            when '2' then"
		+ "             '00'"
		+ "            when '3' then"
		+ "             '00'"
		+ "            when '7' then"
		+ "             '02'"
		+ "            else"
		+ "             '00'"
		+ "          end) as approvemode,"
		+ "          (case INFOF.Check_Status"
		+ "            when '1' then"
		+ "             '0'"
		+ "            when '2' then"
		+ "             '1'"
		+ "            when '0' then"
		+ "             '2'"
		+ "            when '3' then"
		+ "             '1'"
		+ "          end) as result,"
		+ "          INFOF.Bank_Code as approveinfo"
		+ "     FROM UAP_SYS_SCENE      SYSSCENE,"
		+ "          UAP_SYS_SCENE_DATA SSD,"
		+ "          UAP_GOLD_INFO      INFOF"
		+ "    WHERE SYSSCENE.BUSI_SCENE_ID = SSD.SYS_SCENE_ID"
		+ "      AND INFOF.OPERATE_TYPE = '3'"
		+ "      AND INFOF.OPERATE_ID = SSD.META_DATA_ID"
		+ "      AND INFOF.CREATE_TIME >="
		+ "          TO_DATE('"
		+ beginTime
		+ "', 'yyyy-MM-dd HH24:mi:ss')"
		+ "      AND INFOF.CREATE_TIME <"
		+ "          TO_DATE('"
		+ endTime
		+ "', 'yyyy-MM-dd HH24:mi:ss'))m "
		+ " where (to_date(m.endtime, 'yyyy-mm-dd hh24:mi:ss') -to_date(m.begintime, 'yyyy-mm-dd hh24:mi:ss')) < 6 / 24 and m.contextid is not null)";
	log.info("truncateA4GoldAppOpe====" + truncateA4GoldAppOpeFirst);
	log.info("insertA4GoldAppOpe======" + insertA4GoldAppOpeFirst);

	PreparedStatement prepStmttruncate;
	PreparedStatement prepStmtinsert;
	try {
	    prepStmttruncate = conn.prepareStatement(truncateA4GoldAppOpeFirst);
	    ResultSet rs = prepStmttruncate.executeQuery();

	    prepStmtinsert = conn.prepareStatement(insertA4GoldAppOpeFirst);
	    ResultSet rsResultSet = prepStmtinsert.executeQuery();

	    ConnectionManager.closeResultSet(rs);
	    ConnectionManager.closeResultSet(rsResultSet);
	    ConnectionManager.closePrepStmt(prepStmttruncate);
	    ConnectionManager.closePrepStmt(prepStmtinsert);
	} catch (SQLException e) {
	    log.error(e);
	    e.printStackTrace();
	}

    }

    // 生成SMJKR文件方法
    private static void createSMJKRDayFile(StringBuffer a4SMJKRBuffer,
	    String type) throws FileNotFoundException,
	    UnsupportedEncodingException, IOException, Exception {

	createDayFileCommon(a4SMJKRBuffer, type, uap_file_uapload_smjkr);
	System.out.println("SMJKR====" + uploadFileName);
	insertDrUploadFileInfo(type, sumForSMJKR);

    }

    // 生成SMJKA文件方法
    private static void createSMJKADayFile(StringBuffer a4SMJKABuffer,
	    String type) throws FileNotFoundException,
	    UnsupportedEncodingException, IOException, Exception {
	createDayFileCommon(a4SMJKABuffer, type, uap_file_uapload_smjka);
	System.out.println("SMJKA====" + uploadFileName);

	insertDrUploadFileInfo(type, sumForSMJKA);

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

    // 生成文件头方法
    private static void createDayFileCommon(StringBuffer a4GoldDayFileBuffer,
	    String type, String uapfileupload) throws Exception, IOException,
	    FileNotFoundException, UnsupportedEncodingException {

	uploadFileName = type + "_" + prov_code + "_" + "01DY" + "_"
		+ DatetimeServices.getLastDayStr(conn) + "_" + "000" + "_"
		+ "000.xml";

	a4GoldDayFileBuffer
		.append("<?xml version='1.0' encoding='UTF-8'?>\r\n");
	a4GoldDayFileBuffer.append("<smp>\r\n");
	a4GoldDayFileBuffer.append("<type>" + type + "</type>\r\n");
	a4GoldDayFileBuffer.append("<province>" + prov_code + "</province>"
		+ "\r\n");
	a4GoldDayFileBuffer.append("<createtime>" + createTime
		+ "</createtime>" + "\r\n");

	String a4GoldSceneOpDayAddFileSql = "select *  from a4_gold_app_ope2 t  where t.requestname is not null "
		+" and to_date(t.endtime, 'yyyy-mm-dd hh24:mi:ss') -to_date(t.begintime, 'yyyy-mm-dd hh24:mi:ss')>0"
		+ " and ((t.result = '0' and t.approvetime is not null and "
		+ " to_date(t.requesttime, 'yyyy-MM-dd hh24:mi:ss') < to_date(t.approvetime, 'yyyy-MM-dd hh24:mi:ss'))"
		+ " or "
        + " (t.result = '1' and t.approvetime is not null and "
        + " to_date(t.requesttime, 'yyyy-MM-dd hh24:mi:ss') < "
        + " to_date(t.approvetime, 'yyyy-MM-dd hh24:mi:ss')) or "
        + " (t.result = '2' and t.approvetime is null))";

	log.info("a4GoldSceneOpDayAddFileSql===" + a4GoldSceneOpDayAddFileSql);
	log.info("getSMJKRDayFullInfo*******************Start******");

	if ("SMJKR".equals(type)) {
	    getDayFileSMJKRInfo(a4GoldSceneOpDayAddFileSql, a4GoldDayFileBuffer);
	} else if ("SMJKA".equals(type)) {
	    getDayFileSMJKAInfo(a4GoldSceneOpDayAddFileSql, a4GoldDayFileBuffer);
	}
	log.info("getSMJKRDayFullInfo*******************End*********");
	a4GoldDayFileBuffer.append("</data>\r\n");
	a4GoldDayFileBuffer.append("</smp>");
	log.info("a4GoldDayFileBuffer====" + a4GoldDayFileBuffer.toString());

	uapLoadTempFile = new File(uap_file_uapload_temp + "/" + uploadFileName);
        // 处理只在temp中删除了JKA的问题 2013-07-09
        if ("SMJKR".equals(type)) {
            uapLoadTempFileJKR = uapLoadTempFile;
            writeJKRBufferToTempFile(a4GoldDayFileBuffer);
        } else if ("SMJKA".equals(type)) {
            uapLoadTempFileJKA = uapLoadTempFile;
            writeJKABufferToTempFile(a4GoldDayFileBuffer);
        }

        /*废弃代码*/
	/*writeA4AppAcctDayAddFileBufferToTempFile(a4GoldDayFileBuffer);*/

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

    private static void writeJKRBufferToTempFile(
	    StringBuffer a4AppAcctDayAddFileBuffer) throws IOException {
	bwJKR.write(a4AppAcctDayAddFileBuffer.toString());
	bwJKR.flush();
	outputJKR.flush();
	fosJKR.flush();
    }

    private static void writeJKABufferToTempFile(
            StringBuffer a4AppAcctDayAddFileBuffer) throws IOException {
        bwJKA.write(a4AppAcctDayAddFileBuffer.toString());
        bwJKA.flush();
        outputJKA.flush();
        fosJKA.flush();
    }

    // //生成SMJKR文件data部分方法
    private static void getDayFileSMJKRInfo(String qqLogSql,
	    StringBuffer a4GoldDayFileBuffer) throws Exception {
	PreparedStatement prepStmt = conn.prepareStatement(qqLogSql,
		ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	ResultSet rs = prepStmt.executeQuery();
	rs.last();

	sumForSMJKR = rs.getRow();
	log.info("total sumForSMJKR ===" + sumForSMJKR);
	a4GoldDayFileBuffer.append("<sum>" + sumForSMJKR + "</sum>\r\n");
	a4GoldDayFileBuffer.append("<begintime>" + beginTimeWithT
		+ "</begintime>" + "\r\n");
	a4GoldDayFileBuffer.append("<endtime>" + endTimeWithT + "</endtime>"
		+ "\r\n");
	a4GoldDayFileBuffer.append("<data>");

	rs.beforeFirst();
	uapLoadTempFile = new File(uap_file_uapload_temp + "/" + uploadFileName);
	if (uapLoadTempFile.exists()) {
	    uapLoadTempFile.delete();
	    uapLoadTempFile.createNewFile();
	}
	fosJKR = new FileOutputStream(uapLoadTempFile, true);
	outputJKR = new OutputStreamWriter(fosJKR, "UTF-8");
	bwJKR = new BufferedWriter(outputJKR);

	int i = 0;
	while (rs.next()) {
	    i++;
	    if (i % 1000 == 0) {
		log
			.info("writeA4GoldSceneOpDayAddFileBufferToTempFile******start***");
		writeJKRBufferToTempFile(a4GoldDayFileBuffer);
		log
			.info("writeGoldSceneOpDayAddFileBufferToTempFile******end***");
		a4GoldDayFileBuffer.setLength(0);
	    }

	    a4GoldDayFileBuffer.append("<rcd>\r\n");
	    a4GoldDayFileBuffer.append("<seq>" + String.valueOf(i) + "</seq>"
		    + "\r\n");
	    a4GoldDayFileBuffer.append("<requestname>"
		    + rs.getString("requestname").trim() + "</requestname>"
		    + "\r\n");
	    a4GoldDayFileBuffer.append("<requesttime>"
		    + rs.getString("requesttime").trim().replace(" ", "T")
		    + "</requesttime>" + "\r\n");
        a4GoldDayFileBuffer.append("<requestinfo><![CDATA["
                + rs.getString("requestinfo").trim()  + "]]></requestinfo>"
		    + "\r\n");
	    a4GoldDayFileBuffer.append("<restype>"
		    + rs.getString("restype").trim() + "</restype>" + "\r\n");
	    a4GoldDayFileBuffer.append("<contextid>"
		    + rs.getString("contextid").trim() + "</contextid>"
		    + "\r\n");
	    a4GoldDayFileBuffer.append("<requestmode>"
		    + rs.getString("requestmode").trim() + "</requestmode>"
		    + "\r\n");
	    a4GoldDayFileBuffer.append("<requestcond>"
		    + rs.getString("requestcond").trim() + "</requestcond>"
		    + "\r\n");
	    a4GoldDayFileBuffer.append("<begintime>"
		    + rs.getString("begintime").trim().replace(" ", "T")
		    + "</begintime>" + "\r\n");
	    a4GoldDayFileBuffer.append("<endtime>"
		    + rs.getString("endtime").trim().replace(" ", "T")
		    + "</endtime>" + "\r\n");
	    a4GoldDayFileBuffer.append("<requestid>"
		    + rs.getString("requestid").trim() + "</requestid>"
		    + "\r\n");

	    a4GoldDayFileBuffer.append("</rcd>\r\n");
	}
	ConnectionManager.closeResultSet(rs);
	ConnectionManager.closePrepStmt(prepStmt);
    }

    // 生成SMJKA文件data部分方法
    private static void getDayFileSMJKAInfo(String qqLogSql,
	    StringBuffer a4GoldDayFileBuffer) throws Exception {
	PreparedStatement prepStmt = conn.prepareStatement(qqLogSql,
		ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	ResultSet rs = prepStmt.executeQuery();
	rs.last();

	sumForSMJKA = rs.getRow();
	log.info("total sumForSMJKR ===" + sumForSMJKA);
	a4GoldDayFileBuffer.append("<sum>" + sumForSMJKA + "</sum>\r\n");
	a4GoldDayFileBuffer.append("<begintime>" + beginTimeWithT
		+ "</begintime>" + "\r\n");
	a4GoldDayFileBuffer.append("<endtime>" + endTimeWithT + "</endtime>"
		+ "\r\n");
	a4GoldDayFileBuffer.append("<data>");

	rs.beforeFirst();
	uapLoadTempFile = new File(uap_file_uapload_temp + "/" + uploadFileName);
	if (uapLoadTempFile.exists()) {
	    uapLoadTempFile.delete();
	    uapLoadTempFile.createNewFile();
	}
	fosJKA = new FileOutputStream(uapLoadTempFile, true);
	outputJKA = new OutputStreamWriter(fosJKA, "UTF-8");
	bwJKA = new BufferedWriter(outputJKA);

	int i = 0;
	while (rs.next()) {
	    i++;
	    if (i % 1000 == 0) {
		log
			.info("writeA4GoldSceneOpDayAddFileBufferToTempFile******start***");
		writeJKABufferToTempFile(a4GoldDayFileBuffer);
		log
			.info("writeGoldSceneOpDayAddFileBufferToTempFile******end***");
		a4GoldDayFileBuffer.setLength(0);
	    }

	    // 判断授权信息，如果授权方式是自动授权，授权信息填写工单号，如果不是自动授权则要根据操作结果来判断授权信息
	    String approveinfo = "同意";
	    if ("02".equals(rs.getString("approvemode").trim())) {
		approveinfo = rs.getString("approveinfo").trim();
	    } else {
		if ("0".equals(rs.getString("result").trim())) {

		} else {
		    approveinfo = "拒绝";
		}

	    }
	    a4GoldDayFileBuffer.append("<rcd>\r\n");
	    a4GoldDayFileBuffer.append("<seq>" + String.valueOf(i) + "</seq>"
		    + "\r\n");
	    a4GoldDayFileBuffer.append("<approvename>"
		    + rs.getString("approvename").trim() + "</approvename>"
		    + "\r\n");

        String approvetime = rs.getString("approvetime");
        if (approvetime != null) {
            a4GoldDayFileBuffer.append("<approvetime>"
                + rs.getString("approvetime").trim().replace(" ", "T")
                + "</approvetime>" + "\r\n");
        } else {
            a4GoldDayFileBuffer.append("<approvetime>"
                    + rs.getString("endtime").trim().replace(" ", "T")
                    + "</approvetime>" + "\r\n");
        }
        a4GoldDayFileBuffer.append("<requestid>"
		    + rs.getString("requestid").trim() + "</requestid>"
		    + "\r\n");
	    a4GoldDayFileBuffer.append("<approvemode>"
		    + rs.getString("approvemode").trim() + "</approvemode>"
		    + "\r\n");
	    a4GoldDayFileBuffer.append("<result>"
		    + rs.getString("result").trim() + "</result>" + "\r\n");
	    a4GoldDayFileBuffer.append("<approveinfo>" + approveinfo
		    + "</approveinfo>" + "\r\n");

	    a4GoldDayFileBuffer.append("</rcd>\r\n");
	}
	ConnectionManager.closeResultSet(rs);
	ConnectionManager.closePrepStmt(prepStmt);
    }

}