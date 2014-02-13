package com.ailk.check.safeguard.safe.random.safe;

import com.ailk.check.FileType;
import com.ailk.check.safeguard.validate.JKDayValidator;
import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: wangpu
 * Date: 13-5-23
 * Time: 下午3:33
 */
public class JKDayRandomSafeFileCreatorTest {

    @Test
    public void createSafeFileTest() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = 2;

        JKDayRandomSafeFileCreator jkDayRandomSafeFileCreator = new JKDayRandomSafeFileCreator(year, month, day);
        jkDayRandomSafeFileCreator.createJKSafeFile();
        Assert.assertTrue(jkDayRandomSafeFileCreator.isGenerateJKSuccess());

        // 加入校验
        String pathSMJKR = FileType.SMJKR.generateXmlPath(FileType.SMJKR.getFileName(year, month, day, 0));
        String pathSMJKA = FileType.SMJKA.generateXmlPath(FileType.SMJKA.getFileName(year, month, day, 0));

        JKDayValidator jkDayValidator = new JKDayValidator(year, month, day, pathSMJKR, pathSMJKA);
        boolean result = jkDayValidator.validate();
        Assert.assertTrue(result);
        Assert.assertEquals("no error.", jkDayValidator.getErrorCode());
    }
}
