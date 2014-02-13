package com.ailk.check.safeguard.safe;

import com.ailk.check.utils.CalendarUtil;
import com.ailk.check.safeguard.validate.xml.XmlReader;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: wangpu
 * Date: 13-6-4
 * Time: 下午3:36
 * <p/>
 * 日安全文件创建者
 * @see SafeFileCreator
 */
public class DaySafeFileCreator extends SafeFileCreator {
    private static Logger logger = LoggerFactory.getLogger(DaySafeFileCreator.class);

    private String date; // 目标日期
    private String nextDate; // 下一天

    /**
     * 传入的时间是文件中的begintime
     * 其中月从零开始不用加一
     *
     * @param year  年
     * @param month 月
     * @param day   日
     */
    public DaySafeFileCreator(int year, int month, int day) {
        this.date = CalendarUtil.getStringDateFromNumber(year, month, day);
        this.nextDate = CalendarUtil.getStringNextDateFromNumber(year, month, day);
    }

    /**
     * 替换XML头部的日期
     * 替换关键字为：XML_REPLACE_KEY
     *
     * @param xmlReader XML
     */
    protected void replaceHeadTime(XmlReader xmlReader) {
        // 替换头部创建日期
        Element createTimeElement = xmlReader.getHeadCreateTime();
        createTimeElement.setText(createTimeElement.getText().replace(SafeFileCreator.XML_REPLACE_KEY, nextDate));
        // 替换头部开始日期
        Element beginTimeElement = xmlReader.getHeadBeginTime();
        beginTimeElement.setText(beginTimeElement.getText().replace(SafeFileCreator.XML_REPLACE_KEY, date));
        // 替换头部结束日期
        Element endTimeElement = xmlReader.getHeadEndTime();
        endTimeElement.setText(endTimeElement.getText().replace(SafeFileCreator.XML_REPLACE_KEY, nextDate));
    }

    /**
     * 替换XML头部的日期
     *
     * @param xmlReader XML
     */
    protected void replaceHeadTimeWithoutKey(XmlReader xmlReader) {
        // 替换头部创建日期
        Element createTimeElement = xmlReader.getHeadCreateTime();
        createTimeElement.setText(replaceTime(createTimeElement.getText(), nextDate));
        // 替换头部开始日期
        Element beginTimeElement = xmlReader.getHeadBeginTime();
        beginTimeElement.setText(replaceTime(beginTimeElement.getText(), date));
        // 替换头部结束日期
        Element endTimeElement = xmlReader.getHeadEndTime();
        endTimeElement.setText(replaceTime(endTimeElement.getText(), nextDate));
    }

    private String replaceTime(String time, String text) {
        String result = "";
        if (time != null && !time.equals("")) {
            String[] arr = time.split("T");
            if (arr.length > 1) {
                result = text + "T" + arr[1];
            }
        }
        return result;
    }

    // 替换头部日期，替换内容（如果有），创建所需安全文件
    public void createSafeFile(String createPath, XmlReader xmlReader, String text, String... xpaths) {
        // 替换头部时间
        replaceHeadTime(xmlReader);
        // 替换其它内容
        if (xpaths != null) {
            replace(xmlReader, text, xpaths);
        }

        try {
            xmlReader.touch(createPath);
            setCreateSuccess(true);
        } catch (IOException e) {
            logger.error("DaySafeFileCreator.createSafeFile have a IOException!", e);
            setCreateSuccess(false);
        }
    }

    // 替换头部日期，替换内容（如果有），创建所需给BOMC的静态安全文件
    public void createStaticSafeFile(String createPath, XmlReader xmlReader, String key, String text, String... xpaths) {
        // 替换头部时间
        replaceHeadTimeWithoutKey(xmlReader);
        // 替换其它内容
        if (xpaths != null) {
            replace(xmlReader, key, text, xpaths);
        }
        // 设置sum为seq的总数
        xmlReader.setSumValWithSeq();

        try {
            xmlReader.touch(createPath);
            setCreateSuccess(true);
        } catch (IOException e) {
            logger.error("DaySafeFileCreator.createStaticSafeFile have a IOException!", e);
            setCreateSuccess(false);
        }
    }

    // 给定日期
    public String getDate() {
        return date;
    }

    // 给定日期的下一天
    public String getNextDate() {
        return nextDate;
    }
}
