package com.ailk.check;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: wangpu
 * Date: 13-5-24
 * Time: 上午9:35
 *
 * 配置读取者
 */
public class ConfigReader {
    private static Logger logger = LoggerFactory.getLogger(ConfigReader.class);

    private static Properties makeFile = new Properties();

    public static String makeFileName = "/makeFile.properties";

    static {
        try {
            makeFile.load(ConfigReader.class.getResourceAsStream(makeFileName));
        } catch (IOException e) {
            logger.error("Error get system config file ---> makeFile.properties", e);
        }
    }

    /**
     * 获取生成上报文件配置指定属性值
     *
     * @param name 属性名
     * @return 属性值
     */
    public static String getMakeFileValue(String name) {
        return makeFile.getProperty(name);
    }

    /**
     * 获取随机安全文件模板存放目录路径
     *
     * @param fileType 类型
     * @return 随机安全文件模板存放目录路径
     */
    public static String getRandomSafeFileTemplateDir(FileType fileType) {
        return getMakeFileValue("random_safe_template_for_" + fileType.name());
    }

    /**
     * 获取生成文件存放目录路径
     *
     * @param fileType 类型
     * @return 生成文件存放目录路径
     */
    public static String getFileGenerateDir(FileType fileType) {
        return getMakeFileValue("uap_file_generate_for_" + fileType.name());
    }

    /**
     * 获取上传目录
     *
     * @return 上传目录路径
     */
    public static String getUploadDir() {
        return getMakeFileValue("uap_file_upload");
    }

    /**
     * 获取自检验证失败的文件存放目录
     *
     * @return 自检验证失败的文件存放目录
     */
    public static String getValidateErrorDir() {
        return getMakeFileValue("validate_error_dir");
    }

    /**
     * 获取文件分割符
     *
     * @return File Separator
     */
    public static String getFileSeparator() {
        return getMakeFileValue("os_flag");
    }

    /**
     * 获取省份编码
     *
     * @return ProvCode
     */
    public static String getProvCode() {
        return getMakeFileValue("prov_code");
    }
}
