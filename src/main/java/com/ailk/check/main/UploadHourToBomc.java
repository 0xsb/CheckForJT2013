package com.ailk.check.main;

import com.ailk.check.UploadToFtp;
import com.ailk.uap.config.PropertiesUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: wangpu
 * Date: 13-7-17
 * Time: 下午3:22
 * <p/>
 * 上传小时文件到BOMC
 */
public class UploadHourToBomc {
    private static Logger logger = LoggerFactory.getLogger(UploadHourToBomc.class);

    public static void main(String[] args) {
        logger.info("upload hour to BOMC is running ...");

        String uap_file_uapload_hour = PropertiesUtil.getValue("uap_file_uapload_hour").trim();
        File hourUploadDir = new File(uap_file_uapload_hour);
        Collection<File> files = FileUtils.listFiles(hourUploadDir,
                FileFilterUtils.suffixFileFilter(".xml"),
                DirectoryFileFilter.INSTANCE);

        if (files != null && files.size() > 0) {
            logger.info("find hour xml : " + files.size());
            String uap_file_upload_bak_hour = PropertiesUtil.getValue("uap_file_upload_bak_hour").trim();
            UploadToFtp.upload(files, uap_file_upload_bak_hour);
        }
    }
}
