package com.ailk.check;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: wangpu
 * Date: 13-6-6
 * Time: 上午9:44
 *
 * 枚举类测试
 */
public class FileTypeTest {

    @Test
    public void testType() {
        Assert.assertEquals("SMJKA_371_01DY_20120321_000_000.xml", FileType.SMJKA.getFileName(2012, 2, 21, 0));
    }
}
