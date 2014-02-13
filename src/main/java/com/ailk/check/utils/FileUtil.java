package com.ailk.check.utils;

import com.ailk.check.ConfigReader;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: wangpu
 * Date: 13-5-8
 * Time: 下午4:00
 * <p/>
 * 文件操作工具类
 */
public class FileUtil {

    /**
     * 搬移文件到指定目录
     *
     * @param filePath 文件路径
     * @param dirPath  目录路径
     * @throws IOException
     */
    public static void moveToDir(String filePath, String dirPath) throws IOException {
        File file = new File(filePath);
        File dir = new File(dirPath);
        FileUtil.moveToDir(file, dir);
    }

    /**
     * 搬移文件到指定目录
     *
     * @param file 要搬移的文件
     * @param dir  指定目录
     * @throws IOException
     */
    public static void moveToDir(File file, File dir) throws IOException {
        FileUtils.copyFileToDirectory(file, dir);
        FileUtils.forceDelete(file);
    }

    /**
     * 获取文件路径描述的所在目录路径
     *
     * @param filePath 文件路径
     * @return 描述的所在目录路径
     */
    public static String getDirFromFile(String filePath) {
        return filePath.substring(0, filePath.lastIndexOf(ConfigReader.getFileSeparator()));
    }
}
