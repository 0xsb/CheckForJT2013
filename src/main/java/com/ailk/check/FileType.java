package com.ailk.check;

import com.ailk.check.utils.CalendarUtil;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: wangpu
 * Date: 13-6-6
 * Time: 上午9:11
 * <p/>
 * 上报文件类型
 */
public enum FileType {
    // 文件类型
    SMMAF(TimeType.Month), SMJKR(TimeType.Day), SMJKA(TimeType.Day);

    /**
     * 时间类型
     */
    public enum TimeType {
        Hour, Day, Month
    }

    private TimeType timeType;

    private FileType(TimeType timeType) {
        setTimeType(timeType);
    }

    private void setTimeType(TimeType timeType) {
        this.timeType = timeType;
    }

    public TimeType getTimeType() {
        return this.timeType;
    }

    /**
     * 根据年月日小时获取结束时间
     *
     * @param year  年
     * @param month 月
     * @param day   日
     * @param hour  小时
     * @return 结束时间
     */
    public Date getEndTime(int year, int month, int day, int hour) {
        Date date = null;
        switch (this.getTimeType()) {
            case Day:
                date = CalendarUtil.getNextDayFromNumber(year, month, day).getTime();
                break;
            case Month:
                date = CalendarUtil.getFirstDayNextMonthFromNumber(year, month, day).getTime();
                break;
            case Hour:
                // todo 没有完成请继续...
                break;
        }
        return date;
    }

    /**
     * 获取随机安全文件模板路径
     * 目前只有SMJKR、SMJKA使用
     *
     * @param day 日期中的天
     * @return 随机安全文件模板路径
     */
    public String randomSafeTemplatePath(int day) {
        return ConfigReader.getRandomSafeFileTemplateDir(this) + ConfigReader.getFileSeparator() + name() + "_" + day + ".xml";
    }

    /**
     * 获取生成XML目录路径
     *
     * @return 生成XML目录路径
     */
    public String generateXmlDirPath() {
        return ConfigReader.getFileGenerateDir(this);
    }

    /**
     * 获取生成XML路径
     *
     * @param fileName 文件名称
     * @return 生成XML路径
     */
    public String generateXmlPath(String fileName) {
        return ConfigReader.getFileGenerateDir(this) + ConfigReader.getFileSeparator() + fileName;
    }

    /**
     * 根据年月日小时获取文件名称
     *
     * @param year  年
     * @param month 月
     * @param day   日
     * @param hour  小时
     * @return 文件名称
     */
    public String getFileName(int year, int month, int day, int hour) {
        String name = "";
        switch (this.getTimeType()) {
            // 日文件名称获取
            case Day:
                name = this.name() // 类型
                        + getProvCode() // 省份
                        + getDayFileName() // 日文件
                        + getDateName(year, month, day) // 日期
                        + getDayOrMonthSequence() // 序列
                        + getReloadNum() // 重传
                        + getXmlSuffix(); // 后缀
                break;
            // 月文件名称获取
            case Month:
                name = this.name() // 类型
                        + getProvCode() // 省份
                        + getMonthFileName() // 月文件
                        + getDateName(year, month, day) // 日期
                        + getDayOrMonthSequence() // 序列
                        + getReloadNum() // 重传
                        + getXmlSuffix(); // 后缀
                break;
            // todo 没有完成请继续...
            case Hour:
                break;
        }
        return name;
    }

    // 省份名称
    private String getProvCode() {
        return "_" + ConfigReader.getProvCode();
    }

    // 日文件名称
    private String getDayFileName() {
        return "_01DY";
    }

    // 月文件名称
    private String getMonthFileName() {
        return "_01MO";
    }

    // 日期名称
    private String getDateName(int year, int month, int day) {
        return "_" + CalendarUtil.getAtStringDateFromNumber(year, month, day);
    }

    // 日月序号
    private String getDayOrMonthSequence() {
        return "_000";
    }

    // 重传号
    private String getReloadNum() {
        // todo 需要细化
        return "_000";
    }

    // XML文件后缀
    private String getXmlSuffix() {
        return ".xml";
    }
}
