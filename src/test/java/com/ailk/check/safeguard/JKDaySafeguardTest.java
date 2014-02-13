package com.ailk.check.safeguard;

import com.ailk.check.FileData;
import com.ailk.check.FileType;
import com.ailk.check.safeguard.validate.xml.XmlReader;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: wangpu
 * Date: 13-6-7
 * Time: 下午5:46
 */
public class JKDaySafeguardTest {
    private int year = 2013;
    private int month = 3;
    private int day = 7;

    private String pathSMJKR;

    @Before
    public void setUp() throws IOException {
        this.pathSMJKR = FileType.SMJKR.generateXmlPath(FileType.SMJKR.getFileName(year, month, day, 0));
        String pathSMJKA = FileType.SMJKA.generateXmlPath(FileType.SMJKA.getFileName(year, month, day, 0));
        FileData.jkDayFile(year, month, day, pathSMJKR, pathSMJKA);
    }

    /**
     * 正常流程测试
     */
    @Test
    public void testJKDaySafeguard() {
        Safeguard jkDaySafeguard = new JKDaySafeguard(year, month, day);
        boolean result = jkDaySafeguard.safeguard();
        Assert.assertTrue(result);
    }

    /**
     * 异常流程测试：出现错误使用安全文件
     *
     * @throws DocumentException
     * @throws IOException
     */
    @Test
    public void testJKDaySafeguardSumError() throws DocumentException, IOException {
        // 准备错误数据
        XmlReader jkrXmlReader = new XmlReader(this.pathSMJKR);
        Element element = jkrXmlReader.getUniqueElement("/smp/sum");
        element.setText("0");
        jkrXmlReader.touch(this.pathSMJKR);

        Safeguard jkDaySafeguard = new JKDaySafeguard(year, month, day);
        boolean result = jkDaySafeguard.safeguard();
        Assert.assertTrue(result);
        Assert.assertEquals("sumNotEqualSeq", jkDaySafeguard.getValidator().getErrorCode());
        Assert.assertEquals(this.pathSMJKR, jkDaySafeguard.getValidator().getErrorFilePath());
    }
}
