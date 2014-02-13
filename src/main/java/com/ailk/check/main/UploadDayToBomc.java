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
 * Time: 下午5:26
 * <p/>
 * 上传日文件到BOMC
 */
public class UploadDayToBomc {
    private static Logger logger = LoggerFactory.getLogger(UploadDayToBomc.class);

    public static void main(String[] args) {
        logger.info("upload day to BOMC is running ...");

        String uap_file_uapload = PropertiesUtil.getValue("uap_file_uapload").trim();
        File uploadDir = new File(uap_file_uapload);
        Collection<File> files = FileUtils.listFiles(uploadDir,
                FileFilterUtils.suffixFileFilter(".xml"),
                DirectoryFileFilter.INSTANCE);

        if (files != null && files.size() > 0) {
            logger.info("find day xml : " + files.size());
            String uap_file_upload_bak = PropertiesUtil.getValue("uap_file_upload_bak").trim();
            UploadToFtp.upload(files, uap_file_upload_bak);
        }
    }
}
