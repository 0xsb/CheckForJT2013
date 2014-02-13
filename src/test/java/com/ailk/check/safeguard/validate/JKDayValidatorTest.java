package com.ailk.check.safeguard.validate;

import com.ailk.check.FileData;
import com.ailk.check.FileType;
import com.ailk.check.safeguard.validate.xml.XmlReader;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: wangpu
 * Date: 13-6-3
 * Time: 下午2:35
 */
public class JKDayValidatorTest {
    private int year = 2013;
    private int month = 3;
    private int day = 7;

    private String pathSMJKR;
    private String pathSMJKA;

    @Before
    public void setUp() throws IOException {
        this.pathSMJKR = FileType.SMJKR.generateXmlPath(FileType.SMJKR.getFileName(year, month, day, 0));
        this.pathSMJKA = FileType.SMJKA.generateXmlPath(FileType.SMJKA.getFileName(year, month, day, 0));
        FileData.jkDayFile(year, month, day, pathSMJKR, pathSMJKA);
    }

    /**
     * 正常流程测试
     */
    @Test
    public void testValidate() {
        JKDayValidator jkDayValidator = new JKDayValidator(year, month, day, pathSMJKR, pathSMJKA);
        boolean result = jkDayValidator.validate();
        Assert.assertTrue(result);
        Assert.assertEquals("no error.", jkDayValidator.getErrorCode());
    }

    /**
     * 异常流程测试：开始时间 至 结束时间 大于6小时
     *
     * @throws org.dom4j.DocumentException
     * @throws IOException
     */
    @Test
    public void testValidateSixHourError() throws DocumentException, IOException {
        // 准备错误数据
        XmlReader jkrXmlReader = new XmlReader(this.pathSMJKR);
        Element element = jkrXmlReader.getUniqueElement("/smp/data/rcd/endtime");
        element.setText("2013-04-07T16:23:28");
        jkrXmlReader.touch(this.pathSMJKR);

        JKDayValidator jkDayValidator = new JKDayValidator(year, month, day, pathSMJKR, pathSMJKA);
        boolean result = jkDayValidator.validate();
        Assert.assertFalse(result);
        Assert.assertEquals("SmjkrEndtimeBegintimeThanSix", jkDayValidator.getErrorCode());
        Assert.assertEquals(this.pathSMJKR, jkDayValidator.getErrorFilePath());
    }

    /**
     * 异常流程测试：开始时间 至 结束时间 大于2小时 超过10%
     *
     * @throws DocumentException
     * @throws IOException
     */
    @Test
    public void testValidateThanTwoOverTenError() throws DocumentException, IOException, ParseException {
        // 准备错误数据
        XmlReader jkrXmlReader = new XmlReader(this.pathSMJKR);
        List beginList = jkrXmlReader.getElements("/smp/data/rcd/begintime");
        List endList = jkrXmlReader.getElements("/smp/data/rcd/endtime");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (int i = 0; i < 464; i++) {
            Element begin = (Element) beginList.get(i);
            Date beginDate = df.parse(begin.getText().replace("T", " "));
            Element end = (Element) endList.get(i);
            Date endDate = new Date();
            endDate.setTime(beginDate.getTime() + 7201000);
            end.setText(df.format(endDate).replace(" ", "T"));
        }
        jkrXmlReader.touch(this.pathSMJKR);

        JKDayValidator jkDayValidator = new JKDayValidator(year, month, day, pathSMJKR, pathSMJKA);
        boolean result = jkDayValidator.validate();
        Assert.assertFalse(result);
        Assert.assertEquals("SmjkrEndtimeBegintimeThanTwoOverTen", jkDayValidator.getErrorCode());
        Assert.assertEquals(this.pathSMJKR, jkDayValidator.getErrorFilePath());
    }
}
