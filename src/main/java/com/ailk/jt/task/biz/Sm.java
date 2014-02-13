package com.ailk.jt.task.biz;
import java.io.File;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.net.ftp.FTPClient;
import org.springframework.context.support.StaticApplicationContext;

import com.ailk.jt.util.DBUtil;
import com.ailk.uap.config.PropertiesUtil;

public class Sm {
	private static int count = 0;
	private static String bomc_file_upload_dir;
	private static String ftp_server_ip;
	private static String ftp_server_port;
	private static String ftp_server_username;
	private static String ftp_server_password;
	private static FTPClient ftpClientInstance;
	private static Connection conn;
	  
	public static void main(String[] args) {
		String filePath = "D:/work/work2012/upload";
		File file = new File(filePath);
		Calendar calendar = Calendar.getInstance();// 此时打印它获取的是系统当前时间
		calendar.add(Calendar.DATE, -1); // 得到前一天
		String yyyymmdd = new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
		System.out.println("============"+yyyymmdd);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("fileIntervalFlag", "DY");
		map.put("FileNameFlag", "SMJKR");
		map.put("uploadDateFlag", yyyymmdd);

		List<File> fileList = getFile(file, map);
		System.out.println(fileList.toString());		
// checkJKR();
	}

	private static void checkJKR() {
		// TODO Auto-generated method stub
		
	}

	public static void readConfig()
	  {
//	    file_upload_delay = PropertiesUtil.getValue("file_upload_delay").trim();
//	    uap_file_uapload = PropertiesUtil.getValue("uap_file_uapload").trim();
//	    uap_file_upload_bak = PropertiesUtil.getValue("uap_file_upload_bak").trim();

	    bomc_file_upload_dir = PropertiesUtil.getValue("bomc_file_upload_dir").trim();

	    ftp_server_ip = PropertiesUtil.getValue("ftp_server_ip").trim();
	    ftp_server_port = PropertiesUtil.getValue("ftp_server_port").trim();
	    ftp_server_username = PropertiesUtil.getValue("ftp_server_username").trim();

	    ftp_server_password = PropertiesUtil.getValue("ftp_server_password").trim();
	  }
	/**
	 * @Title: getFile
	 * @Description: 依据指定的条件信息返回文件
	 * @param filePath
	 *            上传文件路径
	 * @param fileIntervalFlag
	 *            文件间隔时间,HR/DY/MO
	 * @param FileNameFlag
	 *            文件名称,SMMAL/SMSAL/....
	 * @param uploadDateFlag
	 *            文件上传时间YYYYMMDD
	 * @return List<File> 返回文件列表
	 */
	public static List<File> getFile(File dirFile, HashMap<String, String> parameterMap) {
		Pattern pattern = null;
		String fileIntervalFlag = parameterMap.containsKey("fileIntervalFlag") ? parameterMap.get("fileIntervalFlag"):"";
		String FileNameFlag = parameterMap.containsKey("FileNameFlag") ? parameterMap.get("FileNameFlag") : "";
		String uploadDateFlag = parameterMap.containsKey("uploadDateFlag") ? parameterMap.get("uploadDateFlag") : "";
		System.out.println("----------- "+uploadDateFlag);

		if ("MO".equalsIgnoreCase(fileIntervalFlag)) {
			pattern = Pattern.compile(".*" + FileNameFlag + ".*(01MO).*" + uploadDateFlag + ".*xml$");
		}
		if ("DY".equalsIgnoreCase(fileIntervalFlag)) {
//			pattern = Pattern.compile(".*" + FileNameFlag + ".*(01DY).*" + uploadDateFlag + ".*xml$");
			pattern = Pattern.compile(".*" + "FS_" + ".*(01DY).*" + uploadDateFlag + ".*xml$");
		}
		if ("HR".equalsIgnoreCase(fileIntervalFlag)) {
			pattern = Pattern.compile(".*" + FileNameFlag + ".*(01HR).*" + uploadDateFlag + ".*xml$");
		}

		List<File> FilePath = new ArrayList<File>();
		File[] fileList = dirFile.listFiles();
		for (int i = 0; i < fileList.length; i++) {
			Matcher matcher = pattern.matcher(fileList[i].getAbsolutePath());
			if (matcher.matches()) {
				FilePath.add(fileList[i]);
				count ++;
			}
		}
		if(count==8){
			DBUtil.notice("8个天文件均已经收到正常FS反馈");
		}
		System.out.println("count=" + count);
		return FilePath;
	}

}
