package com.ailk.check.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: wangpu
 * Date: 13-6-6
 * Time: 下午5:58
 */
public class FileUtilTest {

    @Test
    public void testGetDirFromFile() {
        String dir = FileUtil.getDirFromFile("D:\\java\\ailk\\project\\readme.txt");
        Assert.assertEquals("D:\\java\\ailk\\project", dir);
    }
}
