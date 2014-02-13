package com.ailk.check.main;

import com.ailk.check.safeguard.JKDaySafeguard;
import com.ailk.check.safeguard.Safeguard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: wangpu
 * Date: 13-6-13
 * Time: 下午2:11
 * <p/>
 * 金库申请审批日文件保障主方法
 */
public class JKDaySafeguardMain {
    private static Logger logger = LoggerFactory.getLogger(JKDaySafeguardMain.class);

    public static void main(String[] args) {
        // 取当前日期
        Calendar date = Calendar.getInstance();
        // 设置日期为昨天
        date.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH) - 1);
        Safeguard jkDaySafeguard = new JKDaySafeguard(
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH));

        boolean result = jkDaySafeguard.safeguard();

        logger.info("jkDaySafeguard safeguard result: " + result);
    }
}
