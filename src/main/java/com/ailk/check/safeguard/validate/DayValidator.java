package com.ailk.check.safeguard.validate;

import com.ailk.check.utils.CalendarUtil;
import com.ailk.check.safeguard.validate.xml.XmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: wangpu
 * Date: 13-6-3
 * Time: 下午3:41
 * <p/>
 * 日上报文件验证
 */
public abstract class DayValidator extends Validator {
    private static Logger logger = LoggerFactory.getLogger(DayValidator.class);

    private String date; // 目标日期
    private String nextDate; // 创建日期

    /**
     * 传入的时间是文件中的begintime
     * 其中月从零开始不用加一
     *
     * @param year  年
     * @param month 月
     * @param day   日
     */
    public DayValidator(int year, int month, int day) {
        this.date = CalendarUtil.getStringDateFromNumber(year, month, day);
        this.nextDate = CalendarUtil.getStringNextDateFromNumber(year, month, day);
    }

    /**
     * 验证XML的头部日期是否正确
     *
     * @param xmlReaders xml读取
     * @return 如果有一个不正确就返回false
     */
    public boolean timeIsValidated(XmlReader... xmlReaders) {
        boolean result = true;
        for (XmlReader xmlReader : xmlReaders) {
            String createTime = xmlReader.getHeadCreateTimeDate();
            if (!createTime.equals(nextDate)) {
                logger.error("head createTime is error, right should be : " + nextDate +
                        " xml is : " + xmlReader.getXmlPath());
                takeErrorResult(xmlReader.getXmlName(), xmlReader.getXmlPath(), "HeadCreateTimeIsError", xmlReader.getSumVal());
                result = false;
                break;
            }
            String beginTime = xmlReader.getHeadBeginTimeDate();
            if (!beginTime.equals(date)) {
                logger.error("head beginTime is error, right should be : " + date +
                        " xml is : " + xmlReader.getXmlPath());
                takeErrorResult(xmlReader.getXmlName(), xmlReader.getXmlPath(), "HeadBeginTimeIsError", xmlReader.getSumVal());
                result = false;
                break;
            }
            String endTime = xmlReader.getHeadEndTimeDate();
            if (!endTime.equals(nextDate)) {
                logger.error("head endTime is error, right should be : " + nextDate +
                        " xml is : " + xmlReader.getXmlPath());
                takeErrorResult(xmlReader.getXmlName(), xmlReader.getXmlPath(), "HeadEndTimeIsError", xmlReader.getSumVal());
                result = false;
                break;
            }
        }
        return result;
    }
}
