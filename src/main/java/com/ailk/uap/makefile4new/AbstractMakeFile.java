package com.ailk.uap.makefile4new;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;

import org.apache.log4j.Logger;

import com.ailk.jt.util.DBUtil;
import com.ailk.uap.config.PropertiesUtil;
import com.ailk.uap.dbconn.ConnectionManager;
import com.ailk.uap.entity.DrUploadFileInfo;
import com.ailk.uap.util.CommonUtil;
/**
 * 抽象文件创建公共基类
 * 		基于模板模式建造公共文件基类,公共文件创建流程相同，具体实现
 * 细节不同.
 * @date 2013-12-25
 * @author liujie 
 * 		   liujie_09_24@163.com
 * 
 * @version 4.0
 * @since 4.0
 * @see com.ailk.uap.makefile4new.AbstractMakeFile
 */
public abstract class AbstractMakeFile {
	protected static final Logger log = Logger.getLogger(AbstractMakeFile.class);
	protected static String uap_file_uapload;
	protected static String uap_file_uapload_temp;
	protected static String uap_version;
	protected static  String type = "SMSMF";
	protected static String prov_code;
	protected static final String intval = "01DY";
	protected static String createTime;
	protected static String beginTime;
	protected static String beginTimeWithT;
	protected static String endTimeWithT;
	protected static String endTime;
	protected static String uploadFileName;
	protected static String reloadFlag = "0";
	protected static long sum;
	protected static Connection conn;
	protected static OutputStreamWriter output;
	protected static FileOutputStream fos;
	protected static BufferedWriter bw;
	protected static File uapLoadTempFile;
	protected static String getFileNameDate;//上传文件名中的日期
	protected static String uploadFileDirInPath;//上传文件目录中的文件类型
	
	
	public  void readConfig() {
		uap_file_uapload = PropertiesUtil.getValue("uap_file_uapload_for_"+ getUploadFileDirInPath()  + "_db_now");
		uap_version = PropertiesUtil.getValue("uap_version");
		prov_code = PropertiesUtil.getValue("prov_code");
		uap_file_uapload_temp = PropertiesUtil.getValue("uap_file_uapload_temp");
		type = getFileType();
	}
	
	public   void makeFile() {
		readConfig();
		log.info("UpLoadFileThread start to run......");
		log.info("uap_file_uapload==" + AbstractMakeMonthFile.uap_file_uapload);
		log.info("uap_version==" + uap_version);
		long statisticRunStartTime = System.currentTimeMillis();
		log.info(this.getClass().getName() + " begin ...");
		try {
			conn = getConn();
			createTime =getCreateTime();
			beginTime = getBeginTime();
			beginTimeWithT = getBeginTimeWithT();
			endTime = getEndTime();
			endTimeWithT = getEndTimeWithT();

			uploadFileName = getFileType() +"_" + prov_code + "_" + getIntval() + "_"
					+ getFileNameDate() + "_" + "000" + "_" + "000.xml";

			log.info(this.getClass().getName() + " ******Start***************");
			generateMakeFile();
			log.info(this.getClass().getName() + "  ******End ***************");

			log.info("DR_UPLOAD_FILE_INFO**********insert ********Start*********");
			DrUploadFileInfo fileInfo = new DrUploadFileInfo();
			fileInfo.setFileName(uploadFileName);
			fileInfo.setFileSeq("000");
			fileInfo.setIntval(getIntval());
			fileInfo.setProv(prov_code);
			fileInfo.setReloadFlag(reloadFlag);
			fileInfo.setTotal(Long.valueOf(sum));
			fileInfo.setType(getFileType());
			fileInfo.setUploadStatus("0");

			CommonUtil.insertUploadFileInfo(fileInfo, conn, getBeginTime(), getEndTime());
			log.info("DR_UPLOAD_FILE_INFO**********insert ********End*********");
			long statisticRunEndTime = System.currentTimeMillis();
			log.info("GENERATE "+ this.getClass().getName() + " FULL FILE  TOTALTIME======"
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
		}
	}
	
	protected static void writeFileBufferToTempFile(StringBuffer stringBuffer) throws IOException {
		bw.write(stringBuffer.toString());
		bw.flush();
		output.flush();
		fos.flush();
	}
	
	public abstract  String getCreateTime() throws Exception ;
	public abstract String getBeginTime() throws Exception ;
	public abstract String getBeginTimeWithT() throws Exception ;
	public abstract String getEndTimeWithT() throws Exception;
	public abstract String getEndTime() throws Exception;
	public abstract String getFileNameDate()  throws Exception ;
	
	public abstract String getIntval() ;
	public abstract  String getFileType();
	public abstract void generateMakeFile()  throws Exception;
	
	protected  Connection getConn() {
		return DBUtil.getAiuap20Connection();
	}

	public  String getUploadFileDirInPath() {
		return getFileType().toLowerCase();
	}
	
}
