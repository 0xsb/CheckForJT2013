package com.ailk.check.safeguard.validate;

import com.ailk.check.safeguard.validate.xml.XmlReader;
import com.ailk.check.utils.FileUtil;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: wangpu
 * Date: 13-5-31
 * Time: 下午3:21
 * <p/>
 * 金库申请、金库审批文件验证
 */
public class JKDayValidator extends DayValidator {
    private static Logger logger = LoggerFactory.getLogger(JKDayValidator.class);

    private String jkrPath; // SMJKR 文件路径
    private String jkaPath; // SMJKA 文件路径

    public JKDayValidator(int year, int month, int day, String jkrPath, String jkaPath) {
        super(year, month, day);
        this.jkrPath = jkrPath;
        this.jkaPath = jkaPath;
    }

    /**
     * 金库申请与审批XML文件验证
     */
    public boolean validate() {
        boolean result = false;

        try {
            /** 1、检查SMJKR、SMJKA是否生成了XML文件 **/
            if (filesIsExists(jkrPath, jkaPath)) {
                /** 2、验证SMJKR、SMJKA文件是否符合xsd **/
                Map<String, String> xmlPathMaps = new HashMap<String, String>();
                xmlPathMaps.put(jkrPath, FileUtil.getDirFromFile(jkrPath));
                xmlPathMaps.put(jkaPath, FileUtil.getDirFromFile(jkaPath));
                if (xmlIsValidated(xmlPathMaps)) {
                    /** 3、检查两个文件sum是否等于seq总数 **/
                    XmlReader jkrXmlReader = new XmlReader(jkrPath);
                    XmlReader jkaXmlReader = new XmlReader(jkaPath);
                    if (sumIsEqualsSeq(jkrXmlReader, jkaXmlReader)) {
                        /** 4、验证XML的头部日期是否正确 **/
                        if (timeIsValidated(jkrXmlReader, jkaXmlReader)) {
                            /** 5、开始两个XML联合校验 **/
                            if (validateBetweenSMJK(jkrXmlReader, jkaXmlReader)) {
                                result = true;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("JKDayValidator.validate have Exception!", e);
            result = false;
        }

        return result;
    }

    /**
     * 联合SMJKR和SMJKA进行校验
     * 1、校验SMJKR和SMJKA数据量是否相等
     * 2、校验SMJKR中的开始时间和结束时间是否在6小时内
     * 3、校验SMJKR中时间跨度超过两小时的占总数的百分比是否大于10%
     * 4、校验SMJKR的申请ID是否都包含在SMJKA中
     *
     * @param jkrXmlReader SMJKR XML
     * @param jkaXmlReader SMJKA XML
     * @return boolean
     * @throws ParseException
     */
    private boolean validateBetweenSMJK(XmlReader jkrXmlReader, XmlReader jkaXmlReader) throws ParseException {
        Document docSMJKR = jkrXmlReader.getDocument();
        Document docSMJKA = jkaXmlReader.getDocument();
        int sumSMJKR = jkrXmlReader.getSumVal();
        int sumSMJKA = jkaXmlReader.getSumVal();

        // 1、校验SMJKR和SMJKA数据量是否相等
        if (sumSMJKR == 0) {
            logger.error("SMJKR sum is 0!");
            takeErrorResult(jkrXmlReader.getXmlName(), jkrXmlReader.getXmlPath(), "SmjkrSumIs0", 0);
            return false;
        }
        if (sumSMJKA == 0) {
            logger.error("SMJKA sum is 0!");
            takeErrorResult(jkaXmlReader.getXmlName(), jkaXmlReader.getXmlPath(), "SmjkaSumIs0", 0);
            return false;
        }
        if (sumSMJKR != sumSMJKA) {
            logger.error("validate between SMJKR and SMJKA found sumSMJKR != sumSMJKA!");
            takeErrorResult(jkrXmlReader.getXmlName(), jkrXmlReader.getXmlPath(), "SmjkrSmjkaSumNotEquals", sumSMJKR);
            return false;
        }

        Set<String> requestIdSetSMJKA = new HashSet<String>();
        List requestIdListSMJKA = docSMJKA.selectNodes("/smp/data/rcd/requestid");
        for (Object o : requestIdListSMJKA) {
            Element idElement = (Element) o;
            String id = idElement.getText();
            requestIdSetSMJKA.add(id);
        }

        Set<String> requestIdSetSMJKR = new HashSet<String>();
        List rcdListSMJKR = docSMJKR.selectNodes("/smp/data/rcd");
        int count = 0;
        for (Object o : rcdListSMJKR) {
            Element rcdElement = (Element) o;
            Element beginTime = rcdElement.element("begintime");
            Element endTime = rcdElement.element("endtime");
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date begin = df.parse(beginTime.getText().replace("T", " "));
            Date end = df.parse(endTime.getText().replace("T", " "));

            Element idElement = rcdElement.element("requestid");
            String id = idElement.getText();
            requestIdSetSMJKR.add(id);

            // 2、校验SMJKR中的开始时间和结束时间是否在6小时内
            if ((end.getTime() - begin.getTime()) > 21600000) {
                logger.error("validate between SMJKR and SMJKA found endtime - begintime > 6 hour! requestid is : " + id);
                takeErrorResult(jkrXmlReader.getXmlName(), jkrXmlReader.getXmlPath(), "SmjkrEndtimeBegintimeThanSix", sumSMJKR);
                return false;
            }
            if ((end.getTime() - begin.getTime()) <0) {
                logger.error("validate between SMJKR and SMJKA found endtime - begintime < 0 hour! requestid is : " + id);
                takeErrorResult(jkrXmlReader.getXmlName(), jkrXmlReader.getXmlPath(), "validateError", sumSMJKR);
                return false;
            }
            //3、校验SMJKR中时间跨度超过两小时的占总数的百分比是否大于10%
            if ((end.getTime() - begin.getTime()) > 7200000) {
                count = count + 1;
            }


            // 4、校验SMJKR的申请ID是否都包含在SMJKA中
            if (!requestIdSetSMJKA.contains(id)) {
                logger.error("validate between SMJKR and SMJKA found requestid not in SMJKA! requestid is : " + id);
                takeErrorResult(jkrXmlReader.getXmlName(), jkrXmlReader.getXmlPath(), "SmjkrRequestidNotInSmjka", sumSMJKR);
                return false;
            }
        }

        for (String idJKA : requestIdSetSMJKA) {
            if (!requestIdSetSMJKR.contains(idJKA)) {
                logger.error("validate between SMJKR and SMJKA found requestid not in SMJKR! requestid is : " + idJKA);
                takeErrorResult(jkrXmlReader.getXmlName(), jkrXmlReader.getXmlPath(), "SmjkrRequestidNotInSmjka", sumSMJKR);
                return false;
            }
        }

        float ratio = count / (float) sumSMJKR;
        logger.info("Smjkr Endtime Begintime Than Two ratio is : " + ratio);
        if (ratio > 0.1) {
            logger.error("validate SMJKR  endtime - begintime > 2 hour over total 10%! ratio is : " + ratio);
            takeErrorResult(jkrXmlReader.getXmlName(), jkrXmlReader.getXmlPath(), "SmjkrEndtimeBegintimeThanTwoOverTen", sumSMJKR);
            return false;
        }
        return true;
    }
}
