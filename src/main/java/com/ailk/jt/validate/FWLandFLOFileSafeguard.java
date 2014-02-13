package com.ailk.jt.validate;

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

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: wangpu
 * Date: 13-5-21
 * Time: 上午11:31
 *
 * 防火墙单点登录、防火墙操作XML保障
 * 仿照原始代码进行编写，请搬移到原始代码中进行测试
 */
public final class FWLandFLOFileSafeguard {
    private static Logger logger = LoggerFactory.getLogger(FWLandFLOFileSafeguard.class);

    // todo 短信信息
    private static final Properties tran = PropertiesUtil.getProperties("/tran.properties");

    // 获取SMFWL文件生成目录
    private static final String nowPathSMFWL = PropertiesUtil.getValue("uap_file_uapload_for_smfwl_db_now");
    // 获取SMFLO文件生成目录
    private static final String nowPathSMFLO = PropertiesUtil.getValue("uap_file_uapload_for_smflo_db_now");
    // 获取上传目录
    private static final String UPLOAD_DIR_PATH = PropertiesUtil.getValue("uap_file_uapload");
    // 获取数据为0安全文件路径
    private static final String safeDayFilePath = PropertiesUtil.getValue("uap_file_uapload_for_day_dir_safe");

    private static String now_Date = "";

    // 文件分隔符
    private static final String osflag = PropertiesUtil.getValue("os_flag");

    // SMFWL文件名称
    private static String SMFWL_XML_FILE_NAME = "";
    // SMFLO文件名称
    private static String SMFLO_XML_FILE_NAME = "";

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
            SMFWL_XML_FILE_NAME = "SMFWL_371_01DY_" + rightTime + "_000_000.xml";
            logger.info("SMFWL_XML_FILE_NAME = " + SMFWL_XML_FILE_NAME);
            SMFLO_XML_FILE_NAME = "SMFLO_371_01DY_" + rightTime + "_000_000.xml";
            logger.info("SMFLO_XML_FILE_NAME = " + SMFLO_XML_FILE_NAME);

            // 处理SMFWL文件和SMFLO文件
            safeGuardFWLandFLO();
        } catch (Exception e) {
            logger.error("FWLandFLOFileSafeguard Class have Exception ...", e);
        }
    }

    /**
     * 防火墙单点登录、防火墙操作XML文件验证保障
     */
    private static void safeGuardFWLandFLO() {
        try {
            /** 检查SMFWL、SMFLO文件生成目录下是否生成了XML文件 **/
            File fwlFile = new File(nowPathSMFWL + osflag + SMFWL_XML_FILE_NAME);
            File floFile = new File(nowPathSMFLO + osflag + SMFLO_XML_FILE_NAME);
            if (!fwlFile.exists() || !floFile.exists()) {
                /** 如果两个目录有一个没有生成XML，就都要补传安全文件 **/
                logger.error("xml file not exists ...");
                dealWithSafeFileFWLandFLO(getOrErrorFilePath(), "notGernerated", 0);
            } else {
                /** 验证SMFWL、SMFLO已生成文件 **/
                logger.info("this file is: " + fwlFile.getName());
                // todo 搬移时，请替换原始代码的XML验证（本程序对原始代码中的XML验证进行了分析和优化，以备后用）
                // 验证SMFWL
                boolean resultSMFWL = FileValidator.validate(
			fwlFile.toString(), nowPathSMFWL);
                logger.info("validate file:" + fwlFile.getAbsolutePath() + " result:" + resultSMFWL);
                // 验证SMFLO
                boolean resultSMFLO = FileValidator.validate(
			floFile.toString(), nowPathSMFLO);;
                logger.info("validate file:" + floFile.getAbsolutePath() + " result:" + resultSMFLO);
                if (!resultSMFWL || !resultSMFLO) {
                    /** 如果两个XML有一个没有验证通过，就都要补传安全文件 **/
                    logger.error("xml validate error ...");
                    dealWithSafeFileFWLandFLO(getOrErrorFilePath(), "validateError", 0);
                } else {
                    /** 校验成功，开始记录级别校验 **/
                    // 为避免重复读取，先把XML文件加载进来
                    Document docSMFWL = load(fwlFile.getAbsolutePath());
                    Document docSMFLO = load(floFile.getAbsolutePath());

                    // SMFWL
                    int sumSMFWL = getSumVal(docSMFWL);
                    int seqSMFWL = getSeqCountVal(docSMFWL);
                    // SMFLO
                    int sumSMFLO = getSumVal(docSMFLO);
                    int seqSMFLO = getSeqCountVal(docSMFLO);
                    // 检查两个文件 sum是否等于seq总数，sum是否为0
                    if (sumSMFWL != seqSMFWL || sumSMFLO != seqSMFLO) {
                        /** 如果两个XML有一个没有验证通过，就都要补传安全文件 **/
                        logger.error("sum data error ...");
                        dealWithSafeFileFWLandFLO(getOrErrorFilePath(), "sumNotEqualSeq", sumSMFWL);
                    } else {
                        /** 开始两个XML联合校验 **/
                        boolean result = validateBetweenFWLandFLO(docSMFWL, docSMFLO);
                        if (!result) {
                            /** 如果没有验证通过，就都要补传安全文件 **/
                            dealWithSafeFileFWLandFLO(getOrErrorFilePath(), "validateBetweenFWLandFLOError", sumSMFWL);
                        } else {
                            upload();
                        }
                    }
                }
            }
        } catch (Exception e) {
            // 即使报异常，也要保证文件能够正常上传到smp
            logger.error("safeGuardFWLandFLO Method have Exception ...", e);

            // todo 此处有疑问，原始代码是把安全文件放到发送短信中，是否应当是生成文件？
            dealWithSafeFileFWLandFLO(getOrErrorFilePath(), "programError", 0);
        }
    }

    private static String getOrErrorFilePath() {
        String fwlPath = nowPathSMFWL + osflag + SMFWL_XML_FILE_NAME;
        String floPath = nowPathSMFLO + osflag + SMFLO_XML_FILE_NAME;
        return "[" + fwlPath + "] or [" + floPath + "]";
    }

    /**
     * 防火墙单点登录、防火墙操作XML文件均用安全文件替换
     * <p/>
     * todo 请处理发送短信，并检查短信内容
     *
     * @param errorFilePath 错误文件路径
     * @param errorMessage  短信中使用的错误信息
     * @param total 总数
     */
    private static void dealWithSafeFileFWLandFLO(String errorFilePath, String errorMessage, long total) {
        // todo 发送出现错误短信
        DBUtil.notice(tran.getProperty("a4File") + errorFilePath + tran.getProperty(errorMessage));

        // 处理SMFWL
        replaceSafe("SMFWL", nowPathSMFWL, SMFWL_XML_FILE_NAME);
        // todo 发送SMFWL替换成功短信
        String fwlPath = nowPathSMFWL + osflag + SMFWL_XML_FILE_NAME;
        DBUtil.notice(tran.getProperty("a4File") + fwlPath + tran.getProperty("replaceSuccess"));

        // 处理SMFLO
        replaceSafe("SMFLO", nowPathSMFLO, SMFLO_XML_FILE_NAME);
        // todo 发送SMFLO替换成功短信
        String floPath = nowPathSMFLO + osflag + SMFLO_XML_FILE_NAME;
        DBUtil.notice(tran.getProperty("a4File") + floPath + tran.getProperty("replaceSuccess"));

        upload();

        // todo 将错误信息插入表中
        HashMap<String,String> dateMap = new HashMap<String,String>();
        dateMap.put("file_begin_time", TimeAndOtherUtil.getLastDayStartTimeStr());
        dateMap.put("file_end_time", TimeAndOtherUtil.getTodayStartTimeStr());
        dateMap.put("file_name", SMFWL_XML_FILE_NAME);
        dateMap.put("file_sum", String.valueOf(total));
        dateMap.put("file_error_reason", tran.getProperty(errorMessage).trim().split("，")[0]);
        dateMap.put("file_upload_to_bomc", TimeAndOtherUtil.getCurrentDateTimeStr());
        SaveErrorFileUtil.saveErrorFile(dateMap);
    }

    /**
     * 替换为安全文件
     *
     * @param type            XML文件类型
     * @param generateDirPath 生成目录路径
     * @param xmlName         生成XML名称
     */
    private static void replaceSafe(String type, String generateDirPath, String xmlName) {
        try {
            File safe = new File(safeDayFilePath+ osflag +"SMFWL_371_01DY_20130521_000_000.xml");
            OperateFile.copyFile(safe, new File(generateDirPath + osflag + xmlName));
            replaceXMLTypeAndDate(generateDirPath + osflag + xmlName, type);
        } catch (Exception e) {
            logger.error("replaceSafe Method have Exception ...", e);
        }
    }

    private static void touchXML(Document doc, String filePath) {
        /** 将document中的内容写入文件中 */
        XMLWriter writer = null;
        try {
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setNewLineAfterDeclaration(false);
            writer = new XMLWriter(new FileOutputStream(new File(
                    filePath)), format);
            writer.write(doc);
        } catch (Exception e) {
            logger.error("touchXML Method have Exception ...", e);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                logger.error("touchXML Method writer.close() have IOException ...", e);
            }
        }
    }

    /**
     * 替换安全文件里面的类型和时间
     *
     * @param filePath 文件路径
     * @param type 类型
     */
    private static void replaceXMLTypeAndDate(String filePath, String type) {
        // todo 在你的安全文件里面设置以下一处的XML文件类型为toReplace，以便替换
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
        String yesterday = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
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

    private static void upload() {
        try {
            File fwlFile = new File(nowPathSMFWL + osflag + SMFWL_XML_FILE_NAME);
            File floFile = new File(nowPathSMFLO + osflag + SMFLO_XML_FILE_NAME);

            OperateFile.copyFile(fwlFile, new File(UPLOAD_DIR_PATH + osflag + SMFWL_XML_FILE_NAME));
            logger.info("copy SMFWL to upload!!!");
            OperateFile.copyFile(floFile, new File(UPLOAD_DIR_PATH + osflag + SMFLO_XML_FILE_NAME));
            logger.info("copy SMFLO to upload!!!");

            // 为保持生成文件中只有一个文件，进行删除
            OperateFile.deleteFile(fwlFile.getAbsolutePath());
            OperateFile.deleteFile(floFile.getAbsolutePath());
        } catch (Exception e) {
            logger.error("upload Method have Exception ...", e);
        }
    }

    private static Document load(String filename) {
        Document document = null;
        try {
            SAXReader saxReader = new SAXReader();
            document = saxReader.read(new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8")));
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
     * 联合SMFWL和SMFLO进行校验
     * 1、校验SMFWL的申请ID是否都包含在SMFLO中
     *
     * @param docSMFWL SMFWL XML
     * @param docSMFLO SMFLO XML
     * @return boolean
     * @throws java.text.ParseException
     */
    private static boolean validateBetweenFWLandFLO(Document docSMFWL, Document docSMFLO)
            throws ParseException {
        Set<String> sessionIdSetSMFWL = new HashSet<String>();
        List sessionIdListSMFWL = docSMFWL.selectNodes("/smp/data/rcd/loginsessionid");
        for (Object o : sessionIdListSMFWL) {
            Element idElement = (Element) o;
            String id = idElement.getText();
            sessionIdSetSMFWL.add(id);
        }

        Set<String> sessionIdSetSMFLO = new HashSet<String>();
        List sessionIdListSMFLO = docSMFLO.selectNodes("/smp/data/rcd/loginsessionid");
        for (Object o : sessionIdListSMFLO) {
            Element idElement = (Element) o;
            String id = idElement.getText();
            sessionIdSetSMFLO.add(id);
        }

        /*for (String idFWL : sessionIdSetSMFWL) {
            if (!sessionIdSetSMFLO.contains(idFWL)) {
                logger.error("validate between SMFWL and SMFLO found loginsessionid not in SMFLO! loginsessionid is : " + idFWL);
                return false;
            }
        }*/

        for (String idFOL : sessionIdSetSMFLO) {
            if (!sessionIdSetSMFWL.contains(idFOL)) {
                logger.error("validate between SMFWL and SMFLO found loginsessionid not in SMFWL! loginsessionid is : " + idFOL);
                return false;
            }
        }

        return true;
    }
}
