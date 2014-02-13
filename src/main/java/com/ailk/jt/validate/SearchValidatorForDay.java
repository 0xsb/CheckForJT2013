package com.ailk.jt.validate;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.ailk.jt.util.DBUtil;
import com.ailk.jt.util.PropertiesUtil;

public class SearchValidatorForDay {

	private static final Logger log = Logger
			.getLogger(SearchValidatorForDay.class);
	private static String osflag = PropertiesUtil.getValue("os_flag");
	private static Properties tran = PropertiesUtil
			.getProperties("/tran.properties");
	private static String sm4arPath = PropertiesUtil
			.getValue("uap_file_uapload_for_init_R");
	private static String sm4aiPath = PropertiesUtil
			.getValue("uap_file_uapload_for_init_I");
	private static String smbhrPath = PropertiesUtil
			.getValue("uap_file_uapload_for_smbhr_db_now");
	private static String smjkrPath = PropertiesUtil
			.getValue("uap_file_uapload_for_smjkr_db_now");
	private static String smdarPath = PropertiesUtil
			.getValue("uap_file_uapload_for_smdar_db_now");
	private static String smmaiPath = PropertiesUtil
			.getValue("uap_file_uapload_for_smmai_db_now");

	/**
	 * 在文件生成目录查找文件，如果无法找到文件则发短信告警，找到文件后对文件进行文本级校验
	 */
	public static void main(String[] args) {
		// 寻找SM4AR
		SearchFile(sm4arPath, "SM4AR");
		// 寻找SM4AI
		SearchFile(sm4aiPath, "SM4AI");
		// 寻找SMBHR
		SearchFile(smbhrPath, "SMBHR");
		// 寻找SMJKR
		SearchFile(smjkrPath, "SMJKR");
		// 寻找SMDAR
		SearchFile(smdarPath, "SMDAR");
		// 寻找SMMAI
		SearchFile(smmaiPath, "SMMAI");
	}

	public static void SearchFile(String path, String type) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1);
		String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(calendar.getTime());
		String currentyyyymmdd = new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
		try {
			File dayXMLFilePath = FileUtils.getFile(path);
			String dayXMLFile = null;
			Collection<File> dayXMLFileconCollection = FileUtils.listFiles(
					dayXMLFilePath, new String[] { "xml" }, false);
			for (File file : dayXMLFileconCollection) {
				if (file.getName().contains(type)) {
					dayXMLFile = file.getName().toString();
				}
			}
			if (dayXMLFile != null) {
				log.info(dayXMLFile + " has bean found!");
				// 校验文件
				String dayFile = path + osflag + dayXMLFile;
				boolean result = FileValidator.validate(dayFile, path);
				if (result) {
					log.info(type + " file validate success!!!");
				} else {
					DBUtil.notice(tran.getProperty("a4File") + dayFile
							+ tran.getProperty("validatorFailed"));
				}
			} else {
				// 如果无法找到文件会发短信通知
				log.info("Error: the directory: " + path + " can't find "
						+ type + " !!!");
				DBUtil.notice("【4A】截止到" + currentTime + " "
						+ path + tran.getProperty("nofileError") + type
						+ "_371_01DY_" + currentyyyymmdd + "_000_000.xml"
						+ tran.getProperty("nofileErrorDes"));
			}
		} catch (Exception e) {
			log.info(path + type + "*.xml throws exception!!!");
			e.printStackTrace();
		}
	}
}
