package com.ailk.jt.validate;

import com.ailk.check.safeguard.safe.random.safe.JKDayRandomSafeFileCreator;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.jt.util.DBUtil;
import com.ailk.jt.util.DateUtil;
import com.ailk.jt.util.PropertiesUtil;
import com.ailk.jt.util.SaveErrorFileUtil;
import com.ailk.jt.util.TimeAndOtherUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

/**
 * Created with IntelliJ IDEA.
 * User: wangpu
 * Date: 13-5-15
 * Time: 上午10:45
 * <p/>
 * 金库申请、审批XML保障 仿照原始代码进行编写，请搬移到原始代码中进行测试
 * 已被废弃请不要再使用
 * @deprecated
 */
public final class JKFileSafeguard {
    private static Logger logger = LoggerFactory.getLogger(JKFileSafeguard.class);

    // todo 短信信息
    private static Properties tran = PropertiesUtil.getProperties("/tran.properties");

    // 获取SMJKR文件生成目录
    private static final String nowPathSMJKR = PropertiesUtil.getValue("uap_file_uapload_for_smjkr_db_now");
    // 获取SMJKA文件生成目录
    private static final String nowPathSMJKA = PropertiesUtil.getValue("uap_file_uapload_for_smjka_db_now");
    // 获取上传目录
    private static final String UPLOAD_DIR_PATH = PropertiesUtil.getValue("uap_file_uapload");
    // 获取安全文件路径
    private static final String safeDayFilePath = PropertiesUtil.getValue("uap_file_uapload_for_day_dir_safe");

    private static String now_Date = "";

    // 文件分隔符
    private static final String osflag = PropertiesUtil.getValue("os_flag");

    // SMJKR文件名称
    private static String SMJKR_XML_FILE_NAME = "";
    // SMJKA文件名称
    private static String SMJKA_XML_FILE_NAME = "";

    /**
     * 执行主方法
     *
     * @param args 无
     */
    public static void main(String[] args) {
        try {
            String nowDateStr = DateUtil.formatDateyyyyMMDD(new Date());// yyyyMMdd
            now_Date = DateUtil.ymdToStr();// yyyy-MM-dd
            Calendar calendar = Calendar.getInstance();// 获取系统当前时间
            calendar.add(Calendar.DATE, -1); // 得到前一天
            Date date = calendar.getTime();
            String rightTime = DateUtil.formatDateyyyyMMDD(date);
            SMJKR_XML_FILE_NAME = "SMJKR_371_01DY_" + rightTime + "_000_000.xml";
            logger.info("SMJKR_XML_FILE_NAME = " + SMJKR_XML_FILE_NAME);
            SMJKA_XML_FILE_NAME = "SMJKA_371_01DY_" + rightTime + "_000_000.xml";
            logger.info("SMJKA_XML_FILE_NAME = " + SMJKA_XML_FILE_NAME);

            // 处理SMJKR文件和SMJKA文件
            safeGuardSMJK();
        } catch (Exception e) {
            logger.error("JKFileSafeguard Class have Exception ...", e);
        }
    }

    /**
     * 金库申请与审批XML文件验证保障
     *
     * @throws ParseException
     * @throws DatatypeConfigurationException
     * @throws JAXBException
     * @throws IOException
     */
    private static void safeGuardSMJK() {
        try {
            /** 检查SMJKR、SMJKA文件生成目录下是否生成了XML文件 * */
            File jkrFile = new File(nowPathSMJKR + osflag + SMJKR_XML_FILE_NAME);
            File jkaFile = new File(nowPathSMJKA + osflag + SMJKA_XML_FILE_NAME);
            if (!jkrFile.exists() || !jkaFile.exists()) {
                /** 如果两个目录有一个没有生成XML，就都要补传安全文件 * */
                logger.error("xml file not exists ...");
                dealWithSafeFileJK(getOrErrorFilePath(), "notGernerated", 0);
            } else {
                /** 验证SMJKR、SMJKA已生成文件 * */
                logger.info("this file is: " + jkrFile.getName());
                boolean resultSMJKR = FileValidator.validate(
                        jkrFile.toString(), nowPathSMJKR);
                logger.info("this file:" + jkrFile.getAbsolutePath()
                        + " result:" + resultSMJKR);
                // 验证SMJK
                logger.info("this file is: " + jkaFile.getName());
                boolean resultSMJKA = FileValidator.validate(
                        jkaFile.toString(), nowPathSMJKA);
                logger.info("this file:" + jkaFile.getAbsolutePath()
                        + " result:" + resultSMJKA);
                if (!resultSMJKR || !resultSMJKA) {
                    /** 如果两个XML有一个没有验证通过，就都要补传安全文件 * */
                    logger.error("xml validate error ...");
                    dealWithSafeFileJK(getOrErrorFilePath(), "validateError", 0);
                } else {
                    /** 校验成功，开始记录级别校验 * */
                    // 为避免重复读取，先把XML文件加载进来
                    Document docSMJKR = load(jkrFile.getAbsolutePath());
                    Document docSMJKA = load(jkaFile.getAbsolutePath());

                    // SMJKR
                    int sumSMJKR = getSumVal(docSMJKR);
                    int seqSMJKR = getSeqCountVal(docSMJKR);
                    // SMJKA
                    int sumSMJKA = getSumVal(docSMJKA);
                    int seqSMJKA = getSeqCountVal(docSMJKA);
                    // 检查两个文件 sum是否等于seq总数
                    if (sumSMJKR != seqSMJKR || sumSMJKA != seqSMJKA || sumSMJKA == 0 || sumSMJKR == 0) {
                        /** 如果两个XML有一个没有验证通过，就都要补传安全文件 * */
                        logger.error("sum data error ...");
                        dealWithSafeFileJK(getOrErrorFilePath(), "sumNotEqualSeq", sumSMJKR);
                    } else {
                        /** 开始两个XML联合校验 * */
                        boolean result = validateBetweenSMJK(docSMJKR,
                                docSMJKA, sumSMJKR, sumSMJKA);
                        logger.info("compare SMJKR and SMJKA result==:" + result);

                        if (!result) {
                            /** 如果没有验证通过，就都要补传安全文件 * */
                            dealWithSafeFileJK(getOrErrorFilePath(),
                                    "validateBetweenSMJKError", sumSMJKR);
                        } else {
                            uploadSMJK();
                        }
                    }
                }
            }
        } catch (Exception e) {
            // 即使报异常，也要保证文件能够正常上传到smp
            logger.error("safeGuardSMJK Method have Exception ...", e);

            // todo 此处有疑问，原始代码是把安全文件放到发送短信中，是否应当是生成文件？
            dealWithSafeFileJK(getOrErrorFilePath(), "programError", 0);
        }
    }

    private static String getOrErrorFilePath() {
        String jkrPath = nowPathSMJKR + osflag + SMJKR_XML_FILE_NAME;
        String jkaPath = nowPathSMJKA + osflag + SMJKA_XML_FILE_NAME;
        return "[" + jkrPath + "] or [" + jkaPath + "]";
    }

    /**
     * 金库申请与审批XML文件均用安全文件替换 <p/> todo 请处理发送短信，并检查短信内容
     *
     * @param errorFilePath 错误文件路径
     * @param errorMessage  短信中使用的错误信息
     */
    private static void dealWithSafeFileJK(String errorFilePath, String errorMessage, long total) {
        // todo 发送出现错误短信
        DBUtil.notice(tran.getProperty("a4File") + errorFilePath
                + tran.getProperty(errorMessage));

        // 处理SMJKR
//	replaceSafeSMJK("SMJKR",safeDayFilePath, nowPathSMJKR, SMJKR_XML_FILE_NAME);
        // todo 发送SMJKR替换成功短信
        String jkrPath = nowPathSMJKR + osflag + SMJKR_XML_FILE_NAME;
        DBUtil.notice(tran.getProperty("a4File") + jkrPath
                + tran.getProperty("replaceSuccess"));

        replaceSK(SMJKR_XML_FILE_NAME);
        // 处理SMJKA
//	replaceSafeSMJK("SMJKA",safeDayFilePath, nowPathSMJKA, SMJKA_XML_FILE_NAME);
        // todo 发送SMJKA替换成功短信
        String jkaPath = nowPathSMJKA + osflag + SMJKA_XML_FILE_NAME;
        DBUtil.notice(tran.getProperty("a4File") + jkaPath
                + tran.getProperty("replaceSuccess"));

        uploadSMJK();
        //将错误信息插入表中
        HashMap<String, String> dateMap = new HashMap<String, String>();
        dateMap.put("file_begin_time", TimeAndOtherUtil.getLastDayStartTimeStr());
        dateMap.put("file_end_time", TimeAndOtherUtil.getTodayStartTimeStr());
        dateMap.put("file_name", SMJKR_XML_FILE_NAME);
        dateMap.put("file_sum", total + "");
        dateMap.put("file_error_reason", tran.getProperty(errorMessage).trim().split("，")[0]);
        dateMap.put("file_upload_to_bomc", TimeAndOtherUtil.getCurrentDateTimeStr());
        SaveErrorFileUtil.saveErrorFile(dateMap);
    }

    private static void replaceSK(String SMJKR_XML_FILE_NAME) {
        try {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int today = calendar.get(Calendar.DAY_OF_MONTH);

            //SMJKA_371_01DY_20130401_000_000
            JKDayRandomSafeFileCreator jkDayRandomSafeFileCreator = new JKDayRandomSafeFileCreator(year, month, today);
            jkDayRandomSafeFileCreator.createJKSafeFile();
            // todo
        } catch (Exception e) {
            logger.error("replaceSK method have Exception ...", e);
            replaceBlankSK();
        }
    }

    private static void replaceBlankSK() {
        replaceSafeSMJK("SMJKR", safeDayFilePath, nowPathSMJKR, SMJKR_XML_FILE_NAME);
        replaceSafeSMJK("SMJKA", safeDayFilePath, nowPathSMJKA, SMJKA_XML_FILE_NAME);
    }

    /**
     * 替换为安全文件
     *
     * @param type            文件类型
     * @param safeDayFilePath 安全文件路径
     * @param generateDirPath 生成目录路径
     * @param xmlName         生成XML名称
     */
    private static void replaceSafeSMJK(String type, String safeDayFilePath,
                                        String generateDirPath, String xmlName) {
        try {
            File safeSMJK = new File(safeDayFilePath + osflag
                    + "SMJKR_371_01DY_20130519_000_000.xml");
            OperateFile.copyFile(safeSMJK, new File(generateDirPath + osflag
                    + xmlName));
            replaceXMLTypeAndDate(type, generateDirPath + osflag + xmlName);
        } catch (Exception e) {
            logger.error("replaceSafeSMJK Method have Exception ...", e);
        }
    }

    private static void touchXML(Document doc, String filePath) {
        /** 将document中的内容写入文件中 */
        XMLWriter writer = null;
        try {
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setNewLineAfterDeclaration(false);
            writer = new XMLWriter(new FileOutputStream(new File(filePath)),
                    format);
            writer.write(doc);
        } catch (Exception e) {
            logger.error("touchXML Method have Exception ...", e);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                logger.error(
                        "touchXML Method writer.close() have IOException ...",
                        e);
            }
        }
    }

    /**
     * 替换安全文件里面的时间
     *
     * @param filePath 文件路径
     */
    private static void replaceXMLTypeAndDate(String type, String filePath) {
        // todo 在你的安全文件里面设置以下三处的yyyyMMdd为toReplace，以便替换
        String key = "toReplace";

        Document doc = load(filePath);

        List list = doc.selectNodes("/smp/type");
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            Element typeElement = (Element) iterator.next();
            typeElement.setText(typeElement.getText().replace(key, type));
        }

        list = doc.selectNodes("/smp/createtime");
        iterator = list.iterator();
        while (iterator.hasNext()) {
            Element timeElement = (Element) iterator.next();
            timeElement.setText(timeElement.getText().replace(key, now_Date));
        }
        Calendar calendar = Calendar.getInstance();// 此时打印它获取的是系统当前时间
        calendar.add(Calendar.DATE, -1); // 得到前一天
        String yesterday = new SimpleDateFormat("yyyy-MM-dd").format(calendar
                .getTime());
        list = doc.selectNodes("/smp/begintime");
        iterator = list.iterator();
        while (iterator.hasNext()) {
            Element timeElement = (Element) iterator.next();
            timeElement.setText(timeElement.getText().replace(key, yesterday));
        }
        list = doc.selectNodes("/smp/endtime");
        iterator = list.iterator();
        while (iterator.hasNext()) {
            Element timeElement = (Element) iterator.next();
            timeElement.setText(timeElement.getText().replace(key, now_Date));
        }
        touchXML(doc, filePath);
    }

    private static void uploadSMJK() {
        try {
            File jkrFile = new File(nowPathSMJKR + osflag + SMJKR_XML_FILE_NAME);
            File jkaFile = new File(nowPathSMJKA + osflag + SMJKA_XML_FILE_NAME);

            OperateFile.copyFile(jkrFile, new File(UPLOAD_DIR_PATH + osflag
                    + SMJKR_XML_FILE_NAME));
            logger.info("copy SMJKR to upload!!!");
            OperateFile.copyFile(jkaFile, new File(UPLOAD_DIR_PATH + osflag
                    + SMJKA_XML_FILE_NAME));
            logger.info("copy SMJKA to upload!!!");

            // 为保持生成文件中只有一个文件，进行删除
            OperateFile.deleteFile(jkrFile.getAbsolutePath());
            OperateFile.deleteFile(jkaFile.getAbsolutePath());
        } catch (Exception e) {
            logger.error("uploadSMJK Method have Exception ...", e);
        }
    }

    private static Document load(String filename) {
        Document document = null;
        try {
            SAXReader saxReader = new SAXReader();
            document = saxReader.read(new BufferedReader(new InputStreamReader(
                    new FileInputStream(filename), "UTF-8")));
        } catch (Exception e) {
            logger.error("load Method have Exception ...", e);
        }
        return document;
    }

    // 得到XML中的 sum 的值
    private static int getSumVal(Document doc) {
        List list = doc.selectNodes("/smp/sum");
        Element sumElement = (Element) list.iterator().next();
        return Integer.valueOf(sumElement.getText());
    }

    // 得到XML中的 seqCount 的值
    private static int getSeqCountVal(Document doc) {
        List list = doc.selectNodes("/smp/data/rcd/seq");
        return list.size();
    }

    /**
     * 联合SMJKR和SMJKA进行校验
     * 1、校验SMJKR和SMJKA数据量是否相等
     * 2、校验SMJKR中的开始时间和结束时间是否在6小时内
     * 3、校验SMJKR中时间跨度超过两小时的占总数的百分比是否大于10%
     * 4、校验SMJKR的申请ID是否都包含在SMJKA中
     *
     * @param docSMJKR SMJKR XML
     * @param docSMJKA SMJKA XML
     * @param sumSMJKR SMJKR sum
     * @param sumSMJKA SMJKA sum
     * @return boolean
     * @throws ParseException
     */
    private static boolean validateBetweenSMJK(Document docSMJKR, Document docSMJKA, int sumSMJKR, int sumSMJKA)
            throws ParseException {
        // 1、校验SMJKR和SMJKA数据量是否相等
        if (sumSMJKR != sumSMJKA) {
            logger.error("validate between SMJKR and SMJKA found sumSMJKR != sumSMJKA!");
            return false;
        }

        Set<String> requestIdSetSMJKA = new HashSet<String>();
        List requestIdListSMJKA = docSMJKA.selectNodes("/smp/data/rcd/requestid");
        for (Object o : requestIdListSMJKA) {
            Element idElement = (Element) o;
            String id = idElement.getText();
            requestIdSetSMJKA.add(id);
        }

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

            // 2、校验SMJKR中的开始时间和结束时间是否在6小时内
            if ((end.getTime() - begin.getTime()) > 21600000) {
                logger.error("validate between SMJKR and SMJKA found endtime - begintime > 6 hour! requestid is : " + id);
                return false;
            }
            //3、校验SMJKR中时间跨度超过两小时的占总数的百分比是否大于10%
            if ((end.getTime() - begin.getTime()) > 7200000) {
                count = count + 1;
            }


            // 4、校验SMJKR的申请ID是否都包含在SMJKA中
            if (!requestIdSetSMJKA.contains(id)) {
                logger.error("validate between SMJKR and SMJKA found requestid not in SMJKA! requestid is : " + id);
                return false;
            }
        }
        float ratio = count / (float) sumSMJKR;
        if (ratio > 0.1) {
            logger.error("validate SMJKR  endtime - begintime > 2 hour over total 10%! ratio is : " + ratio);
            return false;
        }
        return true;
    }
}
