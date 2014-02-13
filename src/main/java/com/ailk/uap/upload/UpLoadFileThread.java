package com.ailk.uap.upload;

import com.ailk.jt.util.DBUtil;
import com.ailk.uap.config.PropertiesUtil;
import com.ailk.uap.dbconn.ConnectionManager;
import com.asiainfo.uap.util.des.EncryptInterface;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.log4j.Logger;

public class UpLoadFileThread implements Runnable {
	private static final Logger log = Logger.getLogger(UpLoadFileThread.class);
	private static String file_upload_delay;
	private static String uap_file_uapload;
	private static String uap_file_upload_bak;
	private static String bomc_file_upload_dir;
	private static String ftp_server_ip;
	private static String ftp_server_port;
	private static String ftp_server_username;
	private static String ftp_server_password;
	private static FTPClient ftpClientInstance;
	private static Connection conn;

	public static void readConfig() {
		file_upload_delay = PropertiesUtil.getValue("file_upload_delay").trim();
		uap_file_uapload = PropertiesUtil.getValue("uap_file_uapload").trim();
		uap_file_upload_bak = PropertiesUtil.getValue("uap_file_upload_bak")
				.trim();

		bomc_file_upload_dir = PropertiesUtil.getValue("bomc_file_upload_dir")
				.trim();

		ftp_server_ip = PropertiesUtil.getValue("ftp_server_ip").trim();
		ftp_server_port = PropertiesUtil.getValue("ftp_server_port").trim();
		ftp_server_username = PropertiesUtil.getValue("ftp_server_username")
				.trim();

		ftp_server_password = PropertiesUtil.getValue("ftp_server_password")
				.trim();
	}

	public static void main(String[] args) {
		readConfig();

		Thread t = new Thread(new UpLoadFileThread());
		t.start();
	}

	public void run() {
		while (true)
			try {
				File file = new File(uap_file_uapload);
				File[] uploadFileList = file
						.listFiles(new FileNameAccept("xml"));
				log.info(uploadFileList.length + " file to upload");
				log.info("upload file count =" + uploadFileList.length);
				int uploadFileLength = uploadFileList.length;
				if (uploadFileLength > 0) {
					conn = DBUtil.getAiuap20Connection();
					ftpClientInstance = getFtpClientInstance();
					int i = 0;
					if (i < uploadFileLength) {
						if (uploadFileList[i].isFile()) {
							FileInputStream fis = new FileInputStream(
									uploadFileList[i]);

							ftpClientInstance
									.changeWorkingDirectory(bomc_file_upload_dir);

							ftpClientInstance.storeFile(uploadFileList[i]
									.getName(), fis);

							fis.close();

							updateUploadFileInfo(uploadFileList[i].getName(),
									conn);

							log.info("upload  file  "
									+ uploadFileList[i].getName());

							BufferedInputStream in = new BufferedInputStream(
									new FileInputStream(uploadFileList[i]));

							File uapLoadFileBak = new File(uap_file_upload_bak
									+ "/" + uploadFileList[i].getName());

							if (!uapLoadFileBak.exists()) {
								uapLoadFileBak.createNewFile();
							}
							BufferedOutputStream out = new BufferedOutputStream(
									new FileOutputStream(uapLoadFileBak));

							Streams.copy(in, out, true);
							in.close();
							out.close();

							uploadFileList[i].delete();
						}
						i++;
						continue;
					}

				}

				Thread.sleep(Integer.parseInt(file_upload_delay.trim()) * 1000);
				log.info("Thread sleep file_upload_delay" + file_upload_delay);
			} catch (NumberFormatException e) {
				log.error("NumberFormatException:" + e.getMessage());
			} catch (InterruptedException e) {
				log.error("InterruptedException:" + e.getMessage());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				log.error("FileNotFoundException e " + e.getMessage());
			} catch (IOException e) {
				e.printStackTrace();
				log.error("IOException e " + e.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
				log.error("Exception e " + e.getMessage());
			} finally {
				try {
					if ((ftpClientInstance == null)
							|| (!ftpClientInstance.isConnected()))
						continue;
					ftpClientInstance.logout();
					ftpClientInstance.disconnect();
					log.info("ftpClientInstance disconnect");
				} catch (IOException e) {
					e.printStackTrace();
					log.warn("ftpClientInstance disconnect error!!");
					log.error("IOException e" + e.getMessage());
				}
				try {
					if ((conn != null) && (!conn.isClosed())) {
						ConnectionManager.closeConnection(conn);
						log
								.info("ConnectionManager.closeConnection***********");
					}

				} catch (SQLException e) {
					log.error("SQLException e" + e.getMessage());
					e.printStackTrace();
				}
			}
	}

	public static FTPClient getFtpClientInstance() throws IOException {
		log
				.info("getFtpClientConnection******preparing to connect FTP Server **********");

		FTPClient ftpClient = new FTPClient();
		String password = EncryptInterface
				.desUnEncryptData(ftp_server_password);

		log.info("FTPClientConfig.SYST_NT====WINDOWS");
		log.info("ftp_server_ip======" + ftp_server_ip);
		log.info("ftp_server_port=====" + ftp_server_port);
		ftpClient.setControlEncoding("GBK");
		FTPClientConfig conf = new FTPClientConfig("WINDOWS");
		conf.setServerLanguageCode("zh");
		ftpClient.configure(conf);
		log.info("start to connect FTP ip: [" + ftp_server_ip + "][port:"
				+ ftp_server_port + "]");

		ftpClient.connect(ftp_server_ip, Integer.parseInt(ftp_server_port));
		log.info("connected FTP ip: [" + ftp_server_ip + "][port:"
				+ ftp_server_port + "]");

		log.info("ftp_server_username====" + ftp_server_username);
		log.info("start to login FTP ip: [" + ftp_server_ip + "][port:"
				+ ftp_server_port + "]");

		ftpClient.login(ftp_server_username, password);
		log.info(" logined FTP ip: [" + ftp_server_ip + "][port:"
				+ ftp_server_port + "]");

		return ftpClient;
	}

	private static void updateUploadFileInfo(String uploadFileName,
			Connection conn) throws Exception {
		String sql = "update dr_upload_file_info t set t.upload_status ='1' where t.file_name ='"
				+ uploadFileName + "'";

		log.info(sql);
		PreparedStatement prepStmt = conn.prepareStatement(sql);
		prepStmt.executeUpdate();

		ConnectionManager.closePrepStmt(prepStmt);
	}
}