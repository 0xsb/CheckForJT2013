package com.ailk.jt.staticfile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.ailk.check.utils.FileUtil;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.ailk.jt.util.DateUtil;
import com.ailk.jt.util.PropertiesUtil;
import com.ailk.jt.validate.OperateFile;

/** 2013-07-11 更改 作为 SMMAL static safe file to BOMC **/
public class MainAcctLoginFile {
    private static Logger log = Logger.getLogger(MainAcctLoginFile.class); // 获取打印日志工具类对象

    private static String right_Date = PropertiesUtil.getValue("right_Date").trim();
    private static String right_Datebegin = PropertiesUtil.getValue("right_Datebegin").trim();
    private static String now_Date = "";
    private static String createPath = PropertiesUtil.getValue("uap_file_uapload_for_init_static");
    private static String uploadPath = PropertiesUtil.getValue("uap_file_uapload_hour");
    private static String osflag = PropertiesUtil.getValue("os_flag");

    // 针对主账号小时文件
    private static String rightPathSMMAL = PropertiesUtil.getValue("uap_file_uapload_for_smmal_dir_safe");
    private static String rightFirstStrSMMAL = "SMMAL_371_01HR_" + right_Date.replace("-", "") + "_";

    public static void main(String[] args) {
        now_Date = DateUtil.ymdToStr();

        log.info("create static SMMAL begin--------------------------------------------------------------");
        dealSMMAL();
        log.info("create static SMMAL end--------------------------------------------------------------");
    }


    /**
     * @Title: dealSMMAL
     * @Description: 校验SMMAL文件
     */
    private static void dealSMMAL() {
        SMMALFileNotExits();
    }

    /**
     * @Title: SMMALFileNotExits
     * @Description: 主账号小时文件校验失败，或者根本没有生产使用此方法
     */
    private static void SMMALFileNotExits() {
        // 如果找不到文件
        try {
            Calendar calendar = Calendar.getInstance();// 此时打印它获取的是系统当前时间
            String hourFlag = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
            // 找到文件
            String filePath;
            if (hourFlag.equals("0")) {
                filePath = rightPathSMMAL + osflag + rightFirstStrSMMAL + "024_000.xml";
            } else {
                if (hourFlag.length() < 2) {
                    hourFlag = "0" + hourFlag;
                }
                filePath = rightPathSMMAL + osflag + rightFirstStrSMMAL + "0"
                        + hourFlag + "_000.xml";
            }
            File safeFileNameSMMAL = new File(filePath);
            // 将相应正确路径的文件 直接改变成当前名字 拷贝到 上传路径
            String rightFileStr = "SMMAL.xml";

            OperateFile.copyFile(safeFileNameSMMAL, new File(createPath + osflag + rightFileStr));

            // 将新文件 中的时间全部改成 当前日期时间
            changeXMLDate(createPath + osflag + rightFileStr);

            FileUtil.moveToDir(createPath + osflag + rightFileStr, uploadPath);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    // 载入一个xml文档
    public static Document load(String filename) {
        Document document = null;
        try {
            SAXReader saxReader = new SAXReader();
            document = saxReader.read(new BufferedReader(new InputStreamReader(
                    new FileInputStream(filename), "UTF-8")));

        } catch (Exception ex) {
            log.error("create static SMMAL error : load()", ex);
        }
        return document;
    }

    public static void changeXMLDate(String filePath) {
        Document doc = load(filePath);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        Date date = calendar.getTime();
        String lastday = new SimpleDateFormat("yyyy-MM-dd").format(date);

        List list = doc.selectNodes("/smp/createtime");
        Iterator iter = list.iterator();
        while (iter.hasNext()) {
            Element timeElement = (Element) iter.next();
            timeElement.setText(timeElement.getText().replace(right_Date, now_Date));
        }
        list = doc.selectNodes("/smp/begintime");
        iter = list.iterator();
        while (iter.hasNext()) {
            Element timeElement = (Element) iter.next();
            if (timeElement.getText().trim().contains(right_Datebegin)) {
                timeElement.setText(timeElement.getText().replace(right_Datebegin, lastday));
            } else {
                timeElement.setText(timeElement.getText().replace(right_Date, now_Date));
            }
        }
        list = doc.selectNodes("/smp/endtime");
        iter = list.iterator();
        while (iter.hasNext()) {
            Element timeElement = (Element) iter.next();
            timeElement.setText(timeElement.getText().replace(right_Date, now_Date));
        }

        list = doc.selectNodes("/smp/data/rcd");
        iter = list.iterator();
        while (iter.hasNext()) {
            Element rcdElement = (Element) iter.next();
            Iterator ltIter = rcdElement.elementIterator("logintime");
            while (ltIter.hasNext()) {
                Element ltElement = (Element) ltIter.next();
                if (ltElement.getText().trim().contains(right_Datebegin)) {
                    ltElement.setText(ltElement.getText().replace(right_Datebegin, lastday));
                } else {
                    ltElement.setText(ltElement.getText().replace(right_Date, now_Date));
                }
            }
        }

        Node sum = (Node) doc.selectObject("/smp/sum");
        sum.setText(String.valueOf(list.size()));
        createXML(doc, filePath);
    }

    public static void createXML(Document doc, String filePath) {
        /** 将document中的内容写入文件中 */
        try {
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setNewLineAfterDeclaration(false);
            XMLWriter writer = new XMLWriter(new FileOutputStream(new File(
                    filePath)), format);
            writer.write(doc);
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}