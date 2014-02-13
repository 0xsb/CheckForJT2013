package com.ailk.jt.validate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.ailk.jt.util.DBUtil;
import com.ailk.jt.util.PropertiesUtil;

public class SearchValidatorForHour {

	private static final Logger log = Logger
			.getLogger(SearchValidatorForDay.class);
	private static String osflag = PropertiesUtil.getValue("os_flag");
	private static Properties tran = PropertiesUtil
			.getProperties("/tran.properties");
	private static String smmalPath = PropertiesUtil
			.getValue("uap_file_uapload_for_smmal_db_now");
	private static String smsalPath = PropertiesUtil
			.getValue("uap_file_uapload_for_smsal_db_now");

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SearchAndValidator();
	}
	public static void SearchAndValidator(){
		Calendar calendar = Calendar.getInstance();
		String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
		// 寻找SMMAL
		try {
			String smmalXMLFile = OperateFile.searchEndFile(smmalPath, "xml");
			log.info(smmalXMLFile + " has bean found!");
			//校验文件
			String smmalFile = smmalPath + osflag + smmalXMLFile;
			boolean result = FileValidator.validate(smmalFile, smmalPath);
			if (result) {
				log.info("SMMAL file validate success!!!");
			} else {
				DBUtil.notice(tran.getProperty("a4File") + smmalFile + tran.getProperty("validatorFailed"));
			}
		} catch (Exception e) {
			// 如果无法找到文件会报异常
			DBUtil.notice(currentTime +" " + smmalPath + tran.getProperty("nofileError"));
			e.printStackTrace();
		}
		// 寻找SMSAL
		try {
			String smsalXMLFile = OperateFile.searchEndFile(smsalPath, "xml");
			log.info(smsalXMLFile + " has bean found!");
			//校验文件
			String smsalFile = smsalPath + osflag + smsalXMLFile;
			boolean result = FileValidator.validate(smsalFile, smsalPath);
			if (result) {
				log.info("SMSAL file validate success!!!");
			} else {
				DBUtil.notice(tran.getProperty("a4File") + smsalFile + tran.getProperty("validatorFailed"));
			}
		} catch (Exception e) {
			// 如果无法找到文件会报异常
			DBUtil.notice(currentTime +" " + smsalPath + tran.getProperty("nofileError"));
			e.printStackTrace();
		}
	}

}
