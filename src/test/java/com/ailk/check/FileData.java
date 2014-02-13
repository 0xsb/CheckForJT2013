package com.ailk.check;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: wangpu
 * Date: 13-6-8
 * Time: 上午9:56
 *
 * 为单元测试准备数据
 */
public class FileData {

    public static void jkDayFile(int year, int month, int day, String pathSMJKR, String pathSMJKA) throws IOException {
        String testSMJKR = "/testXml/SMJKR_371_01DY_20130407_000_000.xml";
        String testSMJKA = "/testXml/SMJKA_371_01DY_20130407_000_000.xml";
        FileUtils.copyFile(new File(FileData.class.getResource(testSMJKR).getFile()), new File(pathSMJKR));
        FileUtils.copyFile(new File(FileData.class.getResource(testSMJKA).getFile()), new File(pathSMJKA));
    }
}
