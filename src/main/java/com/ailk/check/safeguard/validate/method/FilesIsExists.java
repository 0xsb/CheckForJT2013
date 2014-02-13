package com.ailk.check.safeguard.validate.method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: wangpu
 * Date: 13-5-31
 * Time: 下午4:23
 * <p/>
 * 检查文件是否存在
 * 如果有一个不存在就返回false
 */
public class FilesIsExists {
    private static Logger logger = LoggerFactory.getLogger(FilesIsExists.class);

    private String[] paths;
    private boolean exists = true;
    private String notExistsFileName;
    private String notExistsFilePath;

    public FilesIsExists(String... paths) {
        this.paths = paths;
    }

    public boolean isExists() {
        return this.exists;
    }

    public String getNotExistsFileName() {
        return notExistsFileName;
    }

    public String getNotExistsFilePath() {
        return notExistsFilePath;
    }

    public FilesIsExists invoke() {
        for (String path : paths) {
            File file = new File(path);
            if (!file.exists()) {
                logger.error("xml file not exists, xml is : " + path);
                this.exists = false;
                this.notExistsFileName = file.getName();
                this.notExistsFilePath = path;
                break;
            }
        }
        return this;
    }
}
