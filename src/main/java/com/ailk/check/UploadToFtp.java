package com.ailk.check;

import com.ailk.check.utils.FileUtil;
import com.ailk.uap.config.PropertiesUtil;
import com.asiainfo.uap.util.des.EncryptInterface;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketException;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: wangpu
 * Date: 13-7-17
 * Time: 下午2:38
 * <p/>
 * 上传文件到FTP
 */
public class UploadToFtp {
    private static Logger logger = LoggerFactory.getLogger(UploadToFtp.class);

    public static void upload(Collection<File> uploadFileList, String bakPath) {
        String ftp_server_ip = PropertiesUtil.getValue("ftp_server_ip").trim();
        String ftp_server_port = PropertiesUtil.getValue("ftp_server_port").trim();
        String ftp_server_username = PropertiesUtil.getValue("ftp_server_username").trim();
        String ftp_server_password = PropertiesUtil.getValue("ftp_server_password").trim();
        String bomc_file_upload_dir = PropertiesUtil.getValue("bomc_file_upload_dir").trim();


        logger.info("getFtpClientConnection******preparing to connect FTP Server **********");

        FTPClient ftpClient = new FTPClient();
        // 设置连接超时时间为30秒
        ftpClient.setConnectTimeout(30000);
        String password = EncryptInterface.desUnEncryptData(ftp_server_password);
        ftpClient.setControlEncoding("GBK");
        FTPClientConfig conf = new FTPClientConfig("WINDOWS");
        conf.setServerLanguageCode("zh");
        ftpClient.configure(conf);

        try {
            ftpClient.connect(ftp_server_ip, Integer.parseInt(ftp_server_port));
            // 设置Socket超时时间为30秒
            ftpClient.setSoTimeout(30000);
            ftpClient.login(ftp_server_username, password);
            logger.info("login FTP ip: [" + ftp_server_ip + "][port:" + ftp_server_port + "]");
            ftpClient.changeWorkingDirectory(bomc_file_upload_dir);
            logger.info("change working directory to : " + bomc_file_upload_dir);

            for (File file : uploadFileList) {
                FileInputStream fis = new FileInputStream(file);
                boolean result = false;
                try {
                    result = ftpClient.storeFile(file.getName(), fis);
                    logger.info("upload result : " + result + ", file name : " + file.getName());
                } finally {
                    fis.close();
                }

                if (result) {
                    File hourBakDir = new File(bakPath);
                    FileUtil.moveToDir(file, hourBakDir);
                    logger.info("bak success!");
                }
            }
        } catch (FileNotFoundException e) {
            logger.error("UploadToFtp have FileNotFoundException", e);
        } catch (SocketException e) {
            logger.error("UploadToFtp have SocketException", e);
        } catch (IOException e) {
            logger.error("UploadToFtp have IOException", e);
        } catch (Exception e) {
            logger.error("UploadToFtp have Exception", e);
        } finally {
            try {
                if ((ftpClient.isConnected())) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
                logger.info("ftpClientInstance disconnect");
            } catch (IOException e) {
                logger.error("ftpClientInstance disconnect error!", e);
            }
        }
    }
}
