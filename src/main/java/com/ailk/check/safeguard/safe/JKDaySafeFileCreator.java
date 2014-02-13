package com.ailk.check.safeguard.safe;

import com.ailk.check.safeguard.safe.random.safe.JKDayRandomSafeFileCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: wangpu
 * Date: 13-6-5
 * Time: 上午9:59
 *
 * 金库申请审批安全文件创建者
 * @see SafeFileCreator
 */
public class JKDaySafeFileCreator extends DaySafeFileCreator {
    private static Logger logger = LoggerFactory.getLogger(JKDaySafeFileCreator.class);

    private int year, month, day;

    /**
     * 传入的时间是文件中的begintime
     * 其中月从零开始不用加一
     *
     * @param year  年
     * @param month 月
     * @param day   日
     */
    public JKDaySafeFileCreator(int year, int month, int day) {
        super(year, month, day);
        this.year = year;
        this.month = month;
        this.day = day;
    }

    // 创建金库申请审批日文件随机安全文件
    public void createJKDaySafeFile() {
        // 金库申请审批日文件随机安全文件创建者
        JKDayRandomSafeFileCreator jkDayRandomSafeFileCreator = new JKDayRandomSafeFileCreator(year, month, day);
        // 创建金库安全文件
        jkDayRandomSafeFileCreator.createJKSafeFile();
        if (jkDayRandomSafeFileCreator.isGenerateJKSuccess()) {
            setCreateSuccess(true);
        } else {
            setCreateSuccess(false);
        }
    }
}
